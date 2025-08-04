package me.titan.core.modules.teams.commands.team.args.captain;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamWithdrawArg extends Argument {
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
        Integer money = this.getInt(args[0]);
        if (money == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[0]));
            return;
        }
        if (team.getBalance() < money) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_WITHDRAW.INSUFFICIENT_BAL"));
            return;
        }
        this.getInstance().getBalanceManager().giveBalance(player, money);
        team.setBalance(team.getBalance() - money);
        team.save();
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_WITHDRAW.WITHDREW").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(money)));
    }
    
    public TeamWithdrawArg(CommandManager manager) {
        super(manager, Arrays.asList("withdraw", "w"));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_WITHDRAW.USAGE");
    }
}
