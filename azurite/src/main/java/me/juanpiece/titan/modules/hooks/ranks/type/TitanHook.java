package me.juanpiece.titan.modules.hooks.ranks.type;

import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.hooks.ranks.Rank;
import me.keano.core.CoreAPI;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TitanHook implements Rank {

    private final CoreAPI coreAPI;

    public TitanHook(HCF instance) {
        this.coreAPI = new CoreAPI(instance);
    }

    @Override
    public String getRankName(Player player) {
        return coreAPI.getRankName(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return coreAPI.getRankPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return coreAPI.getRankSuffix(player.getUniqueId());
    }

    @Override
    public String getRankColor(Player player) {
        return coreAPI.getRankColor(player.getUniqueId());
    }
}