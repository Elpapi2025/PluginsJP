package com.ejemplo.economiaplugin.data;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JsonDataManager implements DataManager {

    private final EconomiaPlugin plugin;
    private final File dataFolder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Map<String, Double>> allBalances = new ConcurrentHashMap<>();

    public JsonDataManager(EconomiaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
    }

    @Override
    public void setup() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        loadAllBalances();
    }

    @Override
    public Map<String, Double> getBalances(UUID playerUUID) {
        return allBalances.getOrDefault(playerUUID, new ConcurrentHashMap<>());
    }

    @Override
    public double getBalance(UUID playerUUID, String currencyId) {
        return getBalances(playerUUID).getOrDefault(currencyId, 0.0);
    }

    @Override
    public void setBalance(UUID playerUUID, String currencyId, double balance) {
        Map<String, Double> playerBalances = allBalances.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>());
        playerBalances.put(currencyId, balance);
        saveBalances(playerUUID);
    }

    @Override
    public void close() {
        // No es necesario hacer nada para JSON
    }

    private void loadAllBalances() {
        File[] playerFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (playerFiles == null) return;

        for (File playerFile : playerFiles) {
            try (FileReader reader = new FileReader(playerFile)) {
                Type type = new TypeToken<Map<String, Double>>() {}.getType();
                Map<String, Double> balances = gson.fromJson(reader, type);
                UUID playerUUID = UUID.fromString(playerFile.getName().replace(".json", ""));
                allBalances.put(playerUUID, new ConcurrentHashMap<>(balances));
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo cargar el archivo de datos de un jugador: " + playerFile.getName());
                e.printStackTrace();
            }
        }
    }

    private void saveBalances(UUID playerUUID) {
        File playerFile = new File(dataFolder, playerUUID.toString() + ".json");
        try (FileWriter writer = new FileWriter(playerFile)) {
            gson.toJson(getBalances(playerUUID), writer);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar el archivo de datos del jugador: " + playerUUID);
            e.printStackTrace();
        }
    }
}
