package com.ejemplo.economiaplugin.shop.managers;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.ejemplo.economiaplugin.shop.models.ShopItem;
import com.ejemplo.economiaplugin.shop.models.ShopSection;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopManager {

    private final EconomiaPlugin plugin;
    private final Map<String, ShopSection> sections;
    private final Map<String, ShopItem> shopItems; // Todos los items disponibles referenciados por su ID

    public ShopManager(EconomiaPlugin plugin) {
        this.plugin = plugin;
        this.sections = new HashMap<>();
        this.shopItems = new HashMap<>();
        cargarSeccionesDesdeConfig();
    }

    public void reload() {
        this.sections.clear();
        this.shopItems.clear();
        plugin.saveResource("shops.yml", false); // Asegura que el archivo exista
        cargarSeccionesDesdeConfig();
        plugin.getLogger().info("Tienda recargada correctamente.");
    }

    private void cargarSeccionesDesdeConfig() {
        FileConfiguration shopsConfig = plugin.getShopsConfig();

        // Cargar ítems globales
        ConfigurationSection itemsSection = shopsConfig.getConfigurationSection("items");
        if (itemsSection != null) {
            Set<String> itemIds = itemsSection.getKeys(false);
            for (String itemId : itemIds) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(itemId);
                if (itemConfig == null) continue;

                String material = itemConfig.getString("material");
                int amount = itemConfig.getInt("amount", 1);
                String name = itemConfig.getString("name", "");
                List<String> lore = itemConfig.getStringList("lore");
                String action = itemConfig.getString("action", "");
                String targetShop = itemConfig.getString("target_shop", "");
                String currencyType = itemConfig.getString("currency_type", "");
                double price = itemConfig.getDouble("price", 0.0);
                List<String> commands = itemConfig.getStringList("commands");

                ShopItem shopItem;
                if (action.equalsIgnoreCase("buy_item")) {
                    shopItem = new ShopItem(itemId, material, amount, name, lore, action, currencyType, price, commands);
                } else {
                    shopItem = new ShopItem(itemId, material, amount, name, lore, action, targetShop);
                }
                shopItems.put(itemId, shopItem);
                plugin.getLogger().info("Shop Item cargado: " + name + " (" + itemId + ")");
            }
        }

        // Cargar secciones de la tienda
        ConfigurationSection sectionsSection = shopsConfig.getConfigurationSection("shops");
        if (sectionsSection != null) {
            Set<String> sectionIds = sectionsSection.getKeys(false);
            for (String sectionId : sectionIds) {
                ConfigurationSection sectionConfig = sectionsSection.getConfigurationSection(sectionId);
                if (sectionConfig == null) continue;

                String title = ChatColor.translateAlternateColorCodes('&', sectionConfig.getString("title", "Tienda"));
                int size = sectionConfig.getInt("size", 27);
                ConfigurationSection sectionItemsConfig = sectionConfig.getConfigurationSection("items");

                Map<Integer, String> currentSectionItems = new HashMap<>();
                if (sectionItemsConfig != null) {
                    sectionItemsConfig.getKeys(false).forEach(slot -> {
                        currentSectionItems.put(Integer.parseInt(slot), sectionItemsConfig.getString(slot));
                    });
                }
                ShopSection shopSection = new ShopSection(sectionId, title, size, currentSectionItems);
                sections.put(sectionId, shopSection);
                plugin.getLogger().info("Sección de tienda cargada: " + title + " (" + sectionId + ")");
            }
        }
    }

    public Map<String, ShopItem> getAvailableItems() {
        return shopItems;
    }

    public Map<String, ShopSection> getSections() {
        return sections;
    }

    public ShopItem getShopItem(String itemId) {
        return shopItems.get(itemId);
    }
}