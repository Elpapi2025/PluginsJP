package me.juanpiece.titan.modules.killtag.menu;

import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.killtag.Killtag;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KilltagMenu extends Menu {

    public KilltagMenu(MenuManager manager, Player player) {
        super(
                manager,
                player,
                manager.getConfig().getString("KILL_TAGS.TITLE"),
                manager.getConfig().getInt("KILL_TAGS.SIZE"),
                false
        );
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Killtag> killtags = new ArrayList<>(getInstance().getKilltagManager().getKilltags().values());

        for (Killtag killtag : killtags) {
            buttons.put(killtag.getSlot(), new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
                    e.setCancelled(true);

                    if (!killtag.hasPerm(player)) {
                        player.sendMessage(getLanguageConfig().getString("KILLTAGS_COMMAND.NO_PERM"));
                        return;
                    }

                    user.setKilltag(killtag.getName());
                    user.save();
                    player.closeInventory();
                    player.sendMessage(getLanguageConfig().getString("KILLTAGS_COMMAND.EQUIPPED_KILLTAG")
                            .replace("%killtag%", killtag.getDisplayName())
                    );
                }

                @Override
                public ItemStack getItemStack() {
                    List<String> cloned = new ArrayList<>(killtag.getLore());
                    ItemBuilder builder = new ItemBuilder(killtag.getMaterial());
                    String name = killtag.getDisplayName();

                    if (getInstance().getKilltagManager().getKilltag(player) != killtag) {
                        builder.setName(name.replace("%equipped%", ""));
                        cloned.removeIf(s -> s.contains("%equipped%"));

                    } else {
                        builder.setName(name.replace("%equipped%", getConfig().getString("KILL_TAGS.EQUIPPED_PLACEHOLDER")));
                        cloned.replaceAll(s -> s
                                .replace("%equipped%", getConfig().getString("KILL_TAGS.EQUIPPED_PLACEHOLDER"))
                        );
                    }

                    return builder.setLore(cloned).toItemStack();
                }
            });
        }

        buttons.put(getConfig().getInt("KILL_TAGS.RESET_BUTTON.SLOT"), new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                User user = getInstance().getUserManager().getByUUID(player.getUniqueId());

                if (getInstance().getKilltagManager().getKilltag(player) == null) {
                    player.sendMessage(getLanguageConfig().getString("KILLTAGS_COMMAND.NOT_EQUIPPED"));
                    return;
                }

                user.setKilltag(null);
                user.save();
                player.closeInventory();
                player.sendMessage(getLanguageConfig().getString("KILLTAGS_COMMAND.RESET_KILLTAG"));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(ItemUtils.getMat(getConfig().getString("KILL_TAGS.RESET_BUTTON.MATERIAL")))
                        .setName(getConfig().getString("KILL_TAGS.RESET_BUTTON.NAME")).toItemStack();
            }
        });

        return buttons;
    }
}