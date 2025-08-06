package me.juanpiece.titan.modules.timers.command.keyall;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.timers.command.keyall.args.KeyAllCreateArg;
import me.juanpiece.titan.modules.timers.command.keyall.args.KeyAllDeleteArg;
import me.juanpiece.titan.modules.timers.command.keyall.args.KeyAllListArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KeyAllCommand extends Command {

    public KeyAllCommand(CommandManager manager) {
        super(
                manager,
                "keyall"
        );
        this.setPermissible("titan.keyall");
        this.handleArguments(Arrays.asList(
                new KeyAllCreateArg(manager),
                new KeyAllDeleteArg(manager),
                new KeyAllListArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("KEY_ALL_COMMAND.USAGE");
    }
}