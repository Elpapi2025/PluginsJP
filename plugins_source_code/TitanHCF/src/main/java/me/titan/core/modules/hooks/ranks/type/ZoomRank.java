package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;
import club.frozed.core.*;

public class ZoomRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return ZoomAPI.getRankName(player);
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return ZoomAPI.getRankPrefix(player);
    }
    
    @Override
    public String getRankColor(Player player) {
        return ZoomAPI.getRankColor(player).toString();
    }
    
    @Override
    public String getRankSuffix(Player player) {
        return ZoomAPI.getRankSuffix(player);
    }
}
