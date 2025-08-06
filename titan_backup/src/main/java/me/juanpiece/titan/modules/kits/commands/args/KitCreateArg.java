package me.juanpiece.titan.modules.kits.commands.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KitCreateArg extends Argument {

    public KitCreateArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "create"
                )
        );
        this.setPermissible("titan.kit.create");
    }

    @Override
    public String usage() {
        return getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String name = args[0];

        if (getInstance().getKitManager().getKit(name) != null) {
            sendMessage(sender, getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.ALREADY_EXISTS")
                    .replace("%kit%", name)
            );
            return;
        }

        Kit kit = new Kit(getInstance().getKitManager(), name);
        kit.save();

        sendMessage(sender, getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.CREATED")
                .replace("%kit%", name)
        );
    }
}