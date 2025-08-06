package me.juanpiece.titan.modules.teams.commands.team.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.waypoints.Waypoint;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamUnfocusArg extends Argument {

    public TeamUnfocusArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "unfocus"
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

        if (pt.getFocus() == null) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.NO_FOCUS"));
            return;
        }

        Waypoint focusWaypoint = getInstance().getWaypointManager().getFocusWaypoint();
        Team focusTeam = pt.getFocusedTeam();

        // Titan - Lunar Integration
        for (Player member : pt.getOnlinePlayers(true)) {
            if (focusTeam != null) {
                focusWaypoint.remove(member, focusTeam.getHq(), s -> s
                        .replace("%team%", focusTeam.getName())
                );
            }
        }

        pt.setFocus(null);
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNFOCUS.FOCUS_CLEARED"));
    }
}