package me.keano.azurite.modules.listeners.type;

import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.listeners.ListenerManager;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class WorldListener extends Module<ListenerManager> {

    private final List<CreatureSpawnEvent.SpawnReason> spawnReasons;

    public WorldListener(ListenerManager manager) {
        super(manager);
        this.spawnReasons = Arrays.asList(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG, CreatureSpawnEvent.SpawnReason.EGG, CreatureSpawnEvent.SpawnReason.SPAWNER, CreatureSpawnEvent.SpawnReason.BREEDING, CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
    }

    @EventHandler(ignoreCancelled = true) // just incase above doesn't work?
    public void onWeather(WeatherChangeEvent e) {
        if (e.toWeatherState()) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true) // not sure why iHCF did this, but we should too.
    public void onSquidSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Squid) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // Deny wither/ender-dragon spawning
    public void onWitherSpawn(CreatureSpawnEvent e) {
        Entity entity = e.getEntity();

        if (entity instanceof EnderDragon || entity instanceof Wither) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (!spawnReasons.contains(e.getSpawnReason()) && !Config.NATURAL_MOB_SPAWN) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
}