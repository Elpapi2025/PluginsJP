package com.jprewards.data;

import com.jprewards.JPRewards;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLiteDataManager implements DataManager {

    private final JPRewards plugin;
    private Connection connection;
    private final String databaseFile;

    public SQLiteDataManager(JPRewards plugin) {
        this.plugin = plugin;
        this.databaseFile = plugin.getConfig().getString("database.sqlite.file", "rewards.db");
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                File dbFile = new File(plugin.getDataFolder(), databaseFile);
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            } catch (ClassNotFoundException e) {
                plugin.getLogger().severe("SQLite JDBC driver not found! " + e.getMessage());
            }
        }
        return connection;
    }

    @Override
    public void createTable() {
        try (Statement statement = getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_rewards (" +
                         "uuid VARCHAR(36) PRIMARY KEY," +
                         "last_claimed_day INTEGER NOT NULL," +
                         "current_streak INTEGER NOT NULL" +
                         ");";
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error creating SQLite table: " + e.getMessage());
        }
    }

    @Override
    public void loadPlayerData(UUID uuid, PlayerData playerData) {
        String sql = "SELECT last_claimed_day, current_streak FROM player_rewards WHERE uuid = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                playerData.setLastClaimedDay(rs.getLong("last_claimed_day"));
                playerData.setCurrentStreak(rs.getInt("current_streak"));
            } else {
                // New player, initialize with defaults
                playerData.setLastClaimedDay(0);
                playerData.setCurrentStreak(0);
                savePlayerData(playerData); // Save initial data
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error loading player data from SQLite for " + uuid.toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void savePlayerData(PlayerData playerData) {
        String sql = "INSERT OR REPLACE INTO player_rewards (uuid, last_claimed_day, current_streak) VALUES (?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerData.getUuid().toString());
            statement.setLong(2, playerData.getLastClaimedDay());
            statement.setInt(3, playerData.getCurrentStreak());
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving player data to SQLite for " + playerData.getUuid().toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Error closing SQLite connection: " + e.getMessage());
            }
        }
    }
}