package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.framework.commands.extra.TabCompletion;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ManageSkybaseTokenCommand extends Command {

    public ManageSkybaseTokenCommand(CommandManager manager) {
        super(
                manager,
                "manageskybasetoken"
        );
        this.setPermissible("titan.manageskybasetoken");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("mskybasetoken");
    }

    public List<TabCompletion> completions() {
        return Arrays.asList(
                new TabCompletion(
                        Arrays.asList("set", "add", "plus", "remove", "take"),
                        1
                )
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("SKYBASE_TOKEN_MANAGE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        User targetUser = getInstance().getUserManager().getByName(args[1]);

        if (targetUser == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND.replace("%player%", args[1]));
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sendMessage(sender, getLanguageConfig().getString("INVALID_NUMBER").replace("%number%", args[2]));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "remove":
            case "take":
                targetUser.setSkybaseTokens(targetUser.getSkybaseTokens() - amount);
                targetUser.save();
                sendMessage(sender, getLanguageConfig().getString("SKYBASE_TOKEN_MANAGE_COMMAND.REMOVED_BAL")
                        .replace("%target%", targetUser.getName())
                        .replace("%amount%", String.valueOf(amount))
                );
                break;
            case "add":
            case "plus":
                targetUser.setSkybaseTokens(targetUser.getSkybaseTokens() + amount);
                targetUser.save();
                sendMessage(sender, getLanguageConfig().getString("SKYBASE_TOKEN_MANAGE_COMMAND.ADDED_BAL")
                        .replace("%target%", targetUser.getName())
                        .replace("%amount%", String.valueOf(amount))
                );
                break;
            case "set":
                targetUser.setSkybaseTokens(amount);
                targetUser.save();
                sendMessage(sender, getLanguageConfig().getString("SKYBASE_TOKEN_MANAGE_COMMAND.SET_BAL")
                        .replace("%target%", targetUser.getName())
                        .replace("%amount%", String.valueOf(amount))
                );
                break;
            default:
                sendUsage(sender);
                break;
        }
    }
}
