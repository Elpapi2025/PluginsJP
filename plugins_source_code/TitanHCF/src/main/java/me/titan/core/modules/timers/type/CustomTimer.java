package me.titan.core.modules.timers.type;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.timers.Timer;
import me.titan.core.modules.timers.TimerManager;
import me.titan.core.utils.Formatter;

@Getter
@Setter
public class CustomTimer extends Timer {
    protected Long remaining;
    protected String displayName;
    
    public CustomTimer(TimerManager manager, String name, String text, long time) {
        super(manager, name, "", 0);
        this.displayName = text;
        this.remaining = System.currentTimeMillis() + time;
        this.getManager().getCustomTimers().put(name, this);
    }
    
    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.getManager().getCustomTimers().remove(this.name);
        }
        return Formatter.formatMMSS(time);
    }
}