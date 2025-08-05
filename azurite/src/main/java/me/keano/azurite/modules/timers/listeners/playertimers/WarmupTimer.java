package me.keano.azurite.modules.timers.listeners.playertimers;

import lombok.Getter;
import me.keano.azurite.modules.pvpclass.PvPClass;
import me.keano.azurite.modules.timers.TimerManager;
import me.keano.azurite.modules.timers.type.PlayerTimer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class WarmupTimer extends PlayerTimer {

    private final Map<UUID, String> warmups;

    public WarmupTimer(TimerManager manager) {
        super(
                manager,
                false,
                "Warmup",
                "PLAYER_TIMERS.WARMUP",
                manager.getConfig().getInt("TIMERS_COOLDOWN.WARMUP")
        );
        this.warmups = new HashMap<>();
    }

    @Override
    public void reload() {
        this.fetchScoreboard();
        this.seconds = getConfig().getInt("TIMERS_COOLDOWN.WARMUP");
    }

    @Override
    public void removeTimer(Player player) {
        super.removeTimer(player);
        warmups.remove(player.getUniqueId());
    }

    public void putTimerWithClass(Player player, PvPClass pvpClass) {
        super.applyTimer(player);
        warmups.put(player.getUniqueId(), pvpClass.getName());
    }
}