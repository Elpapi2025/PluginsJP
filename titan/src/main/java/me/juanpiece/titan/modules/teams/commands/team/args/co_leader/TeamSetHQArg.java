package me.juanpiece.titan.modules.teams.commands.team.args.co_leader;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.player.Role;
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
public class TeamSetHQArg extends Argument {

    public TeamSetHQArg(CommandManager manager) {
        super(
                manager,
                Arrays.asList(
                        "sethq",
                        "sethome"
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
        Team atPlayer = getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (!pt.checkRole(player, Role.CO_LEADER)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.CO_LEADER.getName())
            );
            return;
        }

        if (pt != atPlayer) {
            player.sendMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_SETHQ.CANNOT_SET"));
            return;
        }

        Waypoint hqWaypoint = getInstance().getWaypointManager().getHqWaypoint();

        // Titan - Lunar Integration
        for (Player member : pt.getOnlinePlayers(true)) {
            hqWaypoint.remove(member, pt.getHq(), UnaryOperator.identity());
            hqWaypoint.send(member, player.getLocation(), UnaryOperator.identity());
        }

        pt.setHq(player.getLocation());
        pt.save();
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_SETHQ.SETHQ")
                .replace("%player%", player.getName())
        );
    }
}