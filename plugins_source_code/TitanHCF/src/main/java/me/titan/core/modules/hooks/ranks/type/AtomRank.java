package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;
import com.broustudio.AtomAPI.*;

public class AtomRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return AtomAPI.getInstance().rankManager.getRank(player.getUniqueId());
    }
    
    @Override
    public String getRankColor(Player player) {
        return AtomAPI.getInstance().rankManager.getRankColor(player.getUniqueId());
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return AtomAPI.getInstance().rankManager.getRankPrefix(player.getUniqueId());
    }
    
    @Override
    public String getRankSuffix(Player player) {
        return AtomAPI.getInstance().rankManager.getRankSuffix(player.getUniqueId());
    }
}
