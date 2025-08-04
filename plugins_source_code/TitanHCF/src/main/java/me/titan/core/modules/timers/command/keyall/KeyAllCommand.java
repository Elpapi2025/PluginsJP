package me.titan.core.modules.timers.command.keyall;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.command.keyall.args.KeyAllCreateArg;
import me.titan.core.modules.timers.command.keyall.args.KeyAllDeleteArg;
import me.titan.core.modules.timers.command.keyall.args.KeyAllListArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyAllCommand extends Command {
    public KeyAllCommand(CommandManager manager) {
        super(manager, "keyall");
        this.setPermissible("titan.keyall");
        this.handleArguments(Arrays.asList(new KeyAllCreateArg(manager), new KeyAllDeleteArg(manager), new KeyAllListArg(manager)));
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KEY_ALL_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}