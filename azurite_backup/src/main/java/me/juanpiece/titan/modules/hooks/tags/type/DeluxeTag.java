package me.juanpiece.titan.modules.hooks.tags.type;

import me.juanpiece.titan.modules.hooks.tags.Tag;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class DeluxeTag implements Tag {

    @Override
    public String getTag(Player player) {
        String tag = me.clip.deluxetags.tags.DeluxeTag.getPlayerDisplayTag(player);
        return (tag != null ? tag : "");
    }
}