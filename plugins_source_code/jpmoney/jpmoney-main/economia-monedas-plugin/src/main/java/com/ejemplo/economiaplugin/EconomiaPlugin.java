package com.ejemplo.economiaplugin;

import com.ejemplo.economiaplugin.commands.MonedaCommand;
import com.ejemplo.economiaplugin.commands.custom.CustomMonedaCommand;
import com.ejemplo.economiaplugin.managers.MonedaManager;
import com.ejemplo.economiaplugin.models.Moneda;
import com.ejemplo.economiaplugin.shop.commands.ShopCommand;
import com.ejemplo.economiaplugin.shop.commands.ShopReloadCommand;
import com.ejemplo.economiaplugin.shop.listeners.ShopListener;
import com.ejemplo.economiaplugin.shop.managers.ShopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import com.ejemplo.economiaplugin.data.DataManager;
import com.ejemplo.economiaplugin.data.JsonDataManager;
import com.ejemplo.economiaplugin.data.MySQLDataManager;
import com.ejemplo.economiaplugin.data.SQLiteDataManager;
import com.ejemplo.economiaplugin.utils.ColorUtils;

public class EconomiaPlugin extends JavaPlugin implements Listener {

    private MonedaManager monedaManager;
    private ShopManager shopManager;
    private DataManager dataManager;
    private File monedasConfigFile;
    private FileConfiguration monedasConfig;
    private File shopsConfigFile;
    private FileConfiguration shopsConfig;
    private Economy econ = null;


    @Override
    public void onEnable() {
        getLogger().info("EconomiaPlugin habilitado.");

        saveDefaultConfig();

        crearMonedasConfig();
        crearShopsConfig();

        setupDataManager();

        this.monedaManager = new MonedaManager(this, dataManager);
        this.shopManager = new com.ejemplo.economiaplugin.shop.managers.ShopManager(this);

        getCommand("moneda").setExecutor(new com.ejemplo.economiaplugin.commands.MonedaCommand(monedaManager));

        registrarComandosPersonalizados();

        getServer().getPluginManager().registerEvents(new com.ejemplo.economiaplugin.shop.listeners.ShopListener(this, monedaManager, shopManager), this);

        getCommand("shop").setExecutor(new com.ejemplo.economiaplugin.shop.commands.ShopCommand(this, shopManager));

        getCommand("shopreload").setExecutor(new com.ejemplo.economiaplugin.shop.commands.ShopReloadCommand(shopManager));

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().runTaskLater(this, this::setupEconomy, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().info("EconomiaPlugin deshabilitado.");
        if (dataManager != null) {
            dataManager.close();
        }
    }

    private void setupDataManager() {
        String storageType = getConfig().getString("database.type", "json").toUpperCase();
        switch (storageType) {
            case "MYSQL":
                dataManager = new MySQLDataManager(this);
                break;
            case "SQLITE":
                dataManager = new SQLiteDataManager(this);
                break;
            default:
                dataManager = new JsonDataManager(this);
                break;
        }
        dataManager.setup();
    }

    private void crearMonedasConfig() {
        monedasConfigFile = new File(getDataFolder(), "monedas.yml");
        if (!monedasConfigFile.exists()) {
            monedasConfigFile.getParentFile().mkdirs();
            saveResource("monedas.yml", true);
        }
        monedasConfig = YamlConfiguration.loadConfiguration(monedasConfigFile);
        getLogger().info("DEBUG: monedas.yml loaded. Contains 'monedas' section: " + monedasConfig.isConfigurationSection("monedas"));

        InputStream defaultStream = getResource("monedas.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(defaultStream));
            monedasConfig.setDefaults(defaultConfig);
            monedasConfig.options().copyDefaults(true);
            try {
                monedasConfig.save(monedasConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void crearShopsConfig() {
        shopsConfigFile = new File(getDataFolder(), "shops.yml");
        if (!shopsConfigFile.exists()) {
            shopsConfigFile.getParentFile().mkdirs();
            saveResource("shops.yml", false);
        }
        shopsConfig = YamlConfiguration.loadConfiguration(shopsConfigFile);

        InputStream defaultStream = getResource("shops.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(defaultStream));
            shopsConfig.setDefaults(defaultConfig);
            shopsConfig.options().copyDefaults(true);
            try {
                shopsConfig.save(shopsConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getMonedasConfig() {
        return this.monedasConfig;
    }

    public FileConfiguration getShopsConfig() {
        return this.shopsConfig;
    }

    private void registrarComandosPersonalizados() {
        try {
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer().getPluginManager());

            for (Moneda moneda : monedaManager.getMonedasDisponibles().values()) {
                String alias = moneda.getCommandAlias();
                if (alias != null && !alias.isEmpty()) {
                    PluginCommand command = getCommand(alias);
                    if (command == null) {
                        command = (PluginCommand) Class.forName("org.bukkit.command.PluginCommand").getConstructor(String.class, Plugin.class).newInstance(alias, this);
                        commandMap.register(this.getDescription().getName(), command);
                    }
                    CustomMonedaCommand customExecutor = new CustomMonedaCommand(monedaManager, moneda, this);
                    command.setExecutor(customExecutor);
                    command.setTabCompleter(customExecutor);
                    getLogger().info(ColorUtils.translateColorCodes("Comando personalizado registrado: /" + alias + " para moneda: " + moneda.getName()));
                }
            }
        } catch (Exception e) {
            getLogger().severe("Error al registrar comandos personalizados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("DEBUG: Vault plugin is NULL. (This is expected if Vault is not installed)");
            return false;
        }
        econ = getServer().getServicesManager().getRegistration(Economy.class) != null ? getServer().getServicesManager().getRegistration(Economy.class).getProvider() : null;

        if (econ == null) {
            getLogger().warning("DEBUG: Economy provider is NULL. Vault's Economy service might not be fully loaded or a compatible economy plugin is not found.");
            return false;
        }
        getLogger().info("DEBUG: Economy provider found. Vault hooked successfully!");
        return true;
    }

    public Economy getEconomy() {
        return econ;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("Vault")) {
            getLogger().info("Vault ha sido habilitado. Intentando conectar la economía...");
            setupEconomy();
        }
    }
}