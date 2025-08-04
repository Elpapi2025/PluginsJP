package me.titan.core.modules.timers.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.*;
import java.util.*;
import me.titan.core.modules.timers.type.*;

@Getter
@Setter
public class TimerExpireEvent extends Event {
    private static final HandlerList handlers;
    private UUID player;
    private PlayerTimer timer;
    
    public static HandlerList getHandlerList() {
        return TimerExpireEvent.handlers;
    }
    
    public TimerExpireEvent(PlayerTimer timer, UUID player) {
        super(true);
        this.timer = timer;
        this.player = player;
    }
    
    static {
        handlers = new HandlerList();
    }
    
    public HandlerList getHandlers() {
        return TimerExpireEvent.handlers;
    }
}
