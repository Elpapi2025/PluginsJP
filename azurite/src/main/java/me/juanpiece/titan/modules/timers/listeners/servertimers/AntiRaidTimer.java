package me.juanpiece.titan.modules.timers.listeners.servertimers;

import lombok.Getter;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.timers.Timer;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.utils.extra.Pair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class AntiRaidTimer extends Timer {

    private final Map<UUID, Pair<UUID, Long>> cooldowns;

    public AntiRaidTimer(TimerManager manager) {
        super(
                manager,
                "AntiRaid",
                "",
                manager.getConfig().getInt("TIMERS_COOLDOWN.ANTI_RAID")
        );
        this.cooldowns = new ConcurrentHashMap<>();
    }

    public void applyTimer(PlayerTeam raidable, PlayerTeam pt) {
        cooldowns.put(raidable.getUniqueID(), new Pair<>(pt.getUniqueID(), System.currentTimeMillis() + (1000L * seconds)));
    }

    public boolean canBreak(PlayerTeam raidable, PlayerTeam pt) {
        Pair<UUID, Long> remaining = cooldowns.get(raidable.getUniqueID());

        if (remaining != null) {
            // Time has passed
            if (remaining.getValue() < System.currentTimeMillis()) {
                return true;
            }

            // Check if the team name is the same if the time hasnt passed otherwise deny.
            return (pt != null && remaining.getKey().equals(pt.getUniqueID()));
        }

        return true;
    }
}