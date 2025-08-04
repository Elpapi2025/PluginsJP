package me.titan.core.modules.timers;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.utils.CC;

@Getter
@Setter
public abstract class Timer extends HCFModule<TimerManager> {
    protected int seconds;
    protected String name;
    protected String scoreboardPath;
    
    public Timer(TimerManager manager, String name, String text, int seconds) {
        super(manager);
        this.name = name;
        this.scoreboardPath = CC.t(text);
        this.seconds = seconds;
    }
}