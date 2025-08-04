package me.titan.core.modules.hooks.ranks;

import org.bukkit.entity.*;

public interface Rank {
    String getRankColor(Player p0);
    
    String getRankPrefix(Player p0);
    
    String getRankSuffix(Player p0);
    
    String getRankName(Player p0);
}
