package me.juanpiece.titan.modules.listeners.type;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.listeners.ListenerManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class EndListener extends Module<ListenerManager> {

    public EndListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSwitch(PlayerPortalEvent e) {
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        World.Environment from = e.getFrom().getWorld().getEnvironment();
        World.Environment to = e.getTo().getWorld().getEnvironment();
        Player player = e.getPlayer();

        if (from == World.Environment.NORMAL && to == World.Environment.THE_END && cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            e.setTo(getInstance().getWaypointManager().getEndSpawn().clone().add(0.5, 0, 0.5));
            return;
        }

        if (from == World.Environment.THE_END && cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            e.setTo(getInstance().getWaypointManager().getEndWorldExit().clone().add(0.5, 0, 0.5));
            player.sendMessage(getLanguageConfig().getString("END_LISTENER.ENTERED"));
        }
    }
}