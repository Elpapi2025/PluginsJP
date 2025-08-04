package com.ejemplo.economiaplugin.shop.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ShopItem {
    private String id;
    private Material material;
    private int amount;
    private String name;
    private List<String> lore;
    private String action; // close, open_shop, buy_item
    private String targetShop; // For open_shop action
    private String currencyType; // For buy_item action
    private double price; // For buy_item action
    private List<String> commands; // For buy_item action

    // Constructor para ítems de decoración/navegación
    public ShopItem(String id, String material, int amount, String name, List<String> lore, String action, String targetShop) {
        this.id = id;
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.lore = lore != null ? lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()) : null;
        this.action = action;
        this.targetShop = targetShop;
    }

    // Constructor para ítems de compra
    public ShopItem(String id, String material, int amount, String name, List<String> lore, String action, String currencyType, double price, List<String> commands) {
        this.id = id;
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.lore = lore != null ? lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()) : null;
        this.action = action;
        this.currencyType = currencyType;
        this.price = price;
        this.commands = commands;
    }

    // Getters
    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public int getAmount() { return amount; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public String getAction() { return action; }
    public String getTargetShop() { return targetShop; }
    public String getCurrencyType() { return currencyType; }
    public double getPrice() { return price; }
    public List<String> getCommands() { return commands; }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}