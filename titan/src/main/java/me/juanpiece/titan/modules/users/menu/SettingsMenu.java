package me.juanpiece.titan.modules.users.menu;

import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.settings.UserSetting;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SettingsMenu extends Menu {

    public SettingsMenu(MenuManager manager, Player player) {
        super(
                manager,
                player,
                manager.getLanguageConfig().getString("SETTINGS_COMMAND.TITLE"),
                manager.getLanguageConfig().getInt("SETTINGS_COMMAND.SIZE"),
                false
        );
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());


        for (String key : getLanguageConfig().getConfigurationSection("SETTINGS_COMMAND.ITEMS").getKeys(false)) {
            buttons.put(getLanguageConfig().getInt("SETTINGS_COMMAND.ITEMS." + key + ".SLOT"), new Button() {
                private final UserSetting setting = getSetting(key);

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);

                    if (setting != null) {
                        switch (setting.toString()) {
                            case "SCOREBOARD":
                                user.setScoreboard(!user.isScoreboard());
                                break;

                            case "SCOREBOARD_CLAIM":
                                user.setScoreboardClaim(!user.isScoreboardClaim());
                                break;

                            case "PUBLIC_CHAT":
                                user.setPublicChat(!user.isPublicChat());
                                break;

                            case "COBBLE":
                                user.setCobblePickup(!user.isCobblePickup());
                                break;

                            case "FOUND_DIAMOND":
                                user.setFoundDiamondAlerts(!user.isFoundDiamondAlerts());
                                break;
                        }

                        update(); // Update the menu
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(getLanguageConfig().getString("SETTINGS_COMMAND.ITEMS." + key + ".MATERIAL")))
                            .setName(getLanguageConfig().getString("SETTINGS_COMMAND.ITEMS." + key + ".NAME"))
                            .data(getManager(), (short) getLanguageConfig().getInt("SETTINGS_COMMAND.ITEMS." + key + ".DATA"));

                    if (setting != null) {
                        builder.setLore(getLanguageConfig().getStringList("SETTINGS_COMMAND.ITEMS." + key +
                                ".LORE_" + convert(user, setting.toString()))); // basically handles ENABLED or DISABLED
                    } else {
                        builder.setLore(getLanguageConfig().getStringList("SETTINGS_COMMAND.ITEMS." + key + ".LORE"));
                    }

                    return builder.toItemStack();
                }
            });
        }

        return buttons;
    }

    private String convert(User user, String setting) {
        switch (setting) {
            case "SCOREBOARD":
                return convertBoolean(user.isScoreboard());

            case "SCOREBOARD_CLAIM":
                return convertBoolean(user.isScoreboardClaim());

            case "PUBLIC_CHAT":
                return convertBoolean(user.isPublicChat());

            case "COBBLE":
                return convertBoolean(user.isCobblePickup());

            case "FOUND_DIAMOND":
                return convertBoolean(user.isFoundDiamondAlerts());
        }

        return "";
    }

    private UserSetting getSetting(String name) {
        try {

            return UserSetting.valueOf(name);

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String convertBoolean(boolean bool) {
        return (bool ? "ENABLED" : "DISABLED");
    }
}