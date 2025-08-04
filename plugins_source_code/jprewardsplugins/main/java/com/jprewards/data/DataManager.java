package com.jprewards.data;

import java.util.UUID;

public interface DataManager {
    void loadPlayerData(UUID uuid, PlayerData playerData);
    void savePlayerData(PlayerData playerData);
    void createTable();
    void closeConnection();
}