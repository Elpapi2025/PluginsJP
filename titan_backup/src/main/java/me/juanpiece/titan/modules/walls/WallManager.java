package me.juanpiece.titan.modules.walls;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.claims.Claim;
import me.juanpiece.titan.modules.teams.type.*;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.modules.walls.listener.WallListener;
import me.juanpiece.titan.modules.walls.task.WallTask;
import me.juanpiece.titan.utils.NameThreadFactory;
import me.juanpiece.titan.utils.Tasks;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("deprecation")
public class WallManager extends Manager {

    private final Map<UUID, Set<Block>> walls;
    private final Map<UUID, Set<Block>> teamMaps;
    private final Map<UUID, Set<Claim>> lunar;
    private final List<Material> materialList;

    private final Set<UUID> endSafezoneDenied;
    private final Set<UUID> netherSafezoneDenied;

    private final ScheduledExecutorService executor;

    public WallManager(HCF instance) {
        super(instance);

        this.walls = new ConcurrentHashMap<>();
        this.teamMaps = new ConcurrentHashMap<>();
        this.lunar = new ConcurrentHashMap<>();
        this.materialList = new ArrayList<>();

        this.endSafezoneDenied = new HashSet<>();
        this.netherSafezoneDenied = new HashSet<>();

        this.executor = Executors.newScheduledThreadPool(2, new NameThreadFactory("Titan - Wall Thread"));
        this.executor.scheduleAtFixedRate(new WallTask(this), 0L, 200L, TimeUnit.MILLISECONDS);

        this.load();

        new WallListener(this);
    }

    @Override
    public void disable() {
        executor.shutdown();
    }

    private void load() {
        for (Material material : Material.values()) {
            if (material.isBlock() && material.isSolid()) {
                materialList.add(material);
            }
        }
    }

    public void sendWall(Player player, Claim cuboid, WallType type) {
        // Make sure we create a wall if they don't already have one
        if (!walls.containsKey(player.getUniqueId())) walls.put(player.getUniqueId(), new HashSet<>());

        if (!getInstance().getClientHook().getClients().isEmpty() && LunarClientAPI.getInstance().isRunningLunarClient(player)) {
            if (!lunar.containsKey(player.getUniqueId())) lunar.put(player.getUniqueId(), new HashSet<>());

            Set<Claim> teams = lunar.get(player.getUniqueId());

            if (!teams.contains(cuboid)) {
                teams.add(cuboid);
                Tasks.execute(this, () -> getInstance().getClientHook().sendBorderPacket(player, cuboid, type.getLunarColor()));
            }
            return;
        }

        Set<Block> blocks = walls.get(player.getUniqueId());
        int blockY = player.getLocation().getBlockY();

        for (Block wall : cuboid.getWalls(blockY, blockY)) {
            if (wall.getLocation().distanceSquared(player.getLocation()) <= 49) {
                for (int i = 1; i <= 4; i++) {
                    // Upwards
                    Location toSendPositive = wall.getLocation().clone().add(0, i, 0);
                    Block blockPositive = toSendPositive.getBlock();
                    sendBlock(player, toSendPositive, blockPositive, type, blocks);
                    // Downwards
                    Location toSendNegative = wall.getLocation().clone().subtract(0, i, 0);
                    Block blockNegative = toSendNegative.getBlock();
                    sendBlock(player, toSendNegative, blockNegative, type, blocks);
                    // Middle
                    sendBlock(player, wall.getLocation(), wall, type, blocks);
                }
            }
        }
    }

    private void sendBlock(Player player, Location location, Block block, WallType type, Set<Block> blocks) {
        // Don't want to be sending block changes in unloaded chunks.
        if (!block.getChunk().isLoaded()) return;

        // We do not want to replace solid blocks rather grass, flowers etc.
        if (block.getType().isSolid()) return;

        // Send the change and make sure we add the block
        player.sendBlockChange(location, type.getMaterial(), type.getData());
        blocks.add(block);
    }

    public boolean isEndSafezoneDenied(Player player) {
        return endSafezoneDenied.contains(player.getUniqueId());
    }

    public boolean isNetherSafezoneDenied(Player player) {
        return netherSafezoneDenied.contains(player.getUniqueId());
    }

    public void clearWalls(Player player) {
        Set<Block> blocks = walls.get(player.getUniqueId());

        if (blocks != null && !blocks.isEmpty()) {
            // Iterators performance with array list is absolute shit
            // https://stackoverflow.com/questions/33182102/difference-in-lambda-performances
            blocks.removeIf(b -> {
                if (player.getWorld() != b.getWorld()) {
                    player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                    return true;
                }

                Claim claim = getInstance().getTeamManager().getClaimManager().getClaim(b.getLocation());

                if (claim == null) {
                    player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                    return true;
                }

                Team team = getInstance().getTeamManager().getTeam(claim.getTeam());

                if (team == null || getWallType(claim, team, player) == null) {
                    player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                    return true;
                }

                if (b.getLocation().distanceSquared(player.getLocation()) > 54) {
                    player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                    return true;
                }

                return false;
            });
        }
    }

    public void clearLunarBorders(Player player, Set<Claim> nearby) {
        Set<Claim> claims = lunar.get(player.getUniqueId());

        if (claims != null) {
            claims.removeIf(claim -> {
                if (nearby.isEmpty() || !nearby.contains(claim)) {
                    Tasks.execute(this, () -> getInstance().getClientHook().sendRemoveBorderPacket(player, claim.getTeam()));
                    return true;
                }

                Team team = getInstance().getTeamManager().getTeam(claim.getTeam());

                if (team == null || getWallType(claim, team, player) == null) {
                    Tasks.execute(this, () -> getInstance().getClientHook().sendRemoveBorderPacket(player, claim.getTeam()));
                    return true;
                }

                return false;
            });
        }
    }

