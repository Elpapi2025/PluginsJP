package me.juanpiece.titan.modules.teams.commands.team.args.leader;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamDisbandArg extends Argument {

    public TeamDisbandArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "disband"
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
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }

        Player player = (Player) sender;
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (!pt.checkRole(player, Role.LEADER)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.LEADER.getName())
            );
            return;
        }

        if (pt.hasRegen() && !getTeamConfig().getBoolean("TEAMS.DISBAND_WHILE_REGEN")) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.CANNOT_DISBAND_REGEN"));
            return;
        }

        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_TEAM"));
        pt.disband(true);
        Bukkit.broadcastMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_BROADCAST")
                .replace("%team%", pt.getName())
                .replace("%player%", player.getName())
                .replace("%rank-prefix%", CC.t(getInstance().getRankHook().getRankPrefix(player)))
                .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(player)))
        );
    }
}