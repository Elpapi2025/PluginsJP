package me.juanpiece.titan.modules.ability.command;

import me.juanpiece.titan.modules.ability.menu.AbilityListMenu;
import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AbilitiesCommand extends Command {

    public AbilitiesCommand(CommandManager manager) {
        super(
                manager,
                "abilities"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "partneritems",
                "abilitymenu"
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

        Player player = (Player) sender;
        new AbilityListMenu(getInstance().getMenuManager(), player).open();
    }
}