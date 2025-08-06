package me.juanpiece.titan.modules.commands.type.essential;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BroadcastCommand extends Command {

    public BroadcastCommand(CommandManager manager) {
        super(
                manager,
                "broadcast"
        );
        this.setPermissible("titan.broadcast");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "saymessage",
                "bc"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("BROADCAST_COMMAND.USAGE");
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

        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        Bukkit.broadcastMessage(getLanguageConfig().getString("BROADCAST_COMMAND.BROADCAST_FORMAT")
                .replace("%message%", CC.t(message))
        );
    }
}