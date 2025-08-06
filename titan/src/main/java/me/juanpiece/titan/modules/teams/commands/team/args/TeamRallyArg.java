package me.juanpiece.titan.modules.teams.commands.team.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.waypoints.Waypoint;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamRallyArg extends Argument {

    public TeamRallyArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "rally"
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

        Waypoint rallyWaypoint = getInstance().getWaypointManager().getRallyWaypoint();
        Location location = player.getLocation();

        // Titan - Lunar Integration
        for (Player member : pt.getOnlinePlayers(true)) {
            rallyWaypoint.remove(member, pt.getRallyPoint(), UnaryOperator.identity());
            rallyWaypoint.send(member, location, UnaryOperator.identity());
        }

        pt.setRallyPoint(location);
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_RALLY.UPDATED")
                .replace("%player%", player.getName())
                .replace("%world%", Utils.getWorldName(player.getWorld()))
                .replace("%location%", location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ())
        );
    }
}