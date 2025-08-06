package me.juanpiece.titan.modules.hooks.tags.type;

import me.activated.core.plugin.AquaCoreAPI;
import me.juanpiece.titan.modules.hooks.tags.Tag;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AquaCoreTag implements Tag {

    @Override
    public String getTag(Player player) {
        return AquaCoreAPI.INSTANCE.getTagFormat(player);
    }
}