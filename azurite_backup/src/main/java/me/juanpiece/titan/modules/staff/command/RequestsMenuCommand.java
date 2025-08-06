package me.juanpiece.titan.modules.staff.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.staff.menu.RequestMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class RequestsMenuCommand extends Command {

    public RequestsMenuCommand(CommandManager manager) {
        super(
                manager,
                "requestsmenu"
        );
        this.setPermissible("titan.requestsmenu");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "requests",
                "listrequests"
        );
    }

    @Override
    public List<String> usage() {
        return null;
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

        Player player = (Player) sender;
        new RequestMenu(getInstance().getMenuManager(), player).open();
    }
}