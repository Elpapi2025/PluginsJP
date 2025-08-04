package com.ejemplo.economiaplugin.data;

import com.ejemplo.economiaplugin.EconomiaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteDataManager implements DataManager {

    private final EconomiaPlugin plugin;
    private Connection connection;

    public SQLiteDataManager(EconomiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        File dataFolder = new File(plugin.getDataFolder(), "database");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + new File(dataFolder, "balances.db"));
            createTable();
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("No se pudo conectar a la base de datos SQLite.");
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
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO player_balances (player_uuid, currency_id, balance) VALUES (?, ?, ?)")) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, currencyId);
            ps.setDouble(3, balance);
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
