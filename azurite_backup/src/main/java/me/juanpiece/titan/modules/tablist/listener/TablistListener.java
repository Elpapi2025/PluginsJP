package me.juanpiece.titan.modules.tablist.listener;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.tablist.Tablist;
import me.juanpiece.titan.modules.tablist.TablistManager;
import me.juanpiece.titan.modules.tablist.extra.TablistSkin;
import me.juanpiece.titan.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistListener extends Module<TablistManager> {

    public TablistListener(TablistManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // call last
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Tasks.executeLater(getManager(), 20L, () -> new Tablist(getManager(), player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        // Remove them from the map to help ram usage.
        getManager().getTablists().remove(player.getUniqueId());
        TablistSkin.SKIN_CACHE.remove(player.getUniqueId());
    }
}