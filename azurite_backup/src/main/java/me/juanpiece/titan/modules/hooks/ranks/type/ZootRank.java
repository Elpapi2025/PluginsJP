package me.juanpiece.titan.modules.hooks.ranks.type;

import com.minexd.zoot.profile.Profile;
import me.juanpiece.titan.modules.hooks.ranks.Rank;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ZootRank implements Rank {

    @Override
    public String getRankName(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getPrefix();
    }

    @Override
    public String getRankSuffix(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getSuffix();
    }

    @Override
    public String getRankColor(Player player) {
        return Profile.getByUuid(player.getUniqueId()).getActiveRank().getColor().toString();
    }
}