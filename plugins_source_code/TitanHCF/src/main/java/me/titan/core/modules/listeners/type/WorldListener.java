package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import me.titan.core.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener extends HCFModule<ListenerManager> {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Squid) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && !this.getConfig().getBoolean("MOB_NATURAL_SPAWN")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onExplode(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
    
    private void load() {
        Tasks.executeLater(this.getManager(), 200, () -> {
            for(World world : Bukkit.getServer().getWorlds()) {
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThundering(false);
                world.setStorm(false);
                world.setGameRuleValue("mobGriefing", "false");
                if (this.getInstance().getVersionManager().isVer16()) {
                    world.setGameRuleValue("maxEntityCramming", "0");
                    world.setGameRuleValue("doTraderSpawning", "false");
                    world.setGameRuleValue("doPatrolSpawning", "false");
                    world.setGameRuleValue("doInsomnia", "false");
                    world.setGameRuleValue("disableRaids", "true");
                }
            }
        });
    }
    
    public WorldListener(ListenerManager manager) {
        super(manager);
        this.load();
    }
}
