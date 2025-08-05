package me.keano.azurite.modules.events.conquest;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import me.keano.azurite.HCF;
import me.keano.azurite.modules.events.conquest.extra.Capzone;
import me.keano.azurite.modules.events.conquest.listener.ConquestListener;
import me.keano.azurite.modules.framework.Manager;
import me.keano.azurite.utils.Tasks;
import org.bukkit.Location;

import java.util.Map;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("unchecked")
public class ConquestManager extends Manager {

    private final Conquest conquest;
    private final Table<String, Long, Capzone> captureZones;

    public ConquestManager(HCF instance) {
        super(instance);

        this.captureZones = HashBasedTable.create();
        this.conquest = new Conquest(this, (Map<String, Object>) getEventsData().getValues().get("Conquest"));

        new ConquestListener(this);
        Tasks.executeScheduled(this, 20, this::tick);
    }

    @Override
    public void disable() {
        conquest.save();
    }

    public void tick() {
        for (Capzone capzone : conquest.getCapzones().values()) {
            capzone.tick();
        }
    }

    public Capzone getZone(Location location) {
        Capzone koth = captureZones.get(location.getWorld().getName(), toLong(location.getBlockX(), location.getBlockZ()));

        // We need to check y-axis for conquests aswell!
        if (koth != null) {
            int y = location.getBlockY();

            if (y > koth.getZone().getMaximumY() || y < koth.getZone().getMinimumY()) {
                return null;
            }
        }

        return koth;
    }

    public long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }
}