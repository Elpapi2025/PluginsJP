package me.titan.core.modules.timers.command.timer;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.command.timer.args.TimerAddArg;
import me.titan.core.modules.timers.command.timer.args.TimerRemoveArg;

import java.util.Arrays;
import java.util.List;

public class TimerCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TIMER_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("timers", "cooldown");
    }
    
    public TimerCommand(CommandManager manager) {
        super(manager, "timer");
        this.setPermissible("titan.timer");
        this.handleArguments(Arrays.asList(new TimerRemoveArg(manager), new TimerAddArg(manager)));
    }
}