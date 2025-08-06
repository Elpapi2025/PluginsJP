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
public class EventTeam extends Team {

    public EventTeam(TeamManager manager, Map<String, Object> map) {
        super(
                manager,
                map,
                true,
                TeamType.EVENT
        );
    }

    public EventTeam(TeamManager manager, String name) {
        super(
                manager,
                name,
                UUID.randomUUID(),
                true,
                TeamType.EVENT
        );
    }

    @Override
    public String getDisplayName(Player player) {
        return Config.DISPLAY_NAME_EVENT.replace("%team%", super.getDisplayName(player));
    }
}