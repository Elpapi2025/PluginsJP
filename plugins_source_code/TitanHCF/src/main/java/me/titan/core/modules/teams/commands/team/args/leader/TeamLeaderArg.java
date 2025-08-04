package me.titan.core.modules.teams.commands.team.args.leader;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamLeaderArg extends Argument
{
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
        if (!team.checkRole(player, Role.LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.LEADER.getName()));
            return;
        }
        if (!team.getPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEADER.NOT_IN_TEAM"));
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEADER.ALREADY_LEADER"));
            return;
        }
        team.getMember(player.getUniqueId()).setRole(Role.CO_LEADER);
        team.getMember(target.getUniqueId()).setRole(Role.LEADER);
        team.setLeader(target.getUniqueId());
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEADER.LEADER_CHANGED").replaceAll("%player%", target.getName()));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_LEADER.USAGE");
    }
    
    public TeamLeaderArg(CommandManager manager) {
        super(manager, Collections.singletonList("leader"));
        this.setAsync(true);
    }
}
