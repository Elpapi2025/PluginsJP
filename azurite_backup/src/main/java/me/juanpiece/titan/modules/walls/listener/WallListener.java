package me.juanpiece.titan.modules.walls.listener;

import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.type.*;
import me.juanpiece.titan.modules.walls.WallManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class WallListener extends Module<WallManager> {

    public WallListener(WallManager manager) {
        super(manager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        getManager().clearTeamMap(player);
        getManager().clearWalls(player);

        // Just to lower ram usage. Will create again once the player needs it.
        getManager().getWalls().remove(player.getUniqueId());
        getManager().getTeamMaps().remove(player.getUniqueId());
        getManager().getLunar().remove(player.getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        getManager().getEndSafezoneDenied().remove(player.getUniqueId());
        getManager().getNetherSafezoneDenied().remove(player.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        Player player = e.getPlayer();
        Team to = getInstance().getTeamManager().getClaimManager().getTeam(e.getTo());

        if (to instanceof SafezoneTeam && (getManager().isEndSafezoneDenied(player) || getManager().isNetherSafezoneDenied(player))) {
            e.setTo(e.getFrom());
            return;
        }

        if (Config.TEAM_CITADEL_ENTER_LIMIT > 0 && !player.hasPermission("titan.citadel.entry") && to instanceof CitadelTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

            if (pt == null || pt.getOnlinePlayersSize(false) < Config.TEAM_CITADEL_ENTER_LIMIT) {
                e.setTo(e.getFrom());
                player.sendMessage(getLanguageConfig().getString("WALL_LISTENER.DENIED_CITADEL_ENTRY")
                        .replace("%members%", String.valueOf(Config.TEAM_CITADEL_ENTER_LIMIT))
                        .replace("%claim%", to.getDisplayName(player))
                );
                return;
            }
        }

        if (Config.TEAM_CONQUEST_ENTER_LIMIT > 0 && !player.hasPermission("titan.conquest.entry") && to instanceof ConquestTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

            if (pt == null || pt.getOnlinePlayersSize(false) < Config.TEAM_CONQUEST_ENTER_LIMIT) {
                e.setTo(e.getFrom());
                player.sendMessage(getLanguageConfig().getString("WALL_LISTENER.DENIED_CONQUEST_ENTRY")
                        .replace("%members%", String.valueOf(Config.TEAM_CONQUEST_ENTER_LIMIT))
                        .replace("%claim%", to.getDisplayName(player))
                );
                return;
            }
        }

        if (Config.TEAM_EVENT_ENTER_LIMIT > 0 && !player.hasPermission("titan.event.entry") && to instanceof EventTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

            if (pt == null || pt.getOnlinePlayersSize(false) < Config.TEAM_CITADEL_ENTER_LIMIT) {
                e.setTo(e.getFrom());
                player.sendMessage(getLanguageConfig().getString("WALL_LISTENER.DENIED_EVENT_ENTRY")
                        .replace("%members%", String.valueOf(Config.TEAM_CITADEL_ENTER_LIMIT))
                        .replace("%claim%", to.getDisplayName(player))
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player player = e.getPlayer();
        Team to = getInstance().getTeamManager().getClaimManager().getTeam(e.getTo());

        // Don't do citadel since that has anti-pearl already.

        if (to instanceof SafezoneTeam && (getManager().isEndSafezoneDenied(player) || getManager().isNetherSafezoneDenied(player))) {
            e.setCancelled(true);
            getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            return;
        }

        if (Config.TEAM_CONQUEST_ENTER_LIMIT > 0 && !player.hasPermission("titan.conquest.entry") && to instanceof ConquestTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

            if (pt == null || pt.getOnlinePlayersSize(false) < Config.TEAM_CONQUEST_ENTER_LIMIT) {
                e.setCancelled(true);
                getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                player.sendMessage(getLanguageConfig().getString("WALL_LISTENER.DENIED_CONQUEST_ENTRY")
                        .replace("%members%", String.valueOf(Config.TEAM_CONQUEST_ENTER_LIMIT))
                        .replace("%claim%", to.getDisplayName(player))
                );
                return;
            }
        }

        if (Config.TEAM_EVENT_ENTER_LIMIT > 0 && !player.hasPermission("titan.event.entry") && to instanceof EventTeam) {
            PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

            if (pt == null || pt.getOnlinePlayersSize(false) < Config.TEAM_EVENT_ENTER_LIMIT) {
                e.setCancelled(true);
                getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                player.sendMessage(getLanguageConfig().getString("WALL_LISTENER.DENIED_EVENT_ENTRY")
                        .replace("%members%", String.valueOf(Config.TEAM_EVENT_ENTER_LIMIT))
                        .replace("%claim%", to.getDisplayName(player)));
            }
        }
    }
}