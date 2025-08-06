package me.juanpiece.titan.modules.teams.commands.team.args.co_leader;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamRosterArg extends Argument {

    public TeamRosterArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "roster"
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

        if (!pt.checkRole(player, Role.CO_LEADER)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.CO_LEADER.getName())
            );
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                for (String s : getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_ROSTER.ROSTER_LIST.FORMAT")) {
                    if (!s.equalsIgnoreCase("%roster%")) {
                        sendMessage(sender, s);
                        continue;
                    }

                    for (UUID uuid : pt.getRoster()) {
                        User member = getInstance().getUserManager().getByUUID(uuid);
                        sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_ROSTER.ROSTER_LIST.PLAYER_FORMAT")
                                .replace("%player%", member.getName())
                        );
                    }
                }
                return;

            case "add":
                if (args.length == 1) {
                    sendUsage(sender);
                    return;
                }

                User add = getInstance().getUserManager().getByName(args[1]);

                if (add == null) {
                    sendMessage(sender, Config.PLAYER_NOT_FOUND
                            .replace("%player%", args[1])
                    );
                    return;
                }

                if (pt.getRoster().contains(add.getUniqueID())) {
                    sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_ROSTER.ROSTER_ADD.ALREADY_IN")
                            .replace("%player%", add.getName())
                    );
                    return;
                }

                pt.getRoster().add(add.getUniqueID());
                pt.save();
                sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_ROSTER.ROSTER_ADD.ADDED")
                        .replace("%player%", add.getName())
                );
                return;

            case "remove":
                if (args.length == 1) {
                    sendUsage(sender);
                    return;
                }

                User remove = getInstance().getUserManager().getByName(args[1]);

                if (remove == null) {
                    sendMessage(sender, Config.PLAYER_NOT_FOUND
                            .replace("%player%", args[1])
                    );
                    return;
                }

                if (!pt.getRoster().contains(remove.getUniqueID())) {
                    sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_ROSTER.ROSTER_REMOVE.NOT_IN")
                            .replace("%player%", remove.getName())
                    );
                    return;
                }

                pt.getRoster().remove(remove.getUniqueID());
                pt.save();
                sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_ROSTER.ROSTER_REMOVE.REMOVED")
                        .replace("%player%", remove.getName())
                );
                return;
        }

        sendUsage(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String string = args[args.length - 1];
            return Stream.of("remove", "add", "list")
                    .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }

        return super.tabComplete(sender, args);
    }

    @Override
    public void sendUsage(CommandSender sender) {
        for (String s : getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_ROSTER.USAGE")) {
            sendMessage(sender, s);
        }
    }
}