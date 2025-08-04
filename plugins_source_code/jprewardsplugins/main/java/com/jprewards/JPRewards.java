package com.jprewards;

import com.jprewards.data.DataManager;
import com.jprewards.data.JsonDataManager;
import com.jprewards.data.MySQLDataManager;
import com.jprewards.data.SQLiteDataManager;
import com.jprewards.expansion.JPRewardsExpansion; // Added import
import org.bukkit.Bukkit; // Added import
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class JPRewards extends JavaPlugin {

    private static JPRewards instance;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Guardar la configuración por defecto si no existe
        saveDefaultConfig();

        // Inicializar el manejador de datos
        String storageType = getConfig().getString("storage_type", "JSON").toUpperCase();
        switch (storageType) {
            case "SQLITE":
                dataManager = new SQLiteDataManager(this);
                break;
            case "MYSQL":
                dataManager = new MySQLDataManager(this);
                break;
            case "JSON":
            default:
                dataManager = new JsonDataManager(this);
                break;
        }
        dataManager.createTable(); // Crea la tabla si es necesario (para SQL) o el directorio (para JSON)

        // Registrar comando y listeners
        getCommand("rewards").setExecutor(new JPRewardsCommand(this));
        getServer().getPluginManager().registerEvents(new RewardsGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Registrar PlaceholderAPI si está disponible
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new JPRewardsExpansion(this).register();
            getLogger().info("JPRewards ha registrado su expansión de PlaceholderAPI.");
        }

        getLogger().info("JPRewards ha sido habilitado correctamente con almacenamiento " + storageType + ".");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.closeConnection();
        }
        getLogger().info("JPRewards ha sido deshabilitado.");
    }

    public static JPRewards getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}