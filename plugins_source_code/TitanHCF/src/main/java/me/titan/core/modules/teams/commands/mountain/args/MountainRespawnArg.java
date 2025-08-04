package me.titan.core.modules.teams.commands.mountain.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.type.MountainTeam;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MountainRespawnArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("MOUNTAIN_COMMAND.MOUNTAIN_RESPAWN.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        MountainTeam team = this.getInstance().getTeamManager().getMountainTeam(args[0]);
        if (team == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("MOUNTAIN_COMMAND.MOUNTAIN_NOT_FOUND").replaceAll("%mountain%", args[0]));
            return;
        }
        team.resetBlocks();
        this.sendMessage(sender, this.getLanguageConfig().getString("MOUNTAIN_COMMAND.MOUNTAIN_RESPAWN.RESPAWNED").replaceAll("%mountain%", team.getName()));
    }
    
    public MountainRespawnArg(CommandManager manager) {
        super(manager, Collections.singletonList("respawn"));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getTeamManager().getTeams().values().stream().filter(s -> s instanceof MountainTeam).map(Team::getName).filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
