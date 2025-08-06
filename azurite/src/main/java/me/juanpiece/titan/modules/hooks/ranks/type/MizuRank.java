package me.juanpiece.titan.modules.hooks.ranks.type;

import com.broustudio.MizuAPI.MizuAPI;
import me.juanpiece.titan.modules.hooks.ranks.Rank;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
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