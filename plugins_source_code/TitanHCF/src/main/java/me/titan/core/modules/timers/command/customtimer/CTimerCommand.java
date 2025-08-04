package me.titan.core.modules.timers.command.customtimer;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.command.customtimer.args.CTimerCreateArg;
import me.titan.core.modules.timers.command.customtimer.args.CTimerDeleteArg;
import me.titan.core.modules.timers.command.customtimer.args.CTimerListArg;

import java.util.Arrays;
import java.util.List;

public class CTimerCommand extends Command {
    @Override
    public List<String> aliases() {
        return Arrays.asList("ct", "customtimers", "servertimer");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CTIMER_COMMAND.USAGE");
    }
    
    public CTimerCommand(CommandManager manager) {
        super(manager, "customtimer");
        this.setPermissible("titan.customtimer");
        this.handleArguments(Arrays.asList(new CTimerCreateArg(manager), new CTimerDeleteArg(manager), new CTimerListArg(manager)));
    }
}