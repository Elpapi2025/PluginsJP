package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.*;
import me.titan.core.modules.listeners.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import me.titan.core.modules.users.*;
import org.bukkit.event.*;

public class CobbleListener extends HCFModule<ListenerManager> {
    public CobbleListener(ListenerManager manager) {
        super(manager);
    }
    
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (event.getItem().getItemStack().getType() != Material.COBBLESTONE) {
            return;
        }
        if (!user.isCobblePickup()) {
            event.setCancelled(true);
        }
    }
}
