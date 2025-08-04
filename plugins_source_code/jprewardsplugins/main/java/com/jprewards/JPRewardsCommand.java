package com.jprewards;

import com.jprewards.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JPRewardsCommand implements CommandExecutor, TabCompleter {

    private final JPRewards plugin;

    public JPRewardsCommand(JPRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("jprewards.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(getMessage(config, "messages.reload_success"));
                } else {
                    sender.sendMessage(getMessage(config, "messages.no_permission"));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("check")) {
                if (!sender.hasPermission("jprewards.admin.check")) {
                    sender.sendMessage(getMessage(config, "messages.no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /rewards check <jugador>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                    return true;
                }
                PlayerData playerData = new PlayerData(target.getUniqueId(), 0, 0);
                plugin.getDataManager().loadPlayerData(target.getUniqueId(), playerData);

                sender.sendMessage(ChatColor.YELLOW + "--- Datos de Recompensas de " + target.getName() + " ---");
                String lastClaimedDate = (playerData.getLastClaimedDay() == 0) ? "Nunca" : new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(playerData.getLastClaimedDay()));
                sender.sendMessage(ChatColor.YELLOW + "Último día reclamado: " + ChatColor.GOLD + lastClaimedDate);
                sender.sendMessage(ChatColor.YELLOW + "Racha actual: " + ChatColor.GOLD + playerData.getCurrentStreak());
                return true;
            } else if (args[0].equalsIgnoreCase("setstreak")) {
                if (!sender.hasPermission("jprewards.admin.setstreak")) {
                    sender.sendMessage(getMessage(config, "messages.no_permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /rewards setstreak <jugador> <cantidad>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "La cantidad debe ser un número.");
                    return true;
                }

                PlayerData playerData = new PlayerData(target.getUniqueId(), 0, 0);
                plugin.getDataManager().loadPlayerData(target.getUniqueId(), playerData);
                playerData.setCurrentStreak(amount);
                plugin.getDataManager().savePlayerData(playerData);
                sender.sendMessage(ChatColor.GREEN + "Racha de " + target.getName() + " establecida a " + amount + ".");
                return true;
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("jprewards.admin")) {
                    sender.sendMessage(getMessage(config, "messages.no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /rewards reset <jugador>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                    return true;
                }
                PlayerData playerData = new PlayerData(target.getUniqueId(), 0, 0);
                plugin.getDataManager().loadPlayerData(target.getUniqueId(), playerData);
                playerData.setLastClaimedDay(0);
                playerData.setCurrentStreak(0);
                plugin.getDataManager().savePlayerData(playerData);
                sender.sendMessage(ChatColor.GREEN + "Datos de recompensas de " + target.getName() + " reiniciados.");
                return true;
            } else if (args[0].equalsIgnoreCase("advance")) {
                if (!sender.hasPermission("jprewards.admin")) {
                    sender.sendMessage(getMessage(config, "messages.no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /rewards advance <jugador>");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                    return true;
                }

                PlayerData playerData = new PlayerData(target.getUniqueId(), 0, 0);
                plugin.getDataManager().loadPlayerData(target.getUniqueId(), playerData);
                playerData.setLastClaimedDay(0); // Set to 0 to make next reward available immediately
                plugin.getDataManager().savePlayerData(playerData);
                sender.sendMessage(ChatColor.GREEN + "La recompensa de " + target.getName() + " ha sido adelantada al siguiente día.");
                return true;
            }
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage(config, "messages.player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("jprewards.use")) {
            player.sendMessage(getMessage(config, "messages.no_permission"));
            return true;
        }

        RewardsGUIListener.openRewardsGUI(player);
        return true;
    }

    private String getMessage(FileConfiguration config, String path) {
        String message = config.getString(path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("jprewards.admin")) {
                completions.addAll(Arrays.asList("reload", "check", "setstreak", "reset", "advance"));
            }
            // No need to add "rewards" here as it's the command itself
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("setstreak") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("advance")) {
                if (sender.hasPermission("jprewards.admin")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }
        }
        // No need for args.length == 3 for setstreak/advance as it's a number
        return completions;
    }
}