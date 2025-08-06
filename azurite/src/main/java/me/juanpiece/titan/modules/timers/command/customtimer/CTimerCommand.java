package me.juanpiece.titan.modules.timers.command.customtimer;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.timers.command.customtimer.args.CTimerCreateArg;
import me.juanpiece.titan.modules.timers.command.customtimer.args.CTimerDeleteArg;
import me.juanpiece.titan.modules.timers.command.customtimer.args.CTimerListArg;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CTimerCommand extends Command {

    public CTimerCommand(CommandManager manager) {
        super(
                manager,
                "customtimer"
        );

        this.setPermissible("titan.customtimer");
        this.handleArguments(Arrays.asList(
                new CTimerCreateArg(manager),
                new CTimerDeleteArg(manager),
                new CTimerListArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "ct",
                "customtimers",
                "servertimer"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("CTIMER_COMMAND.USAGE");
    }
}