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
public class BalanceCommand extends Command {

    public BalanceCommand(CommandManager manager) {
        super(
                manager,
                "balance"
        );
        this.setAsync(true);
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "eco",
                "bal",
                "$"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("BALANCE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int balance = getInstance().getBalanceManager().getBalance(player.getUniqueId());
                sendMessage(sender, getLanguageConfig().getString("BALANCE_COMMAND.SELF_CHECK")
                        .replace("%balance%", String.valueOf(balance))
                );
                return;
            }

            sendUsage(sender);
            return;
        }

        User target = getInstance().getUserManager().getByName(args[0]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        int targetBalance = getInstance().getBalanceManager().getBalance(target.getUniqueID());
        sendMessage(sender, getLanguageConfig().getString("BALANCE_COMMAND.TARGET_CHECK")
                .replace("%target%", target.getName())
                .replace("%balance%", String.valueOf(targetBalance))
        );
    }
}