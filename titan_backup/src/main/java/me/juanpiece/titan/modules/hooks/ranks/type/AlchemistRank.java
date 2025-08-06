package me.juanpiece.titan.modules.hooks.ranks.type;

import ltd.matrixstudios.alchemist.api.AlchemistAPI;
import me.juanpiece.titan.modules.hooks.ranks.Rank;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AlchemistRank implements Rank {

    @Override
    public String getRankName(Player player) {
        return AlchemistAPI.INSTANCE.findRank(player.getUniqueId()).getName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return AlchemistAPI.INSTANCE.findRank(player.getUniqueId()).getPrefix();
    }

    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public String getRankColor(Player player) {
        return AlchemistAPI.INSTANCE.findRank(player.getUniqueId()).getColor();
    }
}