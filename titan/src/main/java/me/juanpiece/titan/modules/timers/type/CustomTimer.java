package me.juanpiece.titan.modules.timers.type;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.timers.Timer;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.modules.timers.event.CustomTimerExpireEvent;
import me.juanpiece.titan.utils.Formatter;
import org.bukkit.Bukkit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class CustomTimer extends Timer {

    protected Long remaining;
    protected String displayName;

    public CustomTimer(TimerManager manager, String name, String displayName, long time) {
        super(
                manager,
                name,
                "",
                0 // not needed
        );

        this.displayName = displayName;
        this.remaining = (System.currentTimeMillis() + time);
        getManager().getCustomTimers().put(name, this);
    }

    public boolean isActive() {
        return remaining > 0L;
    }

    public String getRemainingString() {
        long rem = remaining - System.currentTimeMillis();

        if (rem < 0L) {
            getManager().getCustomTimers().remove(name);
            Bukkit.getPluginManager().callEvent(new CustomTimerExpireEvent(this));
        }

        return Formatter.getRemaining(rem, false);
    }
}