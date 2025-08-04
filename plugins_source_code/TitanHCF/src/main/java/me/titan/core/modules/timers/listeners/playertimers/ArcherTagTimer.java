package me.titan.core.modules.timers.listeners.playertimers;

import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.timers.type.PlayerTimer;

public class ArcherTagTimer extends PlayerTimer {
    public ArcherTagTimer(TimerManager manager) {
        super(manager, false, "ArcherTag", "PLAYER_TIMERS.ARCHER_TAG", manager.getConfig().getInt("TIMERS_COOLDOWN.ARCHER_TAG"));
    }
}