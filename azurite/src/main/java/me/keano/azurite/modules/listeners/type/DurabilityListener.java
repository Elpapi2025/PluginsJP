package me.keano.azurite.modules.listeners.type;

import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.listeners.ListenerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class DurabilityListener extends Module<ListenerManager> {

    private final int chance;
    private final boolean enabled;

    public DurabilityListener(ListenerManager manager) {
        super(manager);
        this.chance = getConfig().getInt("DURABILITY_FIX.PERCENT");
        this.enabled = getConfig().getBoolean("DURABILITY_FIX.ENABLED");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(PlayerItemDamageEvent e) {
        if (!enabled) return;

        int random = ThreadLocalRandom.current().nextInt(100 + 1);

        if (random <= chance) {
            e.setCancelled(true);
        }
    }
}