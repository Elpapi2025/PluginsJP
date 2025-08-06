package me.juanpiece.titan.modules.teams.commands.mountain;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.teams.commands.mountain.args.MountainReloadArg;
import me.juanpiece.titan.modules.teams.commands.mountain.args.MountainRespawnArg;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class MountainCommand extends Command {

    public MountainCommand(CommandManager manager) {
        super(
                manager,
                "mountain"
        );
        this.setPermissible("titan.mountain");
        this.handleArguments(Arrays.asList(
                new MountainReloadArg(manager),
                new MountainRespawnArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "mountains",
                "adminglowstone",
                "adminmountains",
                "mt"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("MOUNTAIN_COMMAND.USAGE");
    }
}