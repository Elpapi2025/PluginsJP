package me.juanpiece.titan.modules.teams.type;

import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ConquestTeam extends Team {

    public ConquestTeam(TeamManager manager, Map<String, Object> map) {
        super(
                manager,
                map,
                true,
                TeamType.CONQUEST
        );
    }

    public ConquestTeam(TeamManager manager, String name) {
        super(
                manager,
                name,
                UUID.randomUUID(),
                true,
                TeamType.CONQUEST
        );
    }

    @Override
    public String getDisplayName(Player player) {
        return Config.DISPLAY_NAME_CONQUEST.replace("%team%", super.getDisplayName(player));
    }
}