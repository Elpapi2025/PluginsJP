package me.juanpiece.titan.modules.teams.commands.citadel;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.teams.commands.citadel.args.CitadelReloadArg;
import me.juanpiece.titan.modules.teams.commands.citadel.args.CitadelRespawnArg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CitadelCommand extends Command {

    public CitadelCommand(CommandManager manager) {
        super(
                manager,
                "citadel"
        );
        this.setPermissible("titan.citadel");
        this.handleArguments(Arrays.asList(
                new CitadelReloadArg(manager),
                new CitadelRespawnArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("CITADEL_COMMAND.USAGE");
    }
}