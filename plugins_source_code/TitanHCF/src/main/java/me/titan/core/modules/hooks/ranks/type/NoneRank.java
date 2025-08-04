package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;

public class NoneRank implements Rank {
    @Override
    public String getRankSuffix(Player player) {
        return "";
    }
    
    @Override
    public String getRankColor(Player player) {
        return "";
    }
    
    @Override
    public String getRankName(Player player) {
        return "";
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return "";
    }
}
