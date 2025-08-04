package com.ejemplo.economiaplugin.managers;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.ejemplo.economiaplugin.data.DataManager;
import com.ejemplo.economiaplugin.models.Moneda;
import com.ejemplo.economiaplugin.utils.ColorUtils; // Importación correcta
import org.bukkit.OfflinePlayer; // Importación necesaria para el nuevo tipo de argumento
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonedaManager {

    private final EconomiaPlugin plugin;
    private final DataManager dataManager;
    private Map<String, Moneda> monedasDisponibles;

    public MonedaManager(EconomiaPlugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.monedasDisponibles = new ConcurrentHashMap<>();
        cargarMonedas();
    }

    public void cargarMonedas() {
        this.monedasDisponibles.clear();
        FileConfiguration config = plugin.getMonedasConfig();
        ConfigurationSection monedasSection = config.getConfigurationSection("monedas");
        if (monedasSection == null) {
            plugin.getLogger().warning("No se encontraron monedas en monedas.yml. Asegúrate de que el archivo no esté vacío o malformado.");
            return;
        }

        for (String id : monedasSection.getKeys(false)) {
            ConfigurationSection monedaSection = monedasSection.getConfigurationSection(id);
            if (monedaSection != null) {
                String name = monedaSection.getString("name");
                String symbol = monedaSection.getString("symbol");
                String commandAlias = monedaSection.getString("command", null);
                double defaultBalance = monedaSection.getDouble("default-balance", 0.0);
                boolean isDefault = monedaSection.getBoolean("isDefault", false);
                int decimalPlaces = monedaSection.getInt("decimalPlaces", 0);
                String permissionCheck = monedaSection.getString("permissions.check", null);
                String permissionPay = monedaSection.getString("permissions.pay", null);
                String permissionAdmin = monedaSection.getString("permissions.admin", null);
                boolean requiresPermission = monedaSection.getBoolean("requiresPermission", false);

                Moneda moneda = new Moneda(id, name, symbol, commandAlias, defaultBalance, isDefault, decimalPlaces, permissionCheck, permissionPay, permissionAdmin, requiresPermission);
                this.monedasDisponibles.put(id, moneda);
                plugin.getLogger().info(ColorUtils.translateColorCodes("Moneda cargada: " + name + " con alias de comando: /" + commandAlias));
            }
        }
    }

    public Map<String, Moneda> getMonedasDisponibles() {
        return monedasDisponibles;
    }

    public Moneda getMoneda(String id) {
        return monedasDisponibles.get(id.toLowerCase());
    }

    public boolean existeMoneda(String id) {
        return monedasDisponibles.containsKey(id.toLowerCase());
    }

    public double getSaldo(UUID jugadorUUID, String monedaId) {
        return dataManager.getBalance(jugadorUUID, monedaId);
    }

    public void setSaldo(UUID jugadorUUID, String monedaId, double cantidad) {
        dataManager.setBalance(jugadorUUID, monedaId, cantidad);
    }

    public void addSaldo(UUID jugadorUUID, String monedaId, double cantidad) {
        double saldoActual = getSaldo(jugadorUUID, monedaId);
        setSaldo(jugadorUUID, monedaId, saldoActual + cantidad);
    }

    public boolean removeSaldo(UUID jugadorUUID, String monedaId, double cantidad) {
        double saldoActual = getSaldo(jugadorUUID, monedaId);
        if (saldoActual >= cantidad) {
            setSaldo(jugadorUUID, monedaId, saldoActual - cantidad);
            return true;
        }
        return false;
    }

    public boolean hasSaldo(UUID jugadorUUID, String monedaId, double cantidad) {
        return getSaldo(jugadorUUID, monedaId) >= cantidad;
    }

    public String format(double cantidad) {
        // Implementación simple de formato
        return String.format("%.2f", cantidad);
    }
}
