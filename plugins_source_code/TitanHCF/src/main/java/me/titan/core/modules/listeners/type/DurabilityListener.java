package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class DurabilityListener extends HCFModule<ListenerManager> {
    private final int chance;
    
    @EventHandler
    public void onDamage(PlayerItemDamageEvent event) {
        if (!this.getConfig().getBoolean("DURABILITY_FIX.ENABLED")) {
            return;
        }
        int i = ThreadLocalRandom.current().nextInt(101);
        if (i <= this.chance) {
            event.setCancelled(true);
        }
    }
    
    public DurabilityListener(ListenerManager manager) {
        super(manager);
        this.chance = this.getConfig().getInt("DURABILITY_FIX.PERCENT");
    }
}
