package me.titan.core.modules.teams.commands.team.args.captain;

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

public class TeamUninviteArg extends Argument {
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
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.CAPTAIN)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.CAPTAIN.getName()));
            return;
        }
        if (args[0].equalsIgnoreCase("ALL")) {
            team.getInvitedPlayers().clear();
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNINVITE.UNINVITED_ALL"));
            return;
        }
        OfflinePlayer target = CC.getPlayer(args[0]);
        if (this.getInstance().getUserManager().getByUUID(target.getUniqueId()) == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (!team.getInvitedPlayers().contains(target.getUniqueId())) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNINVITE.NEVER_INVITED"));
            return;
        }
        team.getInvitedPlayers().remove(target.getUniqueId());
        this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNINVITE.UNINVITED_PLAYER").replaceAll("%player%", target.getName()));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_UNINVITE.USAGE");
    }
    
    public TeamUninviteArg(CommandManager manager) {
        super(manager, Collections.singletonList("uninvite"));
        this.setAsync(true);
    }
}
