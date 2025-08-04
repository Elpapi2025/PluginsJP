package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;
import com.broustudio.CoreAPI.*;

public class CoreRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return CoreAPI.plugin.rankManager.getRank(player.getUniqueId());
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return CoreAPI.plugin.rankManager.getRankPrefix(player.getUniqueId());
    }
    
    @Override
    public String getRankSuffix(Player player) {
        return "";
    }
    
    @Override
    public String getRankColor(Player player) {
        return CoreAPI.plugin.rankManager.getRankColor(player.getUniqueId());
    }
}
