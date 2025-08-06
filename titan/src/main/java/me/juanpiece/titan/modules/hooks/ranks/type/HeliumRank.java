package me.juanpiece.titan.modules.hooks.ranks.type;

import dev.rage.helium.HeliumAPI;
import me.juanpiece.titan.modules.hooks.ranks.Rank;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class HeliumRank implements Rank {

    private final HeliumAPI heliumAPI;

    public HeliumRank() {
        this.heliumAPI = new HeliumAPI();
    }

    @Override
    public String getRankName(Player player) {
        return heliumAPI.getProfileByUUID(player.getUniqueId()).getActiveRank().getName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return heliumAPI.getProfileByUUID(player.getUniqueId()).getActiveRank().getPrefix();
    }

    @Override
    public String getRankSuffix(Player player) {
        return heliumAPI.getProfileByUUID(player.getUniqueId()).getActiveRank().getSuffix();
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }
}