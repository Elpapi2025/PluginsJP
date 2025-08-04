package com.jprewards;

import com.jprewards.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.TimeUnit;

public class PlayerJoinListener implements Listener {

    private final JPRewards plugin;

    public PlayerJoinListener(JPRewards plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = new PlayerData(player.getUniqueId(), 0, 0); // Default values
        plugin.getDataManager().loadPlayerData(player.getUniqueId(), playerData);

        long lastClaim = playerData.getLastClaimedDay();

        if (lastClaim == 0) {
            return;
        }

        boolean enableStreakLoss = plugin.getConfig().getBoolean("streak_settings.enable_streak_loss", true);
        if (enableStreakLoss) {
            long streakLossPeriodHours = plugin.getConfig().getLong("streak_settings.streak_loss_period_hours", 48);
            long streakLossPeriodMillis = TimeUnit.HOURS.toMillis(streakLossPeriodHours);

            if (System.currentTimeMillis() - lastClaim > streakLossPeriodMillis) {
                playerData.setCurrentStreak(0);
                plugin.getDataManager().savePlayerData(playerData);
                String message = plugin.getConfig().getString("messages.streak_lost", "&cHas perdido tu racha de recompensas diarias.");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }
}