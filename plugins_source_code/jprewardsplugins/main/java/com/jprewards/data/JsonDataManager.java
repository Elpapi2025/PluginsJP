package com.jprewards.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jprewards.JPRewards;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JsonDataManager implements DataManager {

    private final JPRewards plugin;
    private final File dataFolder;
    private final Gson gson;
    private final ConcurrentHashMap<UUID, PlayerData> playerDataCache;

    public JsonDataManager(JPRewards plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.playerDataCache = new ConcurrentHashMap<>();
    }

    @Override
    public void loadPlayerData(UUID uuid, PlayerData playerData) {
        File playerFile = new File(dataFolder, uuid.toString() + ".json");
        if (playerFile.exists()) {
            try (FileReader reader = new FileReader(playerFile)) {
                PlayerData loadedData = gson.fromJson(reader, PlayerData.class);
                playerData.setLastClaimedDay(loadedData.getLastClaimedDay());
                playerData.setCurrentStreak(loadedData.getCurrentStreak());
                playerDataCache.put(uuid, playerData);
            } catch (IOException e) {
                plugin.getLogger().warning("Error loading player data for " + uuid.toString() + ": " + e.getMessage());
            }
        } else {
            // If file doesn't exist, it's a new player or data was lost. Initialize with default.
            playerData.setLastClaimedDay(0);
            playerData.setCurrentStreak(0);
            playerDataCache.put(uuid, playerData);
            savePlayerData(playerData); // Save initial data
        }
    }

    @Override
    public void savePlayerData(PlayerData playerData) {
        File playerFile = new File(dataFolder, playerData.getUuid().toString() + ".json");
        try (FileWriter writer = new FileWriter(playerFile)) {
            gson.toJson(playerData, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Error saving player data for " + playerData.getUuid().toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void createTable() {
        // No table creation needed for JSON files, just ensure directory exists
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    @Override
    public void closeConnection() {
        // No connection to close for JSON files
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.get(uuid);
    }
}