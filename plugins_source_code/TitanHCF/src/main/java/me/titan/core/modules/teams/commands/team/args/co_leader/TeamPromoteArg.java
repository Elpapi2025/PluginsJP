package me.titan.core.modules.teams.commands.team.args.co_leader;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.player.Member;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeamPromoteArg extends Argument {
    private final List<Role> roles;
    
    public TeamPromoteArg(CommandManager manager) {
        super(manager, Collections.singletonList("promote"));
        this.roles = new ArrayList<>(Arrays.asList(Role.values()));
        this.roles.remove(Role.LEADER);
        this.setAsync(true);
    }
    
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
        OfflinePlayer target = CC.getPlayer(args[0]);
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (!team.checkRole(player, Role.CO_LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CO_LEADER.getName()));
            return;
        }
        if (!team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_PROMOTE.NOT_IN_TEAM").replaceAll("%player%", target.getName()));
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_PROMOTE.PROMOTE_SELF"));
            return;
        }
        Member member = team.getMember(target.getUniqueId());
        Role role = this.getRole(member);
        if (member.getRole() == role) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_PROMOTE.HIGHEST_ROLE"));
            return;
        }
        if (role == Role.CO_LEADER && !team.checkRole(player, Role.LEADER)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_PROMOTE.HIGHER_ROLE"));
            return;
        }
        member.setRole(role);
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_PROMOTE.PROMOTED_BROADCAST").replaceAll("%player%", target.getName()).replaceAll("%role%", member.getRole().getName()));
    }
    
    private Role getRole(Member member) {
        if (this.roles.indexOf(member.getRole()) == this.roles.size() - 1 || member.getRole() == Role.LEADER) {
            return member.getRole();
        }
        return this.roles.get(this.roles.indexOf(member.getRole()) + 1);
    }
    
    @Override
    public String usage() {
        return null;
    }
}
