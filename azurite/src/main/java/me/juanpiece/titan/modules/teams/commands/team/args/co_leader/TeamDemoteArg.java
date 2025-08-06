package me.juanpiece.titan.modules.teams.commands.team.args.co_leader;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.player.Member;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamDemoteArg extends Argument {

    private final List<Role> roles;

    public TeamDemoteArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "demote"
                )
        );
        this.roles = new ArrayList<>(Arrays.asList(Role.values()));
        Collections.reverse(roles); // going down now not up like /t promote
    }

    @Override
    public String usage() {
        return getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.USAGE");
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

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        if (!pt.checkRole(player, Role.CO_LEADER)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.CO_LEADER.getName())
            );
            return;
        }

        if (!pt.getPlayers().contains(target.getUniqueID())) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.NOT_IN_TEAM")
                    .replace("%player%", target.getName())
            );
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueID())) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.DEMOTE_SELF"));
            return;
        }

        Member targetMember = pt.getMember(target.getUniqueID());
        Role toPromote = getRole(targetMember);

        if (targetMember.getRole() == toPromote) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.LOWEST_ROLE"));
            return;
        }

        // the target has a higher or equal role
        if (!pt.checkRole(player, targetMember.getRole())) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.HIGHER_ROLE"));
            return;
        }

        targetMember.setRole(toPromote);
        pt.save();
        pt.broadcast(getLanguageConfig().getString("TEAM_COMMAND.TEAM_DEMOTE.DEMOTED_BROADCAST")
                .replace("%player%", target.getName())
                .replace("%role%", toPromote.getName())
        );
    }

    private Role getRole(Member member) {
        if (roles.indexOf(member.getRole()) == roles.size() - 1) {
            return member.getRole(); // already lowest
        }

        return roles.get(roles.indexOf(member.getRole()) + 1);
    }
}