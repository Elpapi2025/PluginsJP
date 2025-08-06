package me.juanpiece.titan.modules.events.conquest.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.conquest.command.args.ConquestClaimArg;
import me.juanpiece.titan.modules.events.conquest.command.args.ConquestEndArg;
import me.juanpiece.titan.modules.events.conquest.command.args.ConquestSetPointsArg;
import me.juanpiece.titan.modules.events.conquest.command.args.ConquestStartArg;
import me.juanpiece.titan.modules.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ConquestCommand extends Command {

    public ConquestCommand(CommandManager manager) {
        super(
                manager,
                "conquest"
        );
        this.setPermissible("titan.conquest");
        this.handleArguments(Arrays.asList(
                new ConquestStartArg(manager),
                new ConquestEndArg(manager),
                new ConquestClaimArg(manager),
                new ConquestSetPointsArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("CONQUEST_COMMAND.USAGE");
    }
}