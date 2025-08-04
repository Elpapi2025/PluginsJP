package me.titan.core.modules.framework.menu.button;

import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public abstract class Button {
    public abstract void onClick(InventoryClickEvent p0);

    public abstract ItemStack getItemStack();
}