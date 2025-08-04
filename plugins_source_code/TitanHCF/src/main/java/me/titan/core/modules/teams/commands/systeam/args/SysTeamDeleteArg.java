package me.titan.core.modules.teams.commands.systeam.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SysTeamDeleteArg extends Argument {
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getSystemTeams().values().stream().map(Team::getName).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Team team = this.getInstance().getTeamManager().getTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, Config.TEAM_NOT_FOUND.replaceAll("%team%", args[0]));
            return;
        }
        team.delete();
        this.sendMessage(sender, this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_DELETE.DELETED_TEAM").replaceAll("%team%", args[0]));
    }
    
    public SysTeamDeleteArg(CommandManager manager) {
        super(manager, Collections.singletonList("delete"));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("SYSTEM_TEAM_COMMAND.SYSTEM_TEAM_DELETE.USAGE");
    }
}
