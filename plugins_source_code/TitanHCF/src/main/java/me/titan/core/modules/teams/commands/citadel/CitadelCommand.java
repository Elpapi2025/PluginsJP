package me.titan.core.modules.teams.commands.citadel;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.teams.commands.citadel.args.CitadelReloadArg;
import me.titan.core.modules.teams.commands.citadel.args.CitadelRespawnArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CitadelCommand extends Command {
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    public CitadelCommand(CommandManager manager) {
        super(manager, "citadel");
        this.setPermissible("titan.citadel");
        this.handleArguments(Arrays.asList(new CitadelReloadArg(manager), new CitadelRespawnArg(manager)));
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CITADEL_COMMAND.USAGE");
    }
}
