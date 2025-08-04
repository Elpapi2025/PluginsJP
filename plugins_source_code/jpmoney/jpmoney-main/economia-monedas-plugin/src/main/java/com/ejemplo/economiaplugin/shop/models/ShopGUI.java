package com.ejemplo.economiaplugin.shop.models;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.Map;

public class ShopGUI {
    private String id;
    private String title;
    private int size;
    private Map<Integer, String> itemIds; // Slot -> ItemId

    public ShopGUI(String id, String title, int size, Map<Integer, String> itemIds) {
        this.id = id;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.size = size;
        this.itemIds = itemIds;
    }

    public Inventory createInventory(Map<String, ShopItem> availableItems) {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (Map.Entry<Integer, String> entry : itemIds.entrySet()) {
            int slot = entry.getKey();
            String itemId = entry.getValue();

            ShopItem shopItem = availableItems.get(itemId);
            if (shopItem != null) {
                inventory.setItem(slot, shopItem.toItemStack());
            } else {
                // Fallback for undefined items, use a default decor_pane
                ShopItem decorPane = availableItems.get("decor_pane");
                if (decorPane != null) {
                    inventory.setItem(slot, decorPane.toItemStack());
                }
            }
        }
        return inventory;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, String> getItemIds() {
        return itemIds;
    }
}