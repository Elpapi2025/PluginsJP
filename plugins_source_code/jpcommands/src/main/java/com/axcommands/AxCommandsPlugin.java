package com.axcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

import com.axcommands.utils.ColorUtils; // Importar ColorUtils

public class AxCommandsPlugin extends JavaPlugin {
    private FileConfiguration config;
    private Map<String, Set<UUID>> usedCommands = new HashMap<>();
    private Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        registerCustomCommands();
        getCommand("axcmd").setExecutor(new AxCmdExecutor());
        getLogger().info(ColorUtils.translateColorCodes("&aAxCommandsPlugin versión 1.2 habilitado (para Minecraft 1.8.8/1.8.9)!"));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        config = getConfig();
        registerCustomCommands();
        usedCommands.clear();
        cooldowns.clear(); // Limpiar cooldowns al recargar
    }

    private void registerCustomCommands() {
        if (!config.isConfigurationSection("commands")) return;
        ConfigurationSection section = config.getConfigurationSection("commands");
        for (String cmd : section.getKeys(false)) {
            PluginCommand pluginCommand = getCommand(cmd);
            if (pluginCommand == null) continue;
            pluginCommand.setExecutor(new CustomCommandExecutor(cmd));
        }
    }

    private class AxCmdExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ColorUtils.translateColorCodes("&6&lAxCommands &7- &eGuía de uso:"));
                sender.sendMessage(ColorUtils.translateColorCodes("&eUsa los comandos configurados directamente, por ejemplo: /freerank"));
                sender.sendMessage(ColorUtils.translateColorCodes("&e/axcmd reload &7- Recarga la configuración del plugin"));
                sender.sendMessage(ColorUtils.translateColorCodes("&e/axcmd help &7- Muestra esta ayuda"));
                sender.sendMessage(ColorUtils.translateColorCodes("&7Comandos configurados: &a" + config.getConfigurationSection("commands").getKeys(false)));
                sender.sendMessage(ColorUtils.translateColorCodes("&7Edita el config.yml para agregar o modificar comandos."));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("axcommands.reload")) {
                    sender.sendMessage(ColorUtils.translateColorCodes("&cNo tienes permiso para recargar la configuración."));
                    return true;
                }
                reloadConfig();
                sender.sendMessage(ColorUtils.translateColorCodes("&aConfiguración recargada correctamente."));
                return true;
            }
            sender.sendMessage(ColorUtils.translateColorCodes("&cComando desconocido. Usa /axcmd help"));
            return true;
        }
    }

    private class CustomCommandExecutor implements CommandExecutor {
        private final String cmdName;
        public CustomCommandExecutor(String cmdName) {
            this.cmdName = cmdName;
        }
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!config.isConfigurationSection("commands." + cmdName)) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cEste comando no está configurado."));
                return true;
            }
            // Limite de usos
            int maxUses = config.getInt("commands." + cmdName + ".max-uses", -1);
            String bypassPerm = config.getString("commands." + cmdName + ".bypass-permission", "axcommands.bypass");
            if (maxUses > 0 && sender instanceof Player && !sender.hasPermission(bypassPerm)) {
                UUID uuid = ((Player) sender).getUniqueId();
                usedCommands.putIfAbsent(cmdName, new HashSet<>());
                Set<UUID> used = usedCommands.get(cmdName);
                if (used.contains(uuid)) {
                    sender.sendMessage(ColorUtils.translateColorCodes(config.getString("commands." + cmdName + ".limit-message", "&cYa has usado este comando.")));
                    return true;
                }
                used.add(uuid);
            }

            // Cooldown
            int cooldownSeconds = config.getInt("commands." + cmdName + ".cooldown", 0);
            if (cooldownSeconds > 0 && sender instanceof Player && !sender.hasPermission(bypassPerm)) {
                UUID uuid = ((Player) sender).getUniqueId();
                cooldowns.putIfAbsent(uuid, new HashMap<>());
                Map<String, Long> playerCooldowns = cooldowns.get(uuid);
                long lastUsed = playerCooldowns.getOrDefault(cmdName, 0L);
                long remainingTime = (lastUsed + (cooldownSeconds * 1000L)) - System.currentTimeMillis();

                if (remainingTime > 0) {
                    sender.sendMessage(ColorUtils.translateColorCodes(config.getString("commands." + cmdName + ".cooldown-message", "&cEste comando está en cooldown. Inténtalo de nuevo en &e%time% segundos.").replace("%time%", String.valueOf(remainingTime / 1000))));
                    return true;
                }
                playerCooldowns.put(cmdName, System.currentTimeMillis());
            }
            List<String> actions = config.getStringList("commands." + cmdName + ".actions");
            for (String action : actions) {
                action = action.replace("{player}", sender.getName());
                if (action.startsWith("console: ")) {
                    String cmd = action.substring(9);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                } else if (action.startsWith("player: ")) {
                    String cmd = action.substring(8);
                    if (sender instanceof Player) {
                        ((Player) sender).performCommand(cmd);
                    } else {
                        sender.sendMessage(ColorUtils.translateColorCodes("&cSolo los jugadores pueden ejecutar comandos como jugador."));
                    }
                } else if (action.startsWith("message: ")) {
                    String msg = action.substring(9);
                    sender.sendMessage(ColorUtils.translateColorCodes(msg));
                } else if (action.startsWith("broadcast: ")) {
                    String msg = action.substring(11);
                    Bukkit.broadcastMessage(ColorUtils.translateColorCodes(msg));
                } else if (action.startsWith("teleport: ")) {
                    String[] parts = action.substring(10).split(" ");
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (parts.length == 3) {
                            try {
                                double x = Double.parseDouble(parts[0]);
                                double y = Double.parseDouble(parts[1]);
                                double z = Double.parseDouble(parts[2]);
                                p.teleport(new org.bukkit.Location(p.getWorld(), x, y, z));
                            } catch (NumberFormatException e) {
                                p.sendMessage(ColorUtils.translateColorCodes("&cCoordenadas inválidas para teleport."));
                            }
                        } else {
                            p.sendMessage(ColorUtils.translateColorCodes("&cUso: teleport: <x> <y> <z>"));
                        }
                    } else {
                        sender.sendMessage(ColorUtils.translateColorCodes("&cSolo los jugadores pueden ser teletransportados."));
                    }
                } else if (action.startsWith("sound: ")) {
                    String[] parts = action.substring(7).split(" ");
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (parts.length >= 1) {
                            try {
                                org.bukkit.Sound sound = org.bukkit.Sound.valueOf(parts[0].toUpperCase());
                                float volume = (parts.length > 1) ? Float.parseFloat(parts[1]) : 1.0f;
                                float pitch = (parts.length > 2) ? Float.parseFloat(parts[2]) : 1.0f;
                                p.playSound(p.getLocation(), sound, volume, pitch);
                            } catch (IllegalArgumentException e) {
                                p.sendMessage(ColorUtils.translateColorCodes("&cSonido inválido o formato incorrecto."));
                            }
                        } else {
                            p.sendMessage(ColorUtils.translateColorCodes("&cUso: sound: <nombre_sonido> [volumen] [tono]"));
                        }
                    } else {
                        sender.sendMessage(ColorUtils.translateColorCodes("&cSolo los jugadores pueden escuchar sonidos."));
                    }
                }
            }
            return true;
        }
    }
}