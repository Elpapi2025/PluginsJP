package me.titan.core.modules.walls;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.claims.Claim;
import me.titan.core.modules.walls.listener.WallListener;
import me.titan.core.modules.walls.thread.WallThread;
import me.titan.core.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WallManager extends Manager {
    private final Map<UUID, List<Block>> walls;
    private final Map<UUID, List<Block>> teamMaps;
    private final List<Material> materialList;
    
    public WallType getWallType(Player player) {
        if (this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            return WallType.COMBAT_TAG;
        }
        if (this.getInstance().getTimerManager().getPvpTimer().hasTimer(player)) {
            return WallType.PVP_TIMER;
        }
        if (this.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(player)) {
            return WallType.INVINCIBILITY;
        }
        return null;
    }
    
    public void clearWalls(Player player) {
        List<Block> walls = this.walls.get(player.getUniqueId());
        if (walls == null) {
            return;
        }
        if (walls.isEmpty()) {
            return;
        }
        Iterator<Block> iterator = walls.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (this.getWallType(player) != null) {
                if (block.getLocation().distance(player.getLocation()) <= 7.0) {
                    continue;
                }
            }
            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
            iterator.remove();
        }
    }
    
    public void clearTeamMap(Player player) {
        List<Block> maps = this.teamMaps.get(player.getUniqueId());
        if (maps == null) {
            return;
        }
        for (Block block : maps) {
            this.clearPillar(player, block.getLocation());
        }
        maps.clear();
        this.teamMaps.remove(player.getUniqueId());
    }
    
    public void sendWall(Player player, Claim claim, boolean bol) {
        if (!this.getWalls().containsKey(player.getUniqueId())) {
            this.getWalls().put(player.getUniqueId(), new ArrayList<>());
        }
        List<Block> walls = this.walls.get(player.getUniqueId());
        for (Block block : claim.getWalls(player.getLocation().getBlockY() - 3, player.getLocation().getBlockY() + 3)) {
            if (block.getLocation().distance(player.getLocation()) <= 7.0) {
                if (!block.getChunk().isLoaded()) {
                    continue;
                }
                if (block.getType().isSolid()) {
                    continue;
                }
                walls.remove(block);
                walls.add(block);
                this.sendBlockChange(player, block.getLocation(), bol);
            }
        }
    }
    
    public void sendPillar(Player player, Location location) {
        this.sendPillar(player, Material.DIAMOND_BLOCK, location);
    }
    
    private void sendPillar(Player player, Material material, Location location) {
        for (int i = 0; i < 256; ++i) {
            Location fLoc = location.clone();
            fLoc.setY(i);
            if (!location.getBlock().getType().isSolid()) {
                if (!location.getBlock().getType().isBlock()) {
                    if (this.getInstance().getVersionManager().isVer16() && i >= location.getBlockY() + 20) {
                        break;
                    }
                    if (i % 5 == 0) {
                        player.sendBlockChange(fLoc, material, (byte)0);
                    }
                    else {
                        player.sendBlockChange(fLoc, Material.GLASS, (byte)0);
                    }
                }
            }
        }
    }
    
    public WallManager(HCF plugin) {
        super(plugin);
        this.walls = new ConcurrentHashMap<>();
        this.teamMaps = new ConcurrentHashMap<>();
        this.materialList = new ArrayList<>();
        this.load();
        new WallListener(this);
        new WallThread(this);
    }
    
    private void load() {
        for (Material material : Material.values()) {
            if (!material.isBlock() || !material.isSolid()) continue;
            this.materialList.add(material);
        }
    }
    
    public void sendBlockChange(Player player, Location location, boolean bol) {
        if (!bol) {
            player.sendBlockChange(location, ItemUtils.getMat(this.getConfig().getString("WALLS." + this.getWallType(player).name() + ".TYPE")), (byte)this.getConfig().getInt("WALLS." + this.getWallType(player).name() + ".DATA"));
        }
        else {
            player.sendBlockChange(location, ItemUtils.getMat(this.getConfig().getString("WALLS.LOCKED_CLAIM.TYPE")), (byte)this.getConfig().getInt("WALLS.LOCKED_CLAIM.DATA"));
        }
    }
    
    public void sendTeamMap(Player player) {
        if (!this.teamMaps.containsKey(player.getUniqueId())) {
            this.teamMaps.put(player.getUniqueId(), new ArrayList<>());
        }
        int radius = this.getTeamConfig().getInt("TEAM_MAP.RADIUS");
        List<Claim> nearbyCuboids = this.getInstance().getTeamManager().getClaimManager().getNearbyCuboids(player.getLocation(), radius);
        ArrayList<String> list = new ArrayList<>();
        if (nearbyCuboids.isEmpty()) {
            this.getInstance().getUserManager().getByUUID(player.getUniqueId()).setClaimsShown(false);
            player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.NO_TEAMS").replaceAll("%radius%", String.valueOf(radius)));
            return;
        }
        int i = 0;
        for (Claim claim : nearbyCuboids) {
            Team team = this.getInstance().getTeamManager().getTeam(claim.getTeam());
            Material material = this.materialList.get(i);
            for (Location corner : claim.getCornerLocations()) {
                this.sendPillar(player, material, corner);
                this.getTeamMaps().get(player.getUniqueId()).add(corner.getBlock());
            }
            ++i;
            list.add(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.CLAIM_FORMAT").replaceAll("%material%", material.name()).replaceAll("%team%", team.getDisplayName(player)));
        }
        for (String s : this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_MAP.MAP_SHOWN")) {
            if (!s.equalsIgnoreCase("%claims%")) {
                player.sendMessage(s);
                continue;
            }
            for (String ss : list) {
                player.sendMessage(ss);
            }
            list.clear();
        }
    }
    
    public void clearPillar(Player player, Location location) {
        if (location == null) {
            return;
        }
        for (int y = 0; y < 256; ++y) {
            Location locClone = location.clone().add(0.0, y, 0.0);
            Block block = locClone.getBlock();
            player.sendBlockChange(locClone, block.getType(), block.getData());
        }
    }
}
