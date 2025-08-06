package me.juanpiece.titan.modules.timers.command.timer;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.timers.command.timer.args.TimerAddArg;
import me.juanpiece.titan.modules.timers.command.timer.args.TimerRemoveArg;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TimerCommand extends Command {

    public TimerCommand(CommandManager manager) {
        super(
                manager,
                "timer"
        );

        this.setPermissible("titan.timer");
        this.handleArguments(Arrays.asList(
                new TimerRemoveArg(manager),
                new TimerAddArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "timers",
                "cooldown"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("TIMER_COMMAND.USAGE");
    }
}