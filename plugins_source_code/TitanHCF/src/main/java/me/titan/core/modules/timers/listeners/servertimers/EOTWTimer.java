package me.titan.core.modules.timers.listeners.servertimers;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.timers.Timer;
import me.titan.core.modules.timers.TimerManager;

@Getter
@Setter
public class EOTWTimer extends Timer {
    private boolean active;
    private Long remaining;
    
    public EOTWTimer(TimerManager manager) {
        super(manager, "EOTW", "", 0);
        this.remaining = 0L;
        this.active = false;
    }
}