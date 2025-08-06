package me.juanpiece.titan.modules.commands.type.essential;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ReplyCommand extends Command {

    public ReplyCommand(CommandManager manager) {
        super(
                manager,
                "reply"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "r",
                "w"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("REPLY_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        if (user.getReplied() == null || Bukkit.getPlayer(user.getReplied()) == null) {
            sendMessage(sender, getLanguageConfig().getString("REPLY_COMMAND.NO_REPLY"));
            return;
        }

        Player reply = Bukkit.getPlayer(user.getReplied());
        User targetUser = getInstance().getUserManager().getByUUID(reply.getUniqueId());

        if (user.getIgnoring().contains(reply.getUniqueId())) {
            sendMessage(sender, getLanguageConfig().getString("REPLY_COMMAND.IGNORING_PLAYER"));
            return;
        }

        if (targetUser.getIgnoring().contains(player.getUniqueId())) {
            sendMessage(sender, getLanguageConfig().getString("REPLY_COMMAND.IGNORING_TARGET"));
            return;
        }

        if (!user.isPrivateMessages()) {
            sendMessage(sender, getLanguageConfig().getString("REPLY_COMMAND.TOGGLED_PLAYER"));
            return;
        }

        if (!targetUser.isPrivateMessages()) {
            sendMessage(sender, getLanguageConfig().getString("REPLY_COMMAND.TOGGLED_TARGET"));
            return;
        }

        reply.sendMessage(getLanguageConfig().getString("MESSAGE_COMMAND.FROM_FORMAT")
                .replace("%player%", player.getName())
                .replace("%message%", message)
                .replace("%prefix%", CC.t(getInstance().getRankHook().getRankPrefix(player)))
                .replace("%suffix%", CC.t(getInstance().getRankHook().getRankSuffix(player)))
                .replace("%color%", CC.t(getInstance().getRankHook().getRankColor(player)))
        );

        player.sendMessage(getLanguageConfig().getString("MESSAGE_COMMAND.TO_FORMAT")
                .replace("%player%", reply.getName())
                .replace("%message%", message)
                .replace("%prefix%", CC.t(getInstance().getRankHook().getRankPrefix(reply)))
                .replace("%suffix%", CC.t(getInstance().getRankHook().getRankSuffix(reply)))
                .replace("%color%", CC.t(getInstance().getRankHook().getRankColor(reply)))
        );
    }
}