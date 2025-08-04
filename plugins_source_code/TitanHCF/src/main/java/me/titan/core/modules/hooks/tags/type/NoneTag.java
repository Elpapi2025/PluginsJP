package me.titan.core.modules.hooks.tags.type;

import me.titan.core.modules.hooks.tags.*;
import org.bukkit.entity.*;

public class NoneTag implements Tag {
    @Override
    public String getTag(Player player) {
        return "";
    }
}
