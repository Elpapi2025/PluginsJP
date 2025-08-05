package me.keano.azurite.modules.kits.commands;

import me.keano.azurite.modules.commands.CommandManager;
import me.keano.azurite.modules.framework.commands.Command;
import me.keano.azurite.modules.kits.commands.args.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KitCommand extends Command {

    public KitCommand(CommandManager manager) {
        super(
                manager,
                "kit"
        );
        this.setPermissible("azurite.kit");
        this.handleArguments(Arrays.asList(
                new KitCreateArg(manager),
                new KitDeleteArg(manager),
                new KitSetItemsArg(manager),
                new KitApplyArg(manager),
                new KitSetCooldownArg(manager),
                new KitSetNameArg(manager),
                new KitListArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "kits"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("KIT_COMMAND.USAGE");
    }
}