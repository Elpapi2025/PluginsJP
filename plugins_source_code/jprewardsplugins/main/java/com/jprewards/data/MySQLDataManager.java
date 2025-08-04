package com.jprewards.data;

import com.jprewards.JPRewards;

import java.sql.*;
import java.util.UUID;

public class MySQLDataManager implements DataManager {

    private final JPRewards plugin;
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLDataManager(JPRewards plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfig().getString("database.mysql.host", "localhost");
        this.port = plugin.getConfig().getInt("database.mysql.port", 3306);
        this.database = plugin.getConfig().getString("database.mysql.database", "jprewards");
        this.username = plugin.getConfig().getString("database.mysql.username", "root");
        this.password = plugin.getConfig().getString("database.mysql.password", "password");
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver"); // Use com.mysql.cj.jdbc.Driver for MySQL 8+
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
                connection = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                plugin.getLogger().severe("MySQL JDBC driver not found! " + e.getMessage());
            }
        }
        return connection;
    }

    @Override
    public void createTable() {
        try (Statement statement = getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_rewards (" +
                         "uuid VARCHAR(36) PRIMARY KEY," +
                         "last_claimed_day BIGINT NOT NULL," +
                         "current_streak INT NOT NULL" +
                         ");";
            statement.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error creating MySQL table: " + e.getMessage());
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
            plugin.getLogger().severe("Error loading player data from MySQL for " + uuid.toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void savePlayerData(PlayerData playerData) {
        String sql = "INSERT INTO player_rewards (uuid, last_claimed_day, current_streak) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE last_claimed_day = VALUES(last_claimed_day), current_streak = VALUES(current_streak)";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerData.getUuid().toString());
            statement.setLong(2, playerData.getLastClaimedDay());
            statement.setInt(3, playerData.getCurrentStreak());
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving player data to MySQL for " + playerData.getUuid().toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Error closing MySQL connection: " + e.getMessage());
            }
        }
    }
}