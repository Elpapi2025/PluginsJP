package me.titan.core.modules.loggers;

import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.loggers.listener.LoggerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerManager extends Manager {
    private final Map<UUID, Logger> loggers;
    private final Map<UUID, UUID> players;
    
    public Map<UUID, UUID> getPlayers() {
        return this.players;
    }
    
    public void spawnLogger(Player player) {
        Logger logger = new Logger(this, player);
        this.loggers.put(logger.getVillager().getUniqueId(), logger);
        this.players.put(player.getUniqueId(), logger.getVillager().getUniqueId());
    }
    
    public LoggerManager(HCF plugin) {
        super(plugin);
        this.loggers = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        new LoggerListener(this);
    }
    
    public void removeLogger(Player player) {
        if (!this.players.containsKey(player.getUniqueId())) {
            return;
        }
        UUID uuid = this.players.remove(player.getUniqueId());
        Logger logger = this.loggers.remove(uuid);
        if (logger != null) {
            Villager villager = logger.getVillager();
            if (!villager.isDead()) {
                villager.remove();
            }
            logger.getRemoveTask().cancel();
        }
    }
    
    public Map<UUID, Logger> getLoggers() {
        return this.loggers;
    }
    
    @Override
    public void enable() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (!(entity instanceof Villager)) {
                    continue;
                }
                if (entity.getCustomName() == null) {
                    continue;
                }
                entity.remove();
            }
        }
    }
}
