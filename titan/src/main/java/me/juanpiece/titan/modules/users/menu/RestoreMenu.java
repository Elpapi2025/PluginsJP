package me.juanpiece.titan.modules.users.menu;

import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.extra.StoredInventory;
import me.juanpiece.titan.utils.Formatter;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class RestoreMenu extends Menu {

    private final Player target;

    public RestoreMenu(MenuManager manager, Player player, Player target) {
        super(
                manager,
                player,
                manager.getConfig().getString("INVENTORY_RESTORE.RESTORE_MENU.TITLE").replace("%player%", target.getName()),
                manager.getConfig().getInt("INVENTORY_RESTORE.RESTORE_MENU.SIZE"),
                false
        );
        this.target = target;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        User targetUser = getInstance().getUserManager().getByUUID(target.getUniqueId());

        int i = 1;

        for (StoredInventory inventory : targetUser.getInventories()) {
            buttons.put(i, new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    player.closeInventory();
                    new InventoryMenu(getManager(), player, target, inventory).open();
                }

                @Override
                public ItemStack getItemStack() {
                    List<String> lore = getConfig().getStringList("INVENTORY_RESTORE.RESTORE_MENU.FORMAT");

                    lore.replaceAll(s -> s
                            .replace("%date%", Formatter.DATE_FORMAT.format(inventory.getDate()))
                            .replace("%armoramount%", String.valueOf((int) Arrays.stream(inventory.getArmor())
                                    .filter(Objects::nonNull)
                                    .filter(item -> item.getType() != Material.AIR)
                                    .count()))
                            .replace("%contentamount%", String.valueOf((int) Arrays.stream(inventory.getContents())
                                    .filter(Objects::nonNull)
                                    .filter(item -> item.getType() != Material.AIR)
                                    .count()))
                    );

                    return new ItemBuilder(ItemUtils.getMat(getConfig().getString("INVENTORY_RESTORE.RESTORE_MENU.RESTORE_ITEM")))
                            .setLore(lore)
                            .setName(getConfig().getString("INVENTORY_RESTORE.RESTORE_MENU.NAME")
                                    .replace("%date%", Formatter.DATE_FORMAT.format(inventory.getDate())))
                            .toItemStack();
                }
            });
            i++;
        }

        return buttons;
    }
}