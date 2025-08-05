package me.keano.azurite.modules.listeners.type;

import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.listeners.ListenerManager;
import me.keano.azurite.modules.users.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CobbleListener extends Module<ListenerManager> {

    public CobbleListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());

        if (e.getItem().getItemStack().getType() != Material.COBBLESTONE) return;

        if (!user.isCobblePickup()) {
            e.setCancelled(true);
        }
    }
}