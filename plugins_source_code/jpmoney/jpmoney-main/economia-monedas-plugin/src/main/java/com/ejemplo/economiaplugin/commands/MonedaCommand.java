package com.ejemplo.economiaplugin.commands;

import com.ejemplo.economiaplugin.managers.MonedaManager;
import com.ejemplo.economiaplugin.models.Moneda;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ejemplo.economiaplugin.utils.ColorUtils; // Añadir este import

public class MonedaCommand implements CommandExecutor, TabCompleter {

    private final MonedaManager monedaManager;

    public MonedaCommand(MonedaManager monedaManager) {
        this.monedaManager = monedaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (subCommand.equals("list")) {
            sender.sendMessage(ColorUtils.translateColorCodes("&aMonedas disponibles:"));
            for (Moneda moneda : monedaManager.getMonedasDisponibles().values()) {
                sender.sendMessage(ColorUtils.translateColorCodes("&e - " + moneda.getName() + " (&f" + moneda.getId() + "&e) Símbolo: " + moneda.getSymbol()));
            }
            return true;
        }

        if (subCommand.equals("add") || subCommand.equals("set") || subCommand.equals("remove")) {
            if (args.length < 4) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cUso incorrecto. Uso: /moneda <add|set|remove> <ID_moneda> <jugador> <cantidad>"));
                return true;
            }

            String monedaId = args[1].toLowerCase();
            String targetPlayerName = args[2];
            double amount;
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cLa cantidad debe ser un número válido."));
                return true;
            }

            Moneda monedaCheck = monedaManager.getMoneda(monedaId);
            if (monedaCheck == null) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cLa moneda con ID '" + monedaId + "' no existe."));
                return true;
            }

            // Obtener el jugador por nombre y su UUID para un manejo correcto
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cEl jugador '" + targetPlayerName + "' no está en línea o no existe."));
                return true;
            }
            UUID targetPlayerUUID = targetPlayer.getUniqueId();

            switch (subCommand) {
                case "add":
                    monedaManager.addSaldo(targetPlayerUUID, monedaId, amount);
                    sender.sendMessage(ColorUtils.translateColorCodes("&aSe han añadido " + amount + " " + monedaCheck.getSymbol() + " " + monedaCheck.getName() + " a " + targetPlayerName + "."));
                    break;
                case "set":
                    monedaManager.setSaldo(targetPlayerUUID, monedaId, amount);
                    sender.sendMessage(ColorUtils.translateColorCodes("&aEl saldo de " + targetPlayerName + " en " + monedaCheck.getName() + " se ha establecido a " + amount + " " + monedaCheck.getSymbol() + "."));
                    break;
                case "remove":
                    if (monedaManager.removeSaldo(targetPlayerUUID, monedaId, amount)) {
                        sender.sendMessage(ColorUtils.translateColorCodes("&aSe han removido " + amount + " " + monedaCheck.getSymbol() + " " + monedaCheck.getName() + " de " + targetPlayerName + "."));
                    } else {
                        sender.sendMessage(ColorUtils.translateColorCodes("&c" + targetPlayerName + " no tiene suficiente " + monedaCheck.getName() + " para remover " + amount + "."));
                    }
                    break;
            }
            return true;
        }

        if (subCommand.equals("balance") || subCommand.equals("bal")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.translateColorCodes("Este comando solo puede ser ejecutado por jugadores."));
                return true;
            }
            Player player = (Player) sender;

            String monedaId;
            if (args.length == 1) {
                // Si no se especifica moneda, buscar la moneda por defecto si hay una
                Moneda defaultMoneda = monedaManager.getMonedasDisponibles().values().stream()
                        .filter(Moneda::isDefault)
                        .findFirst()
                        .orElse(null);
                if (defaultMoneda != null) {
                    monedaId = defaultMoneda.getId();
                } else {
                    sender.sendMessage(ColorUtils.translateColorCodes("&cUso: /moneda balance [ID_moneda] o especifica una moneda por defecto en monedas.yml"));
                    return true;
                }
            } else {
                monedaId = args[1].toLowerCase();
            }

            Moneda moneda = monedaManager.getMoneda(monedaId);
            if (moneda == null) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cLa moneda con ID '" + monedaId + "' no existe. Usa /moneda list para ver las disponibles."));
                return true;
            }

            // Usar el UUID del jugador para obtener el saldo
            double balance = monedaManager.getSaldo(player.getUniqueId(), monedaId);
            sender.sendMessage(ColorUtils.translateColorCodes("&aTu saldo de " + moneda.getName() + " es: &e" + String.format("%." + moneda.getDecimalPlaces() + "f", balance) + " " + moneda.getSymbol() + "&a."));
            return true;
        }

        // Manejar comandos directos de monedas, por ejemplo /souls
        if (monedaManager.getMonedasDisponibles().containsKey(subCommand)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.translateColorCodes("Este comando solo puede ser ejecutado por jugadores."));
                return true;
            }
            Player player = (Player) sender;
            Moneda moneda = monedaManager.getMoneda(subCommand);
            // Usar el UUID del jugador para obtener el saldo
            double balance = monedaManager.getSaldo(player.getUniqueId(), subCommand);
            player.sendMessage(ColorUtils.translateColorCodes("&aTu saldo de " + moneda.getName() + " es: &e" + String.format("%." + moneda.getDecimalPlaces() + "f", balance) + " " + moneda.getSymbol() + "&a."));
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ColorUtils.translateColorCodes("&b--- Ayuda de MonedaPlugin ---"));
        sender.sendMessage(ColorUtils.translateColorCodes("&e/moneda help &7- Muestra este mensaje de ayuda."));
        sender.sendMessage(ColorUtils.translateColorCodes("&e/moneda list &7- Lista todas las monedas disponibles."));
        sender.sendMessage(ColorUtils.translateColorCodes("&e/moneda balance [ID_moneda] &7- Consulta tu saldo de una moneda específica. Si no especificas, usa la moneda por defecto."));
        sender.sendMessage(ColorUtils.translateColorCodes("&e/moneda <add|set|remove> <ID_moneda> <jugador> <cantidad> &7- Modifica el saldo de un jugador (Solo administradores)."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return Arrays.asList("help", "list", "balance", "add", "set", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove")) {
                return monedaManager.getMonedasDisponibles().keySet().stream()
                        .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return completions;
    }
}