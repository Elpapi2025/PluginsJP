package me.juanpiece.titan.modules.ability.menu;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.utils.ItemBuilder;
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
public class AbilityToggleMenu extends Menu {

    public AbilityToggleMenu(MenuManager manager, Player player) {
        super(
                manager,
                player,
                "Ability Toggles",
                54,
                false
        );
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 1;

        for (Ability ability : getInstance().getAbilityManager().getAbilities().values()) {
            buttons.put(slot, new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    ability.setEnabled(!ability.isEnabled());
                    getAbilitiesConfig().set(ability.getNameConfig() + ".ENABLED", ability.isEnabled());
                    getAbilitiesConfig().save();
                    update();
                }

                @Override
                public ItemStack getItemStack() {
                    ItemBuilder builder = new ItemBuilder(ability.getItem().clone());
                    builder.setLore("&eEnabled: " + (ability.isEnabled() ? "&atrue" : "&cfalse"));
                    return builder.toItemStack();
                }
            });
            slot++;
        }

        return buttons;
    }
}