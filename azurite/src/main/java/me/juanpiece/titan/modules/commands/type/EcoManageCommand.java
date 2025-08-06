package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.framework.commands.extra.TabCompletion;
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
public class EcoManageCommand extends Command {

    public EcoManageCommand(CommandManager manager) {
        super(
                manager,
                "ecomanage"
        );
        this.completions.add(new TabCompletion(Arrays.asList("set", "add", "plus", "remove", "take"), 0));
        this.setPermissible("titan.ecomanage");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "ecomanager",
                "balmanager"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("ECOMANAGE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        Integer amount = getInt(args[2]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[1])
            );
            return;
        }

        if (amount == null || amount < 0) {
            sendMessage(sender, Config.NOT_VALID_NUMBER
                    .replace("%number%", args[2])
            );
            return;
        }

        switch (args[0].toLowerCase()) {
            case "take":
            case "remove":
                getInstance().getBalanceManager().takeBalance(target, amount);
                sendMessage(sender, getLanguageConfig().getString("ECOMANAGE_COMMAND.REMOVED_BAL")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%target%", target.getName())
                );
                return;

            case "plus":
            case "add":
                getInstance().getBalanceManager().giveBalance(target.getUniqueId(), amount);
                sendMessage(sender, getLanguageConfig().getString("ECOMANAGE_COMMAND.ADDED_BAL")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%target%", target.getName())
                );
                return;

            case "set":
                getInstance().getBalanceManager().setBalance(target, amount);
                sendMessage(sender, getLanguageConfig().getString("ECOMANAGE_COMMAND.SET_BAL")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%target%", target.getName())
                );
                return;
        }

        sendUsage(sender);
    }
}