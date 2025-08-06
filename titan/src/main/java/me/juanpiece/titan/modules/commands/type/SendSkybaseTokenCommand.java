package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SendSkybaseTokenCommand extends Command {

    public SendSkybaseTokenCommand(CommandManager manager) {
        super(
                manager,
                "sendskybasetoken"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "payskybasetoken"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("SEND_SKYBASE_TOKEN_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());

        User userTarget = getInstance().getUserManager().getByName(args[0]);

        if (userTarget == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND.replace("%player%", args[0]));
            return;
        }

        if (userTarget.getName().equalsIgnoreCase(user.getName())) {
            sendMessage(sender, getLanguageConfig().getString("SEND_SKYBASE_TOKEN_COMMAND.CANT_PAY_SELF"));
            return;
        }

        int paying;

        try {
            paying = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendMessage(sender, getLanguageConfig().getString("INVALID_NUMBER").replace("%number%", args[1]));
            return;
        }

        if (paying <= 0) {
            sendMessage(sender, getLanguageConfig().getString("SEND_SKYBASE_TOKEN_COMMAND.MUST_BE_POSITIVE"));
            return;
        }

        if (user.getSkybaseTokens() < paying) {
            sendMessage(sender, getLanguageConfig().getString("SEND_SKYBASE_TOKEN_COMMAND.INSUFFICIENT_BAL"));
            return;
        }

        user.setSkybaseTokens(user.getSkybaseTokens() - paying);
        user.save();

        userTarget.setSkybaseTokens(userTarget.getSkybaseTokens() + paying);
        userTarget.save();

        sendMessage(player, getLanguageConfig().getString("SEND_SKYBASE_TOKEN_COMMAND.PAID").replace("%target%", userTarget.getName()).replace("%amount%", String.valueOf(paying)));
        Player target = getInstance().getServer().getPlayer(userTarget.getUniqueID());
        if (target != null) {
            sendMessage(target, getLanguageConfig().getString("SEND_SKYBASE_TOKEN_COMMAND.RECEIVED").replace("%player%", player.getName()).replace("%amount%", String.valueOf(paying)));
        }
    }
}
