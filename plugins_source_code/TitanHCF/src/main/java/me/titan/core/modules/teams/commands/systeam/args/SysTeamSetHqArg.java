package me.titan.core.modules.teams.commands.systeam.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.enums.MountainType;
import me.titan.core.modules.teams.type.MountainTeam;
import me.titan.core.modules.waypoints.Waypoint;
import me.titan.core.modules.waypoints.WaypointManager;
import me.titan.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SysTeamSetHqArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        Team team = this.getInstance().getTeamManager().getTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        if (team instanceof MountainTeam) {
            MountainTeam mountainTeam = (MountainTeam)team;
            WaypointManager manager = this.getInstance().getWaypointManager();
            Waypoint waypoint = (mountainTeam.getMountainType() == MountainType.GLOWSTONE) ? manager.getGlowstoneWaypoint() : manager.getOreMountainWaypoint();
            for (Player online : Bukkit.getOnlinePlayers()) {
                waypoint.remove(online, team.getHq(), UnaryOperator.identity());
                waypoint.send(online, player.getLocation(), UnaryOperator.identity());
            }
        }
        team.setHq(player.getLocation());
        team.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_SETHQ.SET_HQ").replaceAll("%team%", team.getDisplayName(player)).replaceAll("%location%", Utils.formatLocation(player.getLocation())));
    }
    
    public SysTeamSetHqArg(CommandManager manager) {
        super(manager, Arrays.asList("sethq", "sethome"));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_SETHQ.USAGE");
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getSystemTeams().values().stream().map(Team::getName).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
