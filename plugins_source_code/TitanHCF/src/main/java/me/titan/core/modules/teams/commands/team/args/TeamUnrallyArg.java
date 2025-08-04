package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public class TeamUnrallyArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (team.getRallyPoint() == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNRALLY.NO_RALLY"));
            return;
        }
        Waypoint waypoint = this.getInstance().getWaypointManager().getRallyWaypoint();
        for (Player online : team.getOnlinePlayers()) {
            waypoint.remove(online, team.getRallyPoint(), UnaryOperator.identity());
        }
        team.setRallyPoint(null);
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNRALLY.UNRALLIED"));
    }
    
    public TeamUnrallyArg(CommandManager manager) {
        super(manager, Arrays.asList("unrally", "removerally"));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNRALLY.USAGE");
    }
}
