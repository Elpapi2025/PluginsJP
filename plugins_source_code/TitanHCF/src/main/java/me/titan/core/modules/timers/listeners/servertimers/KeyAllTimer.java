package me.titan.core.modules.timers.listeners.servertimers;

import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.timers.type.CustomTimer;
import me.titan.core.utils.Formatter;
import me.titan.core.utils.Tasks;
import org.bukkit.Bukkit;

public class KeyAllTimer extends CustomTimer {
    private final String command;
    
    @Override
    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.getManager().getCustomTimers().remove(this.name);
            Tasks.execute(this.getManager(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command));
        }
        return Formatter.formatMMSS(time);
    }
    
    public KeyAllTimer(TimerManager manager, String name, String text, long time, String command) {
        super(manager, name, text, time);
        this.command = command;
    }
}