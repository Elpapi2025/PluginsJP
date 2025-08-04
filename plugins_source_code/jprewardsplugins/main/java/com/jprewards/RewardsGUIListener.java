package com.jprewards;

import com.jprewards.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RewardsGUIListener implements Listener {

    private final JPRewards plugin;

    public RewardsGUIListener(JPRewards plugin) {
        this.plugin = plugin;
    }

    public static void openRewardsGUI(Player player) {
        FileConfiguration config = JPRewards.getInstance().getConfig();
        String guiTitle = getMessage("gui.title", "&8Recompensas Diarias");
        int guiSize = config.getInt("gui.size", 54);

        Inventory gui = Bukkit.createInventory(null, guiSize, guiTitle);

        if (config.getBoolean("gui.fill_item.enabled", false)) {
            ItemStack fillItem = createFillItem();
            for (int i = 0; i < guiSize; i++) {
                gui.setItem(i, fillItem);
            }
        }

        PlayerData playerData = new PlayerData(player.getUniqueId(), 0, 0); // Default values
        JPRewards.getInstance().getDataManager().loadPlayerData(player.getUniqueId(), playerData);

        int consecutiveDays = playerData.getCurrentStreak();
        long lastClaimTimestamp = playerData.getLastClaimedDay();

        List<Map<?, ?>> rewardsList = config.getMapList("rewards");
        if (rewardsList.isEmpty()) {
            player.sendMessage(getMessage("messages.no_rewards_configured"));
            return;
        }

        long timeSinceLastClaim = System.currentTimeMillis() - lastClaimTimestamp;
        long twentyFourHours = TimeUnit.HOURS.toMillis(24);
        boolean canClaimToday = timeSinceLastClaim >= twentyFourHours;

        for (Map<?, ?> rewardMap : rewardsList) {
            int day = (int) rewardMap.get("day");
            int slot = (int) rewardMap.get("slot");

            ItemStack item;
            if (consecutiveDays + 1 == day && canClaimToday) {
                item = createDisplayItem("available", day, 0);
            } else if (consecutiveDays >= day) {
                item = createDisplayItem("claimed", day, 0);
            } else if (consecutiveDays + 1 == day && !canClaimToday) {
                long timeRemaining = twentyFourHours - timeSinceLastClaim;
                item = createDisplayItem("next_locked", day, timeRemaining);
            } else {
                item = createDisplayItem("locked", day, 0);
            }

            gui.setItem(slot, item);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        FileConfiguration config = plugin.getConfig();
        String guiTitle = getMessage("gui.title", "&8Recompensas Diarias");

        if (!event.getView().getTitle().equals(guiTitle)) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR || !clickedItem.hasItemMeta()) return;

        List<Map<?, ?>> rewardsList = config.getMapList("rewards");
        if (rewardsList.isEmpty()) return;

        for (Map<?, ?> rewardMap : rewardsList) {
            if ((int) rewardMap.get("slot") == event.getSlot()) {
                int day = (int) rewardMap.get("day");

                PlayerData playerData = new PlayerData(player.getUniqueId(), 0, 0); // Default values
                plugin.getDataManager().loadPlayerData(player.getUniqueId(), playerData);

                int consecutiveDays = playerData.getCurrentStreak();
                long lastClaim = playerData.getLastClaimedDay();

                if (consecutiveDays + 1 == day && System.currentTimeMillis() - lastClaim >= TimeUnit.HOURS.toMillis(24)) {
                    List<String> commands = (List<String>) rewardMap.get("commands");
                    for (String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }

                    playerData.setCurrentStreak(day);
                    playerData.setLastClaimedDay(System.currentTimeMillis());
                    plugin.getDataManager().savePlayerData(playerData);

                    player.closeInventory();                    sendRewardClaimedNotifications(player, day);                    openRewardsGUI(player);

                } else if (consecutiveDays >= day) {
                    player.sendMessage(getMessage("messages.already_claimed"));
                } else {
                    player.sendMessage(getMessage("messages.cannot_claim_yet"));
                }
                return;
            }
        }
    }

    private static ItemStack createDisplayItem(String type, int day, long timeRemainingMillis) {
        FileConfiguration config = JPRewards.getInstance().getConfig();
        ConfigurationSection itemSection = config.getConfigurationSection("items." + type);
        if (itemSection == null) return new ItemStack(Material.STONE);

        Material material = getMaterial(itemSection.get("material"));
        int data = itemSection.getInt("data", 0);
        String name = ChatColor.translateAlternateColorCodes('&', itemSection.getString("name", "").replace("{day}", String.valueOf(day)));
        boolean enchanted = itemSection.getBoolean("enchanted", false);

        List<String> lore = itemSection.getStringList("lore").stream()
                .map(line -> {
                    String formattedTime = formatTime(timeRemainingMillis);
                    return ChatColor.translateAlternateColorCodes('&', line.replace("{day}", String.valueOf(day)).replace("{time}", formattedTime));
                })
                .collect(Collectors.toList());

        ItemStack item = new ItemStack(material, 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            if (enchanted) {
                meta.addEnchant(Enchantment.LURE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createFillItem() {
        FileConfiguration config = JPRewards.getInstance().getConfig();
        Material material = getMaterial(config.get("gui.fill_item.material"));
        int data = config.getInt("gui.fill_item.data", 0);
        String name = ChatColor.translateAlternateColorCodes('&', config.getString("gui.fill_item.name", " "));
        ItemStack item = new ItemStack(material, 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Material getMaterial(Object materialObj) {
        if (materialObj instanceof String) {
            String materialName = (String) materialObj;
            if (materialName != null && !materialName.isEmpty()) {
                try {
                    return Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    JPRewards.getInstance().getLogger().warning("Material no válido por nombre en config.yml: " + String.valueOf(materialObj));
                }
            }
        } else if (materialObj instanceof Integer) {
            Material material = Material.getMaterial((int) materialObj);
            if (material != null) {
                return material;
            } else {
                JPRewards.getInstance().getLogger().warning("Material no válido por ID en config.yml: " + String.valueOf(materialObj));
            }
        }
        return Material.STONE;
    }

    private static String formatTime(long millis) {
        if (millis <= 0) return "0s";
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }

    private static String getMessage(String path, String... defaultValue) {
        FileConfiguration config = JPRewards.getInstance().getConfig();
        String message = config.getString(path); // Use getString directly

        if (message == null) {
            message = (defaultValue.length > 0) ? defaultValue[0] : "&cMessage not found: " + path;
        }
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static void sendRewardClaimedNotifications(Player player, int day) {
        FileConfiguration config = JPRewards.getInstance().getConfig();

        // Always send chat message
        player.sendMessage(getMessage("messages.reward_claimed").replace("{day}", String.valueOf(day)));

        // Send action bar message
        if (config.getBoolean("notifications.actionbar.enabled", false)) {
            String message = getMessage("notifications.actionbar.message", "").replace("{day}", String.valueOf(day));
            // For 1.8.8, sending action bar requires NMS or ProtocolLib.
            // This is a placeholder for actual implementation.
            // Example using NMS (requires reflection and version specific code):
            // PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte)2);
            // ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }

        // Send title/subtitle
        if (config.getBoolean("notifications.title.enabled", false)) {
            String title = getMessage("notifications.title.title", "").replace("{day}", String.valueOf(day));
            String subtitle = getMessage("notifications.title.subtitle", "").replace("{day}", String.valueOf(day));
            int fadeIn = config.getInt("notifications.title.fade_in", 10);
            int stay = config.getInt("notifications.title.stay", 70);
            int fadeOut = config.getInt("notifications.title.fade_out", 20);
            // For 1.8.8, sending titles requires NMS or ProtocolLib.
            // This is a placeholder for actual implementation.
            // Example using NMS (requires reflection and version specific code):
            // PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut);
            // PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"), fadeIn, stay, fadeOut);
            // ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
            // ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
        }
    }
}
