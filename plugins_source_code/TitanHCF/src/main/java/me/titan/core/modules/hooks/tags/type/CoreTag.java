package me.titan.core.modules.hooks.tags.type;

import me.titan.core.modules.hooks.tags.*;
import org.bukkit.entity.*;
import com.broustudio.CoreAPI.*;

public class CoreTag implements Tag {
    @Override
    public String getTag(Player player) {
        String s = CoreAPI.plugin.tagManager.getTagDisplay(player.getUniqueId());
        return (s != null) ? s : "";
    }
}
