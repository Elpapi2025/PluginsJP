package me.titan.core.modules.hooks.tags.type;

import me.titan.core.modules.hooks.tags.*;
import org.bukkit.entity.*;
import me.activated.core.plugin.*;

public class AquaCoreTag implements Tag {
    @Override
    public String getTag(Player player) {
        return AquaCoreAPI.INSTANCE.getTagFormat(player);
    }
}
