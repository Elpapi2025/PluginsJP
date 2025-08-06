package me.juanpiece.titan.modules.commands.type.essential;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class StackCommand extends Command {

    public StackCommand(CommandManager manager) {
        super(
                manager,
                "stack"
        );
        this.setPermissible("titan.stack");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "more",
                "moreitems"
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
        ItemStack toStack = getManager().getItemInHand(player);

        if (toStack == null || toStack.getType() == Material.AIR) {
            sendMessage(sender, getLanguageConfig().getString("STACK_COMMAND.NO_ITEM"));
            return;
        }

        toStack.setAmount(64);
        getManager().setItemInHand(player, toStack);
        player.updateInventory();
        sendMessage(sender, getLanguageConfig().getString("STACK_COMMAND.STACKED"));
    }
}