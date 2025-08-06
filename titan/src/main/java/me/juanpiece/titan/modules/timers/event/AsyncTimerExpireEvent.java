package me.juanpiece.titan.modules.timers.event;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.timers.type.PlayerTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class AsyncTimerExpireEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private PlayerTimer timer;
    private UUID player;

    public AsyncTimerExpireEvent(PlayerTimer timer, UUID player) {
        super(true);
        this.timer = timer;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}