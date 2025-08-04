package me.titan.core.modules.ability.menu;

import org.bukkit.entity.*;
import me.titan.core.modules.framework.menu.button.*;
import java.util.*;
import me.titan.core.modules.ability.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import me.titan.core.modules.framework.menu.*;

public class AbilityListMenu extends Menu {
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int slot = 1;
        for (Ability ability : this.getInstance().getAbilityManager().getAbilities().values()) {
            buttons.put(slot, new Button() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
                
                @Override
                public ItemStack getItemStack() {
                    return ability.getItem();
                }
            });
            ++slot;
        }
        return buttons;
    }
    
    public AbilityListMenu(MenuManager manager, Player player) {
        super(manager, player, "Abilities", 54, false);
    }
}
