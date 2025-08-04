package me.titan.core.modules.teams.type;

import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.TeamManager;
import me.titan.core.modules.teams.enums.TeamType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WildernessTeam extends Team {
    @Override
    public String getDisplayName(Player player) {
        return this.getTeamConfig().getString("SYSTEM_TEAMS.WILDERNESS") + super.getDisplayName(player);
    }
    
    public WildernessTeam(TeamManager manager) {
        super(manager, "Wilderness", UUID.randomUUID(), true, TeamType.WILDERNESS);
    }
}
