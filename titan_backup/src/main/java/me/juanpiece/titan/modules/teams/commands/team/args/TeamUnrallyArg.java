package me.juanpiece.titan.modules.teams.commands.team.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamUnrallyArg extends Argument {

    public TeamUnrallyArg(CommandManager manager) {
        super(
                manager,
                Arrays.asList(
                        "unrally",
                        "removerally"
                )
        );
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        Player player = (Player) sender;
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (pt.getRallyPoint() == null) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNRALLY.NO_RALLY"));
            return;
        }

        Waypoint rallyWaypoint = getInstance().getWaypointManager().getRallyWaypoint();

        // Titan - Lunar Integration
        for (Player member : pt.getOnlinePlayers(true)) {
            rallyWaypoint.remove(member, pt.getRallyPoint(), UnaryOperator.identity());
        }

        pt.setRallyPoint(null);
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNRALLY.UNRALLIED"));
    }
}