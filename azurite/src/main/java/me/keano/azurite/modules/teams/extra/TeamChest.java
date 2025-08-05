package me.keano.azurite.modules.teams.extra;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TeamChest {

    private final ItemStack itemStack;
    private final double percentage;

    public TeamChest(ItemStack itemStack, double percentage) {
        this.itemStack = itemStack;
        this.percentage = percentage;
    }
}