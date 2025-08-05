package me.keano.azurite.modules.timers.listeners.servertimers;

import me.keano.azurite.HCF;
import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.teams.TeamManager;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.modules.timers.Timer;
import me.keano.azurite.modules.timers.TimerManager;
import me.keano.azurite.utils.Tasks;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamRegenTimer extends Timer {

    private final Map<PlayerTeam, Long> teamsRegenerating;

    public TeamRegenTimer(TimerManager manager) {
        super(
                manager,
                "RegenTimer",
                "", // none
                manager.getTeamConfig().getInt("TEAM_DTR.REGEN_TIMER") * 60 // minutes
        );
        this.teamsRegenerating = new ConcurrentHashMap<>();
        Tasks.executeScheduledAsync(getManager(), 20, this::tick); // can be async because we're just checking
    }

    @Override
    public void reload() {
        this.seconds = getTeamConfig().getInt("TEAM_DTR.REGEN_TIMER") * 60;
    }

    private void tick() {
        Iterator<PlayerTeam> iterator = teamsRegenerating.keySet().iterator();
        TeamManager teamManager = getInstance().getTeamManager();

        while (iterator.hasNext()) {
            PlayerTeam pt = iterator.next();

            if (!teamManager.getTeams().containsKey(pt.getUniqueID())) {
                iterator.remove(); // They disbanded
                continue;
            }

            if (hasTimer(pt)) continue; // if they have the timer continue looping

            iterator.remove();
            new MinuteRegenTask(getInstance(), pt);
            pt.setMinuteRegen(true);
        }
    }

    public void applyTimer(PlayerTeam pt) {
        teamsRegenerating.put(pt, System.currentTimeMillis() + (seconds * 1000L));
        pt.setMinuteRegen(false); // don't regen if they have the timer
    }

    public void applyTimer(PlayerTeam pt, long time) {
        teamsRegenerating.put(pt, System.currentTimeMillis() + time);
        pt.setMinuteRegen(false); // don't regen if they have the timer
    }

    public boolean hasTimer(PlayerTeam pt) {
        return getRemaining(pt) > 0L;
    }

    public long getRemaining(PlayerTeam pt) {
        return teamsRegenerating.containsKey(pt) ? teamsRegenerating.get(pt) - System.currentTimeMillis() : 0L;
    }

    public void startMinuteRegen(PlayerTeam pt) {
        if (pt.isMinuteRegen()) {
            new MinuteRegenTask(getInstance(), pt);
        }
    }

    private static class MinuteRegenTask extends BukkitRunnable {

        private final HCF instance;
        private final PlayerTeam pt;

        public MinuteRegenTask(HCF instance, PlayerTeam pt) {
            this.instance = instance;
            this.pt = pt;
            this.runTaskTimer(instance, 0L, instance.getTimerManager()
                    .getTeamConfig().getInt("TEAM_DTR.REGEN_DTR_INTERVAL") * 20L);
        }

        @Override
        public void run() {
            // they disband while regenerating
            if (!instance.getTeamManager().getTeams().containsKey(pt.getUniqueID())) {
                cancel();
                return;
            }

            // Someone died while regenerating
            if (instance.getTimerManager().getTeamRegenTimer().hasTimer(pt)) {
                cancel();
                pt.setMinuteRegen(false);
                return;
            }

            pt.setMinuteRegen(true);
            pt.setDtr(pt.getDtr() + Config.DTR_REGEN_PER_MIN);
            pt.save();
            pt.broadcast(instance.getTimerManager().getLanguageConfig().getString("TEAM_REGEN_TIMER.REGENERATING")
                    .replace("%dtr%", String.valueOf(Config.DTR_REGEN_PER_MIN))
            );

            // Check this after incrementing.
            if (pt.getDtr() >= pt.getMaxDtr()) {
                cancel();
                pt.setDtr(pt.getMaxDtr());
                pt.setMinuteRegen(false);
                pt.broadcast(instance.getTimerManager().getLanguageConfig().getString("TEAM_REGEN_TIMER.FINISHED_REGENERATING"));
                pt.save();
            }
        }
    }
}