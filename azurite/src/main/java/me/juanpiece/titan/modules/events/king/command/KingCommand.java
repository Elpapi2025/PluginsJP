package me.juanpiece.titan.modules.events.king.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.king.command.args.KingEndArg;
import me.juanpiece.titan.modules.events.king.command.args.KingStartArg;
import me.juanpiece.titan.modules.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KingCommand extends Command {

    public KingCommand(CommandManager manager) {
        super(
                manager,
                "king"
        );
        this.setPermissible("titan.ktk");
        this.handleArguments(Arrays.asList(
                new KingEndArg(manager),
                new KingStartArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "ktk"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("KING_COMMAND.USAGE");
    }
}