package me.juanpiece.titan.modules.walls.task;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.claims.Claim;
import me.juanpiece.titan.modules.walls.WallManager;
import me.juanpiece.titan.modules.walls.WallType;
import me.juanpiece.titan.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class WallTask extends Module<WallManager> implements Runnable {

    private final TeamManager teamManager;
    private final Cooldown messageCooldown;

    public WallTask(WallManager manager) {
        super(manager);
        this.teamManager = manager.getInstance().getTeamManager();
        this.messageCooldown = new Cooldown(manager);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {

                tick(player);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick(Player player) {
        Set<Claim> nearby = teamManager.getClaimManager().getNearbyCuboids(player.getLocation(), 6);

        // Clear past blocks that are out of range
        getManager().clearWalls(player);
        getManager().clearLunarBorders(player, nearby);

        for (Claim claim : nearby) {
            Team team = getInstance().getTeamManager().getTeam(claim.getTeam());
            if (team == null) continue;

            WallType wallType = getManager().getWallType(claim, team, player);
            if (wallType == null) continue;

            getManager().sendWall(player, claim, wallType);

            if (wallType.isEntryLimited()) {
                if (messageCooldown.hasCooldown(player)) continue;

                String deniedMessage = getLanguageConfig().getString("WALL_LISTENER.DENIED_" + wallType.getConfigPath() + "_ENTRY")
                        .replace("%members%", String.valueOf(wallType.getEntryLimit()))
                        .replace("%claim%", team.getDisplayName(player));

                player.sendMessage(deniedMessage);
                messageCooldown.applyCooldown(player, 3);
            }
        }
    }
}