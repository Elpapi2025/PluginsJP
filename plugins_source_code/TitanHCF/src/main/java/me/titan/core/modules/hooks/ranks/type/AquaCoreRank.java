package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import org.bukkit.entity.*;
import me.activated.core.plugin.*;

public class AquaCoreRank implements Rank {
    @Override
    public String getRankColor(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getColor().toString();
    }
    
    @Override
    public String getRankSuffix(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getSuffix();
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getPrefix();
    }
    
    @Override
    public String getRankName(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getName();
    }
}
