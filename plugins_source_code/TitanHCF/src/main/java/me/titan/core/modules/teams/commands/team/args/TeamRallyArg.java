package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamRallyArg extends Argument {
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
        Waypoint waypoint = this.getInstance().getWaypointManager().getRallyWaypoint();
        for (Player online : team.getOnlinePlayers()) {
            waypoint.remove(online, team.getRallyPoint(), UnaryOperator.identity());
            waypoint.send(online, player.getLocation(), UnaryOperator.identity());
        }
        team.setRallyPoint(player.getLocation());
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RALLY.UPDATED").replaceAll("%location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY()));
    }
    
    public TeamRallyArg(CommandManager manager) {
        super(manager, Collections.singletonList("rally"));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_RALLY.USAGE");
    }
}
