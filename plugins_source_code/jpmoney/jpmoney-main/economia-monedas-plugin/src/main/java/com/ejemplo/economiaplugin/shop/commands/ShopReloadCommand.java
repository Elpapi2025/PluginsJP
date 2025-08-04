package com.ejemplo.economiaplugin.shop.commands;

import com.ejemplo.economiaplugin.shop.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopReloadCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public ShopReloadCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economia.shop.reload")) {
            sender.sendMessage("§cNo tienes permiso para recargar la tienda.");
            return true;
        }

        shopManager.reload();
        sender.sendMessage("§aLa tienda ha sido recargada correctamente.");
        return true;
    }
}