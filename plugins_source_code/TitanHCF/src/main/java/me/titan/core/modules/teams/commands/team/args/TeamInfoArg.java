package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamInfoArg extends Argument {
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getStringTeams().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
    
    public TeamInfoArg(CommandManager manager) {
        super(manager, Arrays.asList("info", "who", "i"));
        this.setAsync(true);
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                this.sendUsage(sender);
                return;
            }
            Player player = (Player)sender;
            PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
            if (team != null) {
                for (String s : team.getTeamInfo(player)) {
                    player.sendMessage(CC.t(s));
                }
                return;
            }
            this.sendMessage(sender, Config.NOT_IN_TEAM);
        }
        else {
            OfflinePlayer target = CC.getPlayer(args[0]);
            PlayerTeam playerTeam = this.getInstance().getTeamManager().getByPlayer(target.getUniqueId());
            Team team = this.getInstance().getTeamManager().getTeam(args[0]);
            if (playerTeam == null && team == null) {
                this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
                return;
            }
            if (playerTeam != null) {
                for (String s : playerTeam.getTeamInfo(sender)) {
                    sender.sendMessage(CC.t(s));
                }
            }
            if (team != null) {
                for (String s : team.getTeamInfo(sender)) {
                    sender.sendMessage(CC.t(s));
                }
            }
        }
    }
}
