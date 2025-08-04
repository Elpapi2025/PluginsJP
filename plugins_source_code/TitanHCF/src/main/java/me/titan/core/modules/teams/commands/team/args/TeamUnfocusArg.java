package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamUnfocusArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.USAGE");
    }
    
    public TeamUnfocusArg(CommandManager manager) {
        super(manager, Collections.singletonList("unfocus"));
    }
    
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
        if (team.getFocus() == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.NO_FOCUS"));
            return;
        }
        Waypoint waypoint = this.getInstance().getWaypointManager().getFocusWaypoint();
        PlayerTeam targetTeam = team.getFocusedTeam();
        for (Player online : team.getOnlinePlayers()) {
            waypoint.remove(online, targetTeam.getHq(), w -> w.replaceAll("%team%", targetTeam.getName()));
        }
        team.setFocus(null);
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.FOCUS_CLEARED"));
        this.getInstance().getNametagManager().update();
    }
}
