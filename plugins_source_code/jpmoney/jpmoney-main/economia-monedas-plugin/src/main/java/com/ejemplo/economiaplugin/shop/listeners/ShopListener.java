package com.ejemplo.economiaplugin.shop.listeners;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.ejemplo.economiaplugin.managers.MonedaManager;
import com.ejemplo.economiaplugin.shop.managers.ShopManager;
import com.ejemplo.economiaplugin.shop.models.ShopGUI;
import com.ejemplo.economiaplugin.shop.models.ShopItem;
import com.ejemplo.economiaplugin.shop.models.ShopSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer; // Importación agregada
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.ejemplo.economiaplugin.shop.models.QuantityGUI;
import com.ejemplo.economiaplugin.utils.ColorUtils; // Añadir este import

public class ShopListener implements Listener {

    private final EconomiaPlugin plugin;
    private final MonedaManager monedaManager;
    private final ShopManager shopManager;
    private final Map<UUID, QuantityGUI> quantityGUIs = new ConcurrentHashMap<>();

    public ShopListener(EconomiaPlugin plugin, MonedaManager monedaManager, ShopManager shopManager) {
        this.plugin = plugin;
        this.monedaManager = monedaManager;
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();

        if (event.getView().getTitle().equals("Seleccionar Cantidad")) {
            event.setCancelled(true);
            QuantityGUI quantityGUI = quantityGUIs.get(playerUUID);
            if (quantityGUI == null) return;

            int slot = event.getRawSlot();
            switch (slot) {
                case 10: // -10
                    quantityGUI.setQuantity(quantityGUI.getQuantity() - 10);
                    break;
                case 11: // -1
                    quantityGUI.setQuantity(quantityGUI.getQuantity() - 1);
                    break;
                case 15: // +1
                    quantityGUI.setQuantity(quantityGUI.getQuantity() + 1);
                    break;
                case 16: // +10
                    quantityGUI.setQuantity(quantityGUI.getQuantity() + 10);
                    break;
                case 22: // Confirmar
                    ShopItem shopItem = quantityGUI.getShopItem();
                    int quantity = quantityGUI.getQuantity();
                    double totalPrice = shopItem.getPrice() * quantity;
                    String currencyName = shopItem.getCurrencyType();

                    if (monedaManager.getSaldo(playerUUID, currencyName) >= totalPrice) {
                        monedaManager.removeSaldo(playerUUID, currencyName, totalPrice);
                        for (String cmd : shopItem.getCommands()) {
                            String commandToExecute = cmd.replace("%player%", player.getName()).replace("%quantity%", String.valueOf(quantity));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                        }
                        player.sendMessage(ColorUtils.translateColorCodes("&aHas comprado " + quantity + " " + shopItem.getName() + " por " + totalPrice + " " + currencyName + "."));
                    } else {
                        player.sendMessage(ColorUtils.translateColorCodes("&cNo tienes suficiente dinero para comprar esto."));
                    }
                    player.closeInventory();
                    quantityGUIs.remove(playerUUID);
                    return;
            }
            player.getOpenInventory().getTopInventory().setContents(quantityGUI.getInventory().getContents());
            return;
        }

        ShopSection currentSection = shopManager.getSections().values().stream()
                .filter(s -> ChatColor.stripColor(s.getTitle()).equals(ChatColor.stripColor(event.getView().getTitle())))
                .findFirst()
                .orElse(null);

        if (currentSection == null) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= currentSection.getSize()) return;

        String itemId = currentSection.getItems().get(slot);
        if (itemId == null) return;

        ShopItem shopItem = shopManager.getShopItem(itemId);
        if (shopItem == null) {
            player.sendMessage(ColorUtils.translateColorCodes("&cError: El item de la tienda no se encontró. Contacta a un administrador."));
            return;
        }

        // Handle actions
        switch (shopItem.getAction().toLowerCase()) {
            case "close":
                player.closeInventory();
                break;
            case "back":
                ShopSection previousSection = shopManager.getSections().get("main_menu");
                if (previousSection != null) {
                    ShopGUI gui = new ShopGUI(previousSection.getId(), previousSection.getTitle(), previousSection.getSize(), previousSection.getItems());
                    player.openInventory(gui.createInventory(shopManager.getAvailableItems()));
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes("&cError: No se encontró la sección del menú principal para volver."));
                }
                break;
            case "open_shop":
                String targetShopId = shopItem.getTargetShop();
                ShopSection targetSection = shopManager.getSections().get(targetShopId);
                if (targetSection != null) {
                    ShopGUI gui = new ShopGUI(targetSection.getId(), targetSection.getTitle(), targetSection.getSize(), targetSection.getItems());
                    player.openInventory(gui.createInventory(shopManager.getAvailableItems()));
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes("&cError: No se encontró la sección de tienda: " + targetShopId));
                }
                break;
            case "buy_item":
                QuantityGUI quantityGUI = new QuantityGUI(shopItem);
                quantityGUIs.put(playerUUID, quantityGUI);
                player.openInventory(quantityGUI.getInventory());
                break;
            default:
                player.sendMessage(ColorUtils.translateColorCodes("&cAcción no reconocida para este ítem."));
                break;
        }
    }
}