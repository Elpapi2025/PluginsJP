package com.ejemplo.economiaplugin.commands.custom;

import com.ejemplo.economiaplugin.EconomiaPlugin;
import com.ejemplo.economiaplugin.managers.MonedaManager;
import com.ejemplo.economiaplugin.models.Moneda;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.ejemplo.economiaplugin.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CustomMonedaCommand implements CommandExecutor, TabCompleter {

    private final MonedaManager monedaManager;
    private final Moneda moneda;
    private final EconomiaPlugin plugin;

    public CustomMonedaCommand(MonedaManager monedaManager, Moneda moneda, EconomiaPlugin plugin) {
        this.monedaManager = monedaManager;
        this.moneda = moneda;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.translateColorCodes("&cEste comando solo puede ser ejecutado por un jugador."));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            // Mostrar saldo del jugador
            double balance = monedaManager.getSaldo(playerUUID, moneda.getId());
            player.sendMessage(ColorUtils.translateColorCodes("&aTu saldo de " + moneda.getName() + " es: &e" + String.format("%." + moneda.getDecimalPlaces() + "f", balance) + " " + moneda.getSymbol() + "&a."));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "check":
                if (moneda.requiresPermission() && (moneda.getPermissionCheck() == null || !player.hasPermission(moneda.getPermissionCheck()))) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cNo tienes permiso para ver el saldo de otros jugadores."));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cUso: /" + moneda.getCommandAlias() + " check <jugador>"));
                    return true;
                }
                String targetPlayerName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
                if (targetPlayer == null) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cJugador no encontrado."));
                    return true;
                }
                UUID targetPlayerUUID = targetPlayer.getUniqueId();
                double targetBalance = monedaManager.getSaldo(targetPlayerUUID, moneda.getId());
                player.sendMessage(ColorUtils.translateColorCodes("&aSaldo de " + targetPlayerName + " en " + moneda.getName() + ": &e" + String.format("%." + moneda.getDecimalPlaces() + "f", targetBalance) + " " + moneda.getSymbol() + "&a."));
                return true;

            case "pay":
                if (moneda.requiresPermission() && (moneda.getPermissionPay() == null || !player.hasPermission(moneda.getPermissionPay()))) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cNo tienes permiso para pagar con esta moneda."));
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cUso: /" + moneda.getCommandAlias() + " pay <jugador> <cantidad>"));
                    return true;
                }
                targetPlayerName = args[1];
                targetPlayer = Bukkit.getPlayer(targetPlayerName);
                if (targetPlayer == null) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cJugador no encontrado."));
                    return true;
                }
                UUID targetPlayerUUIDPay = targetPlayer.getUniqueId();
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cCantidad inválida."));
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cLa cantidad debe ser mayor que cero."));
                    return true;
                }

                if (monedaManager.getSaldo(playerUUID, moneda.getId()) < amount) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cNo tienes suficiente " + moneda.getName() + " para hacer este pago."));
                    return true;
                }

                if (monedaManager.removeSaldo(playerUUID, moneda.getId(), amount)) {
                    monedaManager.addSaldo(targetPlayerUUIDPay, moneda.getId(), amount);
                    player.sendMessage(ColorUtils.translateColorCodes("&aHas pagado " + amount + " " + moneda.getSymbol() + " " + moneda.getName() + " a " + targetPlayerName + "."));
                    targetPlayer.sendMessage(ColorUtils.translateColorCodes("&aHas recibido " + amount + " " + moneda.getSymbol() + " " + moneda.getName() + " de " + player.getName() + "."));
                } else {
                    player.sendMessage(ColorUtils.translateColorCodes("&cError al procesar el pago."));
                }
                return true;

            case "add":
            case "set":
            case "remove":
                if (moneda.requiresPermission() && (moneda.getPermissionAdmin() == null || !player.hasPermission(moneda.getPermissionAdmin()))) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cNo tienes permiso para esta acción administrativa."));
                    return true;
                }
                if (args.length < 4) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cUso: /" + moneda.getCommandAlias() + " <add|set|remove> <jugador> <cantidad>"));
                    return true;
                }
                targetPlayerName = args[1];
                targetPlayer = Bukkit.getPlayer(targetPlayerName);
                if (targetPlayer == null) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cJugador no encontrado."));
                    return true;
                }
                UUID targetPlayerUUIDAdmin = targetPlayer.getUniqueId();
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ColorUtils.translateColorCodes("&cCantidad inválida."));
                    return true;
                }

                switch (subCommand) {
                    case "add":
                        monedaManager.addSaldo(targetPlayerUUIDAdmin, moneda.getId(), amount);
                        player.sendMessage(ColorUtils.translateColorCodes("&aSe han añadido " + amount + " " + moneda.getSymbol() + " " + moneda.getName() + " a " + targetPlayerName + "."));
                        break;
                    case "set":
                        monedaManager.setSaldo(targetPlayerUUIDAdmin, moneda.getId(), amount);
                        player.sendMessage(ColorUtils.translateColorCodes("&aEl saldo de " + targetPlayerName + " en " + moneda.getName() + " se ha establecido a " + amount + " " + moneda.getSymbol() + "."));
                        break;
                    case "remove":
                        if (monedaManager.removeSaldo(targetPlayerUUIDAdmin, moneda.getId(), amount)) {
                            player.sendMessage(ColorUtils.translateColorCodes("&aSe han removido " + amount + " " + moneda.getSymbol() + " " + moneda.getName() + " de " + targetPlayerName + "."));
                        }
                        break;
                }
                return true;

            default:
                player.sendMessage(ColorUtils.translateColorCodes("&cUso: /" + moneda.getCommandAlias() + " [check|pay|add|set|remove]"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return Arrays.asList("check", "pay", "add", "set", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("pay") ||
                args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") ||
                args[0].equalsIgnoreCase("remove")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return completions;
    }
}