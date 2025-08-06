package me.juanpiece.titan.modules.teams.commands.team.args.captain;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.eotw.EOTWManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.player.Member;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamKickArg extends Argument {

    public TeamKickArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "kick"
                )
        );
    }

    @Override
    public String usage() {
        return getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        User target = getInstance().getUserManager().getByName(args[0]);
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        EOTWManager eotwManager = getInstance().getEotwManager();

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (!pt.checkRole(player, Role.CAPTAIN)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.CAPTAIN.getName())
            );
            return;
        }

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        Member targetMember = pt.getMember(target.getUniqueID());

        if (!pt.getPlayers().contains(target.getUniqueID())) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.NOT_IN_TEAM"));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueID())) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.CANNOT_KICK_SELF"));
            return;
        }

        if (targetMember.getRole() == Role.LEADER || targetMember.getRole() == Role.CO_LEADER) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.HIGHER_ROLE")
                    .replace("%player%", target.getName())
            );
            return;
        }

        if (pt.hasRegen() && !eotwManager.isActive() && !getTeamConfig().getBoolean("TEAMS.KICK_WHILE_REGEN")) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.CANNOT_KICK_FREEZE"));
            return;
        }

        pt.removeMember(target);
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.BROADCAST_TEAM")
                .replace("%player%", target.getName())
        );

        Player playerObject = target.getPlayer();

        if (playerObject != null) {
            sendMessage(playerObject, getLanguageConfig().getString("TEAM_COMMAND.TEAM_KICK.KICKED_MESSAGE")
                    .replace("%team%", pt.getName())
            );
        }
    }
}