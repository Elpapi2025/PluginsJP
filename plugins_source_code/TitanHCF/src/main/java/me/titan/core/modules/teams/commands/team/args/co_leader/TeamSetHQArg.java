package me.titan.core.modules.teams.commands.team.args.co_leader;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public class TeamSetHQArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (playerTeam == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!playerTeam.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (playerTeam != team) {
            player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SETHQ.CANNOT_SET"));
            return;
        }
        Waypoint waypoint = this.getInstance().getWaypointManager().getHqWaypoint();
        for (Player online : playerTeam.getOnlinePlayers()) {
            waypoint.remove(online, playerTeam.getHq(), UnaryOperator.identity());
            waypoint.send(online, player.getLocation(), UnaryOperator.identity());
        }
        playerTeam.setHq(player.getLocation());
        playerTeam.save();
        playerTeam.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SETHQ.SETHQ").replaceAll("%player%", player.getName()));
    }
    
    @Override
    public String usage() {
        return null;
    }
    
    public TeamSetHQArg(CommandManager manager) {
        super(manager, Arrays.asList("sethq", "sethome"));
    }
}