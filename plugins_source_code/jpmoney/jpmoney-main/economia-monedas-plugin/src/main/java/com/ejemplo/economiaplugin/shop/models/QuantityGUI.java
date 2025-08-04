package com.ejemplo.economiaplugin.shop.models;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class QuantityGUI {

    private final ShopItem shopItem;
    private int quantity = 1;

    public QuantityGUI(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, "Seleccionar Cantidad");

        // Botones de control
        inv.setItem(10, createControlButton(Material.REDSTONE_BLOCK, -10, "-10"));
        inv.setItem(11, createControlButton(Material.REDSTONE, -1, "-1"));
        inv.setItem(15, createControlButton(Material.EMERALD, 1, "+1"));
        inv.setItem(16, createControlButton(Material.EMERALD_BLOCK, 10, "+10"));

        // Item a comprar
        inv.setItem(13, shopItem.toItemStack());

        // Botón de confirmar
        inv.setItem(22, createConfirmButton());

        return inv;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    private ItemStack createControlButton(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, Math.abs(amount));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createConfirmButton() {
        ItemStack item = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aConfirmar Compra");
        meta.setLore(Arrays.asList(
                "§7Cantidad: §e" + quantity,
                "§7Precio Total: §e" + (shopItem.getPrice() * quantity) + " " + shopItem.getCurrencyType()
        ));
        item.setItemMeta(meta);
        return item;
    }
}
