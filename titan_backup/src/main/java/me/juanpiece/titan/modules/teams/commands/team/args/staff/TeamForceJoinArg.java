package me.juanpiece.titan.modules.teams.commands.team.args.staff;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamForceJoinArg extends Argument {

    public TeamForceJoinArg(CommandManager manager) {
        super(
                manager,
                Arrays.asList(
                        "forcejoin",
                        "staffjoin"
                )
        );
        this.setPermissible("titan.team.forcejoin");
    }

    @Override
    public String usage() {
        return getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_FORCEJOIN.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        PlayerTeam inTeam = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        PlayerTeam pt = getInstance().getTeamManager().getByPlayerOrTeam(args[0]);

        if (inTeam != null) {
            sendMessage(sender, getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_FORCEJOIN.ALREADY_IN_TEAM"));
            return;
        }

        if (pt == null) {
            sendMessage(sender, Config.TEAM_NOT_FOUND
                    .replace("%team%", args[0])
            );
            return;
        }

        pt.joinMember(player);
        pt.broadcast(getLanguageConfig().getString("ADMIN_TEAM_COMMAND.TEAM_FORCEJOIN.BROADCAST_FORCEJOIN")
                .replace("%player%", player.getName())
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String string = args[args.length - 1];
            return getInstance().getTeamManager().getTeams().values()
                    .stream()
                    .filter(team -> team instanceof PlayerTeam)
                    .map(Team::getName)
                    .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }

        return super.tabComplete(sender, args);
    }
}