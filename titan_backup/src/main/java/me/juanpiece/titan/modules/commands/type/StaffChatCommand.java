package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.settings.TeamChatSetting;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class StaffChatCommand extends Command {

    public StaffChatCommand(CommandManager manager) {
        super(
                manager,
                "staffchat"
        );
        this.setPermissible("titan.staffchat");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("sc", "chatstaff");
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
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());

        if (user.getTeamChatSetting() == TeamChatSetting.STAFF) {
            user.setTeamChatSetting(TeamChatSetting.PUBLIC);
            user.save();
            sendMessage(sender, getLanguageConfig().getString("STAFF_CHAT_COMMAND.TOGGLED_OFF"));
            return;
        }

        user.setTeamChatSetting(TeamChatSetting.STAFF);
        user.save();
        sendMessage(sender, getLanguageConfig().getString("STAFF_CHAT_COMMAND.TOGGLED_ON"));
    }
}