package me.titan.core.modules.teams.commands.mountain;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.teams.commands.mountain.args.MountainReloadArg;
import me.titan.core.modules.teams.commands.mountain.args.MountainRespawnArg;

import java.util.Arrays;
import java.util.List;

public class MountainCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("MOUNTAIN_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("mountains", "adminglowstone", "adminmountains", "mt");
    }
    
    public MountainCommand(CommandManager manager) {
        super(manager, "mountain");
        this.setPermissible("titan.mountain");
        this.handleArguments(Arrays.asList(new MountainReloadArg(manager), new MountainRespawnArg(manager)));
    }
}
