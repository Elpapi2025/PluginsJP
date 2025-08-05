package me.keano.azurite.modules.events.koth;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import me.keano.azurite.HCF;
import me.keano.azurite.modules.events.EventType;
import me.keano.azurite.modules.events.koth.listener.KothListener;
import me.keano.azurite.modules.framework.Manager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("unchecked")
public class KothManager extends Manager {

    private final Map<String, Koth> koths;
    private final Table<String, Long, Koth> captureZones;

    public KothManager(HCF instance) {
        super(instance);
        this.koths = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.captureZones = HashBasedTable.create();
        this.load();
        new KothListener(this);
    }

    @Override
    public void disable() {
        for (Koth koth : koths.values()) {
            koth.save();
        }
    }

    private void load() {
        for (Object object : getEventsData().getValues().values()) {
            Map<String, Object> map = (Map<String, Object>) object;

            // it's not a koth rather a different event.
            if (EventType.valueOf((String) map.get("type")) != EventType.KOTH) continue;

            Koth koth = new Koth(this, map);
            koth.save(); // we need to save the capture zone to the map
        }
    }

    public Koth getKoth(String name) {
        return koths.get(name);
    }

    public Koth getZone(Location location) {
        Koth koth = captureZones.get(location.getWorld().getName(), toLong(location.getBlockX(), location.getBlockZ()));

        // We need to check y-axis for koths aswell!
        if (koth != null) {
            int y = location.getBlockY();

            if (y > koth.getCaptureZone().getMaximumY() || y < koth.getCaptureZone().getMinimumY()) {
                return null;
            }
        }

        return koth;
    }

    public List<Koth> getActiveKoths() {
        List<Koth> koths = new ArrayList<>();

        for (Koth koth : getKoths().values()) {
            if (koth.isActive()) koths.add(koth);
        }

        return koths;
    }

    public long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }
}