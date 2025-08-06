package me.juanpiece.titan.modules.events.conquest.command.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.conquest.Conquest;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ConquestEndArg extends Argument {

    public ConquestEndArg(CommandManager manager) {
        super(
                manager,
                Arrays.asList(
                        "end",
                        "stop"
                )
        );
        this.setPermissible("titan.conquest.end");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        Conquest conquest = getInstance().getConquestManager().getConquest();

        if (!conquest.isActive()) {
            sendMessage(sender, getLanguageConfig().getString("CONQUEST_COMMAND.CONQUEST_END.NOT_ACTIVE"));
            return;
        }

        conquest.end();
        sendMessage(sender, getLanguageConfig().getString("CONQUEST_COMMAND.CONQUEST_END.ENDED"));
    }
}