    public void sendPillar(Player player, Location location) {
        sendPillar(player, Material.DIAMOND_BLOCK, location);
    }

    public void clearPillar(Player player, Location location) {
        if (location == null) return;

        for (int i = 0; i < location.getWorld().getMaxHeight(); i++) {
            Location clone = location.clone();
            clone.setY(i);
            Block block = clone.getBlock();
            player.sendBlockChange(
                    clone,
                    block.getType(),
                    block.getData()
            );
        }
    }

    public void sendTeamMap(Player player) {
        // Create new team map
        if (!teamMaps.containsKey(player.getUniqueId())) teamMaps.put(player.getUniqueId(), new HashSet<>());

        int teamMapRadius = getTeamConfig().getInt("TEAM_MAP.RADIUS");

        Set<Claim> nearby = getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), teamMapRadius);
        Set<Block> blocks = teamMaps.get(player.getUniqueId());
        List<String> msg = new ArrayList<>();

        if (nearby.isEmpty()) {
            getInstance().getUserManager().getByUUID(player.getUniqueId()).setClaimsShown(false);
            player.sendMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.NO_TEAMS")
                    .replace("%radius%", String.valueOf(teamMapRadius))
            );
            return;
        }

        int i = 0;

        // Loop through teams in a certain radius
        for (Claim cuboid : nearby) {
            Team team = getInstance().getTeamManager().getTeam(cuboid.getTeam());
            Material material = materialList.get(i);

            // Loop through corners and send pillar
            for (Block block : cuboid.getCornerBlocks()) {
                Location clone = block.getLocation().clone();
                clone.setY(player.getLocation().getBlockY());
                sendPillar(player, material, clone);
                blocks.add(block);
            }

            i++;
            msg.add(getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.CLAIM_FORMAT")
                    .replace("%material%", material.name())
                    .replace("%team%", team.getDisplayName(player))
            );
        }

        // Send all the claim info we just sent pillars for
        for (String string : getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_MAP.MAP_SHOWN")) {
            if (!string.equalsIgnoreCase("%claims%")) {
                player.sendMessage(string);
                continue;
            }

            for (String materials : msg) player.sendMessage(materials);
            msg.clear(); // clear ram
        }
    }

    public void clearTeamMap(Player player) {
        Set<Block> blocks = teamMaps.remove(player.getUniqueId());

        if (blocks == null) return;

        for (Block block : blocks) {
            clearPillar(player, block.getLocation());
        }

        blocks.clear();
    }

    public WallType getWallType(Claim claim, Team team, Player player) {
        TimerManager timerManager = getInstance().getTimerManager();

        if (timerManager.getCombatTimer().hasTimer(player) && team instanceof SafezoneTeam)
            return WallType.COMBAT_TAG;

        if (timerManager.getPvpTimer().checkEntry(player, team))
            return WallType.PVP_TIMER;

        if (timerManager.getInvincibilityTimer().checkEntry(player, team))
            return WallType.INVINCIBILITY;

        if (endSafezoneDenied.contains(player.getUniqueId()) || netherSafezoneDenied.contains(player.getUniqueId()))
            return WallType.COMBAT_TAG;

        if (getInstance().getSotwManager().isActive() && !player.hasPermission("titan.lockclaim.bypass")
                && !getInstance().getSotwManager().getEnabled().contains(player.getUniqueId())) {
            if (team instanceof PlayerTeam) {
                PlayerTeam pt = (PlayerTeam) team;

                if (claim.isLocked() && !pt.getPlayers().contains(player.getUniqueId())) {
                    return WallType.LOCKED_CLAIM;
                }
            }
        }

        if (Config.TEAM_EVENT_ENTER_LIMIT > 0 && !player.hasPermission("titan.event.entry") && team instanceof EventTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
            if (pt == null) return WallType.EVENT_DENIED;
            if (pt.getOnlinePlayersSize(false) < Config.TEAM_EVENT_ENTER_LIMIT) return WallType.EVENT_DENIED;
            return null; // no use checking below so just return null, a team can't be an event and citadel
        }

        if (Config.TEAM_CITADEL_ENTER_LIMIT > 0 && !player.hasPermission("titan.citadel.entry") && team instanceof CitadelTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
            if (pt == null) return WallType.CITADEL_DENIED;
            if (pt.getOnlinePlayersSize(false) < Config.TEAM_CITADEL_ENTER_LIMIT) return WallType.CITADEL_DENIED;
        }

        if (Config.TEAM_CONQUEST_ENTER_LIMIT > 0 && !player.hasPermission("titan.conquest.entry") && team instanceof ConquestTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
            if (pt == null) return WallType.CONQUEST_DENIED;
            if (pt.getOnlinePlayersSize(false) < Config.TEAM_CONQUEST_ENTER_LIMIT) return WallType.CONQUEST_DENIED;
        }

        return null;
    }

    private void sendPillar(Player player, Material material, Location location) {
        Location clone = location.clone();

        for (int i = 0; i < 256; i++) {
            clone.setY(i);
            Material type = clone.getBlock().getType();

            if (!type.isTransparent()) continue;
            if (type.isSolid() && !type.name().contains("WATER")) continue;

            // Need to limit the height otherwise it's going to lag the client on 1.16
            if (Utils.isModernVer() && i >= location.getBlockY() + 20) break;

            // If multiple of 5 send the different material
            if (i % 5 == 0) {
                player.sendBlockChange(
                        clone,
                        material,
                        (byte) 0
                );
                continue;
            }

            player.sendBlockChange(
                    clone,
                    Material.GLASS,
                    (byte) 0
            );
        }
    }
}