package me.titan.core.modules.hooks.tags.type;

import com.broustudio.MizuAPI.MizuAPI;
import me.titan.core.modules.hooks.tags.Tag;
import org.bukkit.entity.Player;

public class MizuTag implements Tag {
    @Override
    public String getTag(Player player) {
        String s = MizuAPI.getAPI().getTagDisplay(MizuAPI.getAPI().getTag(player.getUniqueId()));
        return (s != null) ? s : "";
    }
}
