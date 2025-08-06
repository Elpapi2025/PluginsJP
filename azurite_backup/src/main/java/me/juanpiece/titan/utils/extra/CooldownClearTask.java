package me.juanpiece.titan.utils.extra;

import me.juanpiece.titan.HCF;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CooldownClearTask extends BukkitRunnable {

    private final HCF instance;

    public CooldownClearTask(HCF instance) {
        this.instance = instance;
        this.runTaskTimer(instance, 0L, (20 * 60) * 5);
    }

    @Override
    public void run() {
        for (Cooldown cooldown : instance.getCooldowns()) {
            cooldown.clean();
        }

        for (TeamCooldown teamCooldown : instance.getTeamCooldowns()) {
            teamCooldown.clean();
        }
    }
}