package me.juanpiece.titan.modules.listeners;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.listeners.type.*;
import me.juanpiece.titan.modules.listeners.type.team.PlayerTeamListener;
import me.juanpiece.titan.modules.listeners.type.team.TeamListener;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class ListenerManager extends Manager {

    private final List<Listener> listeners;
    private final List<BukkitTask> tasks;

    public ListenerManager(HCF instance) {
        super(instance);
        this.listeners = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.load();
    }

    private void load() {
        listeners.addAll(Arrays.asList(
                new MainListener(this),
                new ChatListener(this),
                new DeathListener(this),
                new FixListener(this),
                new BorderListener(this),
                new GlitchListener(this),
                new WorldListener(this),
                new CobbleListener(this),
                new DiamondListener(this),
                new DropListener(this),
                new StackListener(this),
                new LimiterListener(this),
                new DurabilityListener(this),
                new StrengthListener(this),
                new EndListener(this),
                new CrowbarListener(this),

                // Team Listeners
                new TeamListener(getInstance().getTeamManager()),
                new PlayerTeamListener(getInstance().getTeamManager())
        ));

        if (Utils.isModernVer()) {
            listeners.add(new PortalListener(this));
        } else {
            listeners.add(new SmeltListener(this));
            listeners.add(new PortalLegacyListener(this));
        }
    }

    @Override
    public void reload() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        Utils.iterate(tasks, (task) -> {
            task.cancel();
            return true;
        });

        this.load();
    }
}