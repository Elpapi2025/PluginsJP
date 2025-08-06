package me.juanpiece.titan.modules.ability.menu;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
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
public class AbilityListMenu extends Menu {

    public AbilityListMenu(MenuManager manager, Player player) {
        super(
                manager,
                player,
                manager.getAbilitiesConfig().getString("GLOBAL_ABILITY.ABILITY_MENU_TITLE"),
                54,
                false
        );
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int slot = 1;

        for (Ability ability : getInstance().getAbilityManager().getAbilities().values()) {
            if (!ability.isEnabled()) continue;

            buttons.put(slot, new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    if (!(e.getWhoClicked() instanceof Player)) return;

                    Player clicked = (Player) e.getWhoClicked();

                    if (clicked.hasPermission("titan.abilitymenu.click")) {
                        clicked.getInventory().addItem(ability.getItem().clone());
                    }

                    e.setCancelled(true);
                }

                @Override
                public ItemStack getItemStack() {
                    return ability.getItem();
                }
            });
            slot++;
        }

        return buttons;
    }
}