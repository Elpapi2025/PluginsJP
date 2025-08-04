package me.titan.core.modules.ability.menu;

import org.bukkit.entity.*;
import me.titan.core.modules.framework.menu.button.*;
import java.util.*;
import me.titan.core.modules.ability.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import me.titan.core.utils.*;
import me.titan.core.modules.framework.menu.*;

public class AbilityToggleMenu extends Menu {
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int slot = 1;
        for (Ability ability : this.getInstance().getAbilityManager().getAbilities().values()) {
            buttons.put(slot, new Button() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    ability.setEnabled(!ability.isEnabled());
                    AbilityToggleMenu.this.getAbilitiesConfig().set(String.valueOf(new StringBuilder().append(ability.getNameConfig()).append(".ENABLED")), ability.isEnabled());
                    AbilityToggleMenu.this.getAbilitiesConfig().save();
                    AbilityToggleMenu.this.update();
                }
                
                @Override
                public ItemStack getItemStack() {
                    ItemBuilder builder = new ItemBuilder(ability.getItem().clone());
                    builder.setLore(String.valueOf(new StringBuilder().append("&eActivado: ").append(ability.isEnabled() ? "&averdadero" : "&cfalso")));
                    return builder.toItemStack();
                }
            });
            ++slot;
        }
        return buttons;
    }
    
    public AbilityToggleMenu(MenuManager manager, Player player) {
        super(manager, player, "Ability Toggles", 54, false);
    }
}
