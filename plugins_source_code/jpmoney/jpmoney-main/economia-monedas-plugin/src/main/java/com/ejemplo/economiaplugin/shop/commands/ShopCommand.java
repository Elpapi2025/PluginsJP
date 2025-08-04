package com.ejemplo.economiaplugin.shop.commands;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.ejemplo.economiaplugin.shop.managers.ShopManager;
import com.ejemplo.economiaplugin.shop.models.ShopGUI;
import com.ejemplo.economiaplugin.shop.models.ShopSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final EconomiaPlugin plugin;
    private final ShopManager shopManager;

    public ShopCommand(EconomiaPlugin plugin, ShopManager shopManager) {
        this.plugin = plugin;
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("economia.shop")) {
            player.sendMessage("§cNo tienes permiso para usar la tienda.");
            return true;
        }

        // Open the main shop menu
        ShopSection mainShop = shopManager.getSections().get("main_menu");
        if (mainShop != null) {
            ShopGUI gui = new ShopGUI(mainShop.getId(), mainShop.getTitle(), mainShop.getSize(), mainShop.getItems());
            player.openInventory(gui.createInventory(shopManager.getAvailableItems()));
        } else {
            player.sendMessage("§cError: El menú principal de la tienda no está configurado correctamente. Contacta a un administrador.");
        }

        return true;
    }
}