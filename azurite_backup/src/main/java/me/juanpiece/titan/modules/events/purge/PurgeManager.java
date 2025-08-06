package me.juanpiece.titan.modules.events.purge;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.events.purge.listener.PurgeListener;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.timers.type.CustomTimer;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class PurgeManager extends Manager {

    private final List<String> interactable;

    public PurgeManager(HCF instance) {
        super(instance);
        this.interactable = getConfig().getStringList("PURGE_TIMER.INTRACTABLE");
        new PurgeListener(this);
    }

    @Override
    public void reload() {
        interactable.clear();
        interactable.addAll(getConfig().getStringList("PURGE_TIMER.INTRACTABLE"));
    }

    public void start(long time) {
        new CustomTimer(getInstance().getTimerManager(), "Purge", "Purge", time);
    }

    public boolean isActive() {
        return getInstance().getTimerManager().getCustomTimer("Purge") != null;
    }
}