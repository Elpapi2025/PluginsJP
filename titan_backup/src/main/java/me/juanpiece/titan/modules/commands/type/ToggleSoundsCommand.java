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
public class ToggleSoundsCommand extends Command {

    public ToggleSoundsCommand(CommandManager manager) {
        super(
                manager,
                "togglesounds"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "sounds"
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
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        String path = "TOGGLESOUNDS_COMMAND.SOUNDS_" + (user.isPrivateMessagesSound() ? "FALSE" : "TRUE");

        user.setPrivateMessagesSound(!user.isPrivateMessagesSound());
        sendMessage(sender, getLanguageConfig().getString(path));
    }
}