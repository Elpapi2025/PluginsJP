package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;
import com.broustudio.MizuAPI.*;

public class MizuRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return MizuAPI.getAPI().getRank(player.getUniqueId());
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return MizuAPI.getAPI().getRankPrefix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }
    
    @Override
    public String getRankSuffix(Player player) {
        return MizuAPI.getAPI().getRankSuffix(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }
    
    @Override
    public String getRankColor(Player player) {
        return MizuAPI.getAPI().getRankColor(MizuAPI.getAPI().getRank(player.getUniqueId()));
    }
}
