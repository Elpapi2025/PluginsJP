package me.titan.core.modules.events.koth.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.koth.command.args.*;
import me.titan.core.modules.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KothCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KOTH_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    public KothCommand(CommandManager manager) {
        super(manager, "koth");
        this.setPermissible("titan.koth");
        this.handleArguments(Arrays.asList(new KothClaimArg(manager), new KothCreateArg(manager), new KothDeleteArg(manager), new KothStartArg(manager), new KothEndArg(manager), new KothSetColorArg(manager), new KothSetMinArg(manager), new KothSetRemArg(manager)));
    }
}
