package me.juanpiece.titan.modules.events.king.listener;

import me.juanpiece.titan.modules.events.king.KingManager;
import me.juanpiece.titan.modules.framework.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KingListener extends Module<KingManager> {

    public KingListener(KingManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if (getManager().getKing() == player) {
            e.getDrops().clear(); // we make sure we clear the drops.
            e.setDroppedExp(0);
            e.setDeathMessage(null);
            getManager().stopKing(false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (getManager().getKing() == player) {
            getManager().stopKing(true);
        }
    }
}