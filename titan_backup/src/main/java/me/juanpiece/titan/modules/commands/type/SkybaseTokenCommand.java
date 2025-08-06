package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SkybaseTokenCommand extends Command {

    public SkybaseTokenCommand(CommandManager manager) {
        super(
                manager,
                "skybasetoken"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "stoken",
                "skybase"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("SKYBASE_TOKEN_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
                sendMessage(sender, getLanguageConfig().getString("SKYBASE_TOKEN_COMMAND.SELF_CHECK")
                        .replace("%balance%", String.valueOf(user.getSkybaseTokens()))
                );
                return;
            }

            sendUsage(sender);
            return;
        }

        User user = getInstance().getUserManager().getByName(args[0]);

        if (user == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        int targetBalance = user.getSkybaseTokens();
        sendMessage(sender, getLanguageConfig().getString("SKYBASE_TOKEN_COMMAND.TARGET_CHECK")
                .replace("%target%", user.getName())
                .replace("%balance%", String.valueOf(targetBalance))
        );
    }
}
