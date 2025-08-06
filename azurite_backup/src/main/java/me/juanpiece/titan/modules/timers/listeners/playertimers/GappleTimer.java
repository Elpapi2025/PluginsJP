package me.juanpiece.titan.modules.timers.listeners.playertimers;

import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.modules.timers.type.PlayerTimer;
import me.juanpiece.titan.utils.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class GappleTimer extends PlayerTimer {

    public GappleTimer(TimerManager manager) {
        super(
                manager,
                false,
                "Gapple",
                "PLAYER_TIMERS.GAPPLE",
                manager.getConfig().getInt("TIMERS_COOLDOWN.GAPPLE")
        );
    }

    @Override
    public void reload() {
        this.fetchScoreboard();
        this.seconds = getConfig().getInt("TIMERS_COOLDOWN.GAPPLE");
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (getInstance().getVersionManager().getVersion().isNotGapple(item)) return;

        if (hasTimer(player)) {
            e.setCancelled(true);
            player.sendMessage(getLanguageConfig().getString("GAPPLE_TIMER.COOLDOWN")
                    .replace("%seconds%", getRemainingString(player))
            );
            return;
        }

        if (seconds != 0) {
            applyTimer(player);

            for (String s : getLanguageConfig().getStringList("GAPPLE_TIMER.ADDED_COOLDOWN")) {
                player.sendMessage(s
                        .replace("%cooldown%", Formatter.formatDetailed(getSeconds() * 1000L))
                );
            }
        }
    }
}