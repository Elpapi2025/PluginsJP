package me.titan.core.modules.teams.type;

import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.TeamManager;
import me.titan.core.modules.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class SafezoneTeam extends Team {
    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.SAFEZONE") + super.getDisplayName(player);
    }
    
    public SafezoneTeam(TeamManager manager, String name) {
        super(manager, name, UUID.randomUUID(), false, TeamType.SAFEZONE);
    }
    
    public SafezoneTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, false, TeamType.SAFEZONE);
    }
}
