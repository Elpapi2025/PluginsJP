package me.juanpiece.titan.modules.events.conquest.extra;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.events.conquest.Conquest;
import me.juanpiece.titan.modules.events.conquest.ConquestManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.staff.StaffManager;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.utils.Formatter;
import me.juanpiece.titan.utils.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Capzone extends Module<ConquestManager> {

    private ConquestType type;
    private Player capturing;
    private Cuboid zone;
    private List<Player> onCap;
    private long remaining;

    public Capzone(ConquestManager manager, Cuboid zone, ConquestType type) {
        super(manager);
        this.type = type;
        this.zone = zone;
        this.capturing = null;
        this.onCap = new ArrayList<>();
        this.remaining = 0L;
    }

    public long getRemaining() {
        // Just return minutes so the time stays the same.
        if (capturing == null) {
            remaining = System.currentTimeMillis() + (Config.CONQUEST_SECONDS * 1000L);
            return Config.CONQUEST_SECONDS * 1000L;
        }

        long rem = remaining - System.currentTimeMillis();

        if (rem < 0L) {
            this.handleCapture();
            return 0L;
        }

        return rem;
    }

    public String getRemainingString() {
        return Formatter.getRemaining(getRemaining(), false);
    }

    public void handleCapture() {
        if (capturing == null) return;

        Conquest conquest = getManager().getConquest();
        Map<UUID, Integer> points = conquest.getPoints();
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(capturing.getUniqueId());

        if (pt != null) {
            points.putIfAbsent(pt.getUniqueID(), 0);
            points.put(pt.getUniqueID(), points.get(pt.getUniqueID()) + 1);
            conquest.sortPoints();
            remaining = System.currentTimeMillis() + (Config.CONQUEST_SECONDS * 1000L);

            for (String s : getLanguageConfig().getStringList("CONQUEST_EVENTS.BROADCAST_CAP")) {
                Bukkit.broadcastMessage(s
                        .replace("%team%", pt.getName())
                        .replace("%color%", type.getColor().toString())
                        .replace("%capzone%", type.getName())
                        .replace("%points%", String.valueOf(points.get(pt.getUniqueID())))
                        .replace("%maxpoints%", String.valueOf(Config.CONQUEST_POINTS_CAPTURE))
                );
            }

            if (points.get(pt.getUniqueID()) >= Config.CONQUEST_POINTS_CAPTURE) {
                conquest.reward(capturing);
                conquest.end();
                conquest.save();
            }
        }
    }

    public void tick() {
        if (capturing == null && !onCap.isEmpty()) {
            Collections.shuffle(onCap);
            capturing = onCap.get(0);
        }

        if (capturing != null) {
            TimerManager timerManager = getInstance().getTimerManager();
            StaffManager staffManager = getInstance().getStaffManager();

            if (!capturing.isOnline() || capturing.isDead()) {
                onCap.remove(capturing);
                capturing = null;

            } else if (staffManager.isStaffEnabled(capturing) || staffManager.isVanished(capturing)) {
                onCap.remove(capturing);
                capturing = null;

            } else if (timerManager.getInvincibilityTimer().hasTimer(capturing)) {
                onCap.remove(capturing);
                capturing = null;

            } else if (timerManager.getPvpTimer().hasTimer(capturing)) {
                onCap.remove(capturing);
                capturing = null;

            } else if (getManager().getZone(capturing.getLocation()) == null) {
                onCap.remove(capturing);
                capturing = null;
            }
        }
    }

    public void checkZone(boolean delete) {
        if (zone == null) return;

        // Save it to the capture zones map
        for (int x = zone.getMinimumX(); x <= zone.getMaximumX(); x++) {
            for (int z = zone.getMinimumZ(); z <= zone.getMaximumZ(); z++) {
                if (delete) {
                    getManager().getCaptureZones().remove(zone.getWorldName(), getManager().toLong(x, z));

                } else {
                    if (getManager().getCaptureZones().contains(
                            zone.getWorldName(),
                            getManager().toLong(x, z))) continue;

                    getManager().getCaptureZones().put(zone.getWorldName(), getManager().toLong(x, z), this);
                }
            }
        }
    }
}