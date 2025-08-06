package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.menu.RestoreMenu;
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
public class RestoreCommand extends Command {

    public RestoreCommand(CommandManager manager) {
        super(
                manager,
                "restore"
        );
        this.setPermissible("titan.restore");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "lastinv",
                "restoreinv",
                "inventoryrestore",
                "invrestore",
                "restores",
                "lastinvs",
                "inventories",
                "rollback"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("RESTORE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        new RestoreMenu(getInstance().getMenuManager(), player, target).open();
    }
}