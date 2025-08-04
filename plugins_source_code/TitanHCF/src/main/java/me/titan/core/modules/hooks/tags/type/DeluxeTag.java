package me.titan.core.modules.hooks.tags.type;

import me.titan.core.modules.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class DeluxeTag implements Tag {
    @Override
    public String getTag(Player player) {
        return me.clip.deluxetags.DeluxeTag.getPlayerDisplayTag(player);
    }
}
