package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CoordsCommand extends Command {

    public CoordsCommand(CommandManager manager) {
        super(
                manager,
                "coords"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String s : getLanguageConfig().getStringList("COORDS_COMMAND.COORDS_MESSAGE")) {
            sendMessage(sender, s);
        }
    }
}