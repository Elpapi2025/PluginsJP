package me.keano.azurite.modules.timers.listeners.servertimers;

import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.modules.timers.Timer;
import me.keano.azurite.modules.timers.TimerManager;
import me.keano.azurite.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AntiCleanTimer extends Timer {

    private final int membersRequired;

    public AntiCleanTimer(TimerManager manager) {
        super(
                manager,
                "AntiClean",
                "",
                manager.getConfig().getInt("ANTI_CLEAN.COOLDOWN")
        );
        this.membersRequired = getConfig().getInt("ANTI_CLEAN.MEMBERS_REQUIRED");
    }

    @Override
    public void reload() {
        this.fetchScoreboard();
        this.seconds = getConfig().getInt("ANTI_CLEAN.COOLDOWN");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!Config.ANTICLEAN_ENABLED) return;

        Player damaged = (Player) e.getEntity();
        Player damager = Utils.getDamager(e.getDamager());

        if (damager == null) return;
        if (damaged == damager) return;

        PlayerTeam damagedTeam = getInstance().getTeamManager().getByPlayer(damaged.getUniqueId());
        PlayerTeam damagerTeam = getInstance().getTeamManager().getByPlayer(damager.getUniqueId());

        // Teams
        PlayerTeam antiCleanDamaged = null;

        // Cooldowns
        long antiCleanCooldownDamager = 0L;
        long antiCleanCooldownDamaged = 0L;

        // Cache these
        if (damagerTeam != null) {
            antiCleanCooldownDamager = damagerTeam.getAntiCleanRemaining();
        }

        // Cache these
        if (damagedTeam != null) {
            antiCleanDamaged = damagedTeam.getAntiCleanTeam();
            antiCleanCooldownDamaged = damagedTeam.getAntiCleanRemaining();
        }

        // Update Anti-Clean
        if (damagedTeam != null && damagerTeam != null &&
                // Update if no anti-clean
                (antiCleanCooldownDamager <= 0L && antiCleanCooldownDamaged <= 0L) ||
                // Update if the team doesn't exist anymore
                (damagedTeam != null && damagerTeam != null && antiCleanDamaged == null) ||
                // Update if anti-clean but same team
                (antiCleanCooldownDamager > 0L && antiCleanCooldownDamaged > 0L && antiCleanDamaged == damagerTeam)) {

            // Member Check
            if (damagedTeam.getOnlinePlayersSize(false) < membersRequired) return;
            if (damagerTeam.getOnlinePlayersSize(false) < membersRequired) return;

            damagedTeam.setAntiCleanTeam(damagerTeam.getUniqueID());
            damagerTeam.setAntiCleanTeam(damagedTeam.getUniqueID());

            damagedTeam.setAntiCleanTimer(System.currentTimeMillis() + (1000L * seconds));
            damagerTeam.setAntiCleanTimer(System.currentTimeMillis() + (1000L * seconds));
        }
    }

    public boolean checkRadius(Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        return (x > Config.ANTICLEAN_RADIUS || x < -Config.ANTICLEAN_RADIUS
                || z > Config.ANTICLEAN_RADIUS || z < -Config.ANTICLEAN_RADIUS);
    }
}