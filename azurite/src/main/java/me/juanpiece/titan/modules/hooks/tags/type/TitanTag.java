package me.juanpiece.titan.modules.hooks.tags.type;

import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.hooks.tags.Tag;
import me.juanpiece.core.CoreAPI;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TitanTag implements Tag {

    private final CoreAPI coreAPI;

    public TitanTag(HCF instance) {
        this.coreAPI = new CoreAPI(instance);
    }

    @Override
    public String getTag(Player player) {
        return coreAPI.getTag(player.getUniqueId());
    }
}