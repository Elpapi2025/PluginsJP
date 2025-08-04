package com.ejemplo.economiaplugin.data;

import com.ejemplo.economiaplugin.EconomiaPlugin;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MySQLDataManager implements DataManager {

    private final EconomiaPlugin plugin;
    private Connection connection;

    public MySQLDataManager(EconomiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + plugin.getConfig().getString("database.mysql.host") +
                            ":" + plugin.getConfig().getInt("database.mysql.port") +
                            "/" + plugin.getConfig().getString("database.mysql.database"),
                    plugin.getConfig().getString("database.mysql.user"),
                    plugin.getConfig().getString("database.mysql.password")
            );
            createTable();
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("No se pudo conectar a la base de datos MySQL.");
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Double> getBalances(UUID playerUUID) {
        Map<String, Double> balances = new ConcurrentHashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT currency_id, balance FROM player_balances WHERE player_uuid = ?")) {
            ps.setString(1, playerUUID.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                balances.put(rs.getString("currency_id"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("No se pudo obtener los saldos del jugador: " + playerUUID);
            e.printStackTrace();
        }
        return balances;
    }

    @Override
    public double getBalance(UUID playerUUID, String currencyId) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT balance FROM player_balances WHERE player_uuid = ? AND currency_id = ?")) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, currencyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("No se pudo obtener el saldo del jugador: " + playerUUID);
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public void setBalance(UUID playerUUID, String currencyId, double balance) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO player_balances (player_uuid, currency_id, balance) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE balance = ?")) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, currencyId);
            ps.setDouble(3, balance);
            ps.setDouble(4, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("No se pudo establecer el saldo del jugador: " + playerUUID);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_balances (" +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "currency_id VARCHAR(255) NOT NULL," +
                    "balance DOUBLE PRECISION NOT NULL," +
                    "PRIMARY KEY (player_uuid, currency_id)" +
                    ");");
        }
    }
}
