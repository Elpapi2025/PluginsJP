package me.juanpiece.titan.modules.hooks.ranks;

import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface Rank {

    String getRankName(Player player);

    String getRankPrefix(Player player);

    String getRankSuffix(Player player);

    String getRankColor(Player player);

}