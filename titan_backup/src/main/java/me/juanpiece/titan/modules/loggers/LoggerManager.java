package me.juanpiece.titan.modules.loggers;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.loggers.listener.LoggerListener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class LoggerManager extends Manager {

    private final Map<UUID, Logger> loggers; // logger uuid, logger
    private final Map<UUID, UUID> players; // player uuid, villager uuid

    public LoggerManager(HCF instance) {
        super(instance);
        this.loggers = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        new LoggerListener(this);
    }

    @Override
    public void disable() {
        for (Logger logger : loggers.values()) {
            logger.getVillager().remove();
        }
    }

    public void spawnLogger(Player player) {
        Logger logger = new Logger(this, player);
        loggers.put(logger.getVillager().getUniqueId(), logger);
        players.put(player.getUniqueId(), logger.getVillager().getUniqueId());
    }

    public void removeLogger(Player player) {
        if (!players.containsKey(player.getUniqueId())) return;

        UUID villagerUUID = players.remove(player.getUniqueId());
        Logger logger = loggers.remove(villagerUUID);

        if (logger != null) {
            Villager villager = logger.getVillager();
            if (!villager.isDead()) villager.remove();
            logger.getRemoveTask().cancel();
        }
    }
}