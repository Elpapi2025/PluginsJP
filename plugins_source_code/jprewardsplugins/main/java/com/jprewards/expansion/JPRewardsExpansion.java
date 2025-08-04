package com.jprewards.expansion;

import com.jprewards.JPRewards;
import com.jprewards.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
// import org.jetbrains.annotations.NotNull; // Removed import

public class JPRewardsExpansion extends PlaceholderExpansion {

    private final JPRewards plugin;

    public JPRewardsExpansion(JPRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() { // Removed @NotNull
        return "jprewards";
    }

    @Override
    public String getAuthor() { // Removed @NotNull
        return "YourName"; // Replace with your name or plugin author
    }

    @Override
    public String getVersion() { // Removed @NotNull
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is a persistent expansion
    }

    @Override
    public String onPlaceholderRequest(org.bukkit.entity.Player player, String params) { // Changed OfflinePlayer to Player
        if (player == null) { // No need for !player.isOnline() as it's a Player object
            return "";
        }

        PlayerData playerData = new PlayerData(player.getUniqueId(), 0, 0);
        plugin.getDataManager().loadPlayerData(player.getUniqueId(), playerData);

        if (params.equalsIgnoreCase("current_streak")) {
            return String.valueOf(playerData.getCurrentStreak());
        }

        if (params.equalsIgnoreCase("last_claimed_day")) {
            return String.valueOf(playerData.getLastClaimedDay());
        }

        // Add more placeholders as needed
        return null; // Placeholder is not valid
    }
}