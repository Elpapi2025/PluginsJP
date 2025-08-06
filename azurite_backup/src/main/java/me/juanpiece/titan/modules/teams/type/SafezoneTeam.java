package me.juanpiece.titan.modules.teams.type;

import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.enums.TeamType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SafezoneTeam extends Team {

    public SafezoneTeam(TeamManager manager, Map<String, Object> map) {
        super(
                manager,
                map,
                false,
                TeamType.SAFEZONE
        );
    }

    public SafezoneTeam(TeamManager manager, String name) {
        super(
                manager,
                name,
                UUID.randomUUID(),
                false,
                TeamType.SAFEZONE
        );
    }

    @Override
    public String getDisplayName(Player player) {
        return Config.DISPLAY_NAME_SAFEZONE.replace("%team%", super.getDisplayName(player));
    }

    public boolean canMine(Block block) {
        return Config.ALLOW_ORES_MINE_SPAWN && block.getType().name().endsWith("_ORE");
    }
}