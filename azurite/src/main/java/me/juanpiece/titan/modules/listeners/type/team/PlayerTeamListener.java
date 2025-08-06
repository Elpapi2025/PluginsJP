package me.juanpiece.titan.modules.listeners.type.team;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.task.TeamViewerTask;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.utils.Tasks;
import me.juanpiece.titan.utils.extra.Cooldown;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class PlayerTeamListener extends Module<TeamManager> {

    private final Cooldown messageCooldown;
    private final Cooldown netherCooldown;
    private final Cooldown endCooldown;

    private final int netherMinutes;
    private final int endMinutes;

    public PlayerTeamListener(TeamManager manager) {
        super(manager);

        this.messageCooldown = new Cooldown(manager);
        this.netherCooldown = new Cooldown(manager);
        this.endCooldown = new Cooldown(manager);

        this.netherMinutes = getConfig().getInt("DEATH_PORTAL_BAN.NETHER");
        this.endMinutes = getConfig().getInt("DEATH_PORTAL_BAN.END");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();

        if (e.getTo().getWorld().getEnvironment() == World.Environment.NETHER && netherCooldown.hasCooldown(player)) {
            e.setCancelled(true);

            if (!messageCooldown.hasCooldown(player)) {
                messageCooldown.applyCooldown(player, 3);
                player.sendMessage(getLanguageConfig().getString("PORTAL_LISTENER.CANNOT_USE_NETHER")
                        .replace("%time%", netherCooldown.getRemaining(player))
                );
            }
            return;
        }

        if (e.getTo().getWorld().getEnvironment() == World.Environment.THE_END && endCooldown.hasCooldown(player)) {
            e.setCancelled(true);

            if (!messageCooldown.hasCooldown(player)) {
                messageCooldown.applyCooldown(player, 3);
                player.sendMessage(getLanguageConfig().getString("PORTAL_LISTENER.CANNOT_USE_NETHER")
                        .replace("%time%", endCooldown.getRemaining(player))
                );
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        switch (player.getWorld().getEnvironment()) {
            case NETHER:
                if (netherMinutes != -1) netherCooldown.applyCooldown(player, netherMinutes * 60);
                return;

            case THE_END:
                if (endMinutes != -1) endCooldown.applyCooldown(player, endMinutes * 60);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerTeam pt = getManager().getByPlayer(player.getUniqueId());

        if (pt == null) return;

        // Start the task
        if (pt.getTeamViewerTask() == null) {
            pt.setTeamViewerTask(new TeamViewerTask(getManager(), pt.getUniqueID()));
        }

        getInstance().getClientHook().clearTeamViewer(player);
        Tasks.execute(getManager(), () -> getManager().checkTeamSorting(player.getUniqueId()));

        pt.broadcast(getLanguageConfig().getString("PLAYER_TEAM_LISTENER.MEMBER_ONLINE")
                .replace("%player%", player.getName())
        );
        pt.broadcastAlly(getLanguageConfig().getString("PLAYER_TEAM_LISTENER.ALLY_ONLINE")
                .replace("%player%", player.getName())
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        handleQuit(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        handleQuit(e.getPlayer());
    }

    private void handleQuit(Player player) {
        PlayerTeam pt = getManager().getByPlayer(player.getUniqueId());

        if (pt == null) return;

        // Cancel if no members online
        if (pt.getTeamViewerTask() != null && pt.getOnlinePlayersSize(true) == 1) { // Fix
            pt.getTeamViewerTask().cancel();
            pt.setTeamViewerTask(null);
        }

        pt.getTeamViewer().remove(player.getUniqueId());
        getInstance().getClientHook().clearTeamViewer(player);
        Tasks.execute(getManager(), () -> getManager().checkTeamSorting(player.getUniqueId()));

        pt.broadcast(getLanguageConfig().getString("PLAYER_TEAM_LISTENER.MEMBER_OFFLINE")
                .replace("%player%", player.getName())
        );
        pt.broadcastAlly(getLanguageConfig().getString("PLAYER_TEAM_LISTENER.ALLY_OFFLINE")
                .replace("%player%", player.getName())
        );
    }
}