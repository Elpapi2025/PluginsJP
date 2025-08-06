package me.juanpiece.titan.modules.hooks.ranks.type;

import me.juanpiece.titan.modules.hooks.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ZPermissionRank implements Rank {

    private final ZPermissionsService service;

    public ZPermissionRank() {
        this.service = Bukkit.getServicesManager().load(ZPermissionsService.class);
    }

    @Override
    public String getRankName(Player player) {
        return service.getPlayerPrimaryGroup(player.getUniqueId());
    }

    @Override
    public String getRankPrefix(Player player) {
        return service.getPlayerPrefix(player.getUniqueId());
    }

    @Override
    public String getRankSuffix(Player player) {
        return service.getPlayerSuffix(player.getUniqueId());
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }
}