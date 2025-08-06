package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.Formatter;
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
public class PlaytimeCommand extends Command {

    private final boolean pausePlaytimeStaff;

    public PlaytimeCommand(CommandManager manager) {
        super(
                manager,
                "playtime"
        );
        this.pausePlaytimeStaff = getConfig().getBoolean("STAFF_MODE.PAUSE_PLAYTIME_STAFF");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "playertime",
                "played"
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

        if (args.length == 0) {
            Player player = (Player) sender;
            User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
            if (!pausePlaytimeStaff || !getInstance().getStaffManager().isStaffEnabled(player)) user.updatePlaytime();
            sendMessage(sender, getLanguageConfig().getString("PLAYTIME_COMMAND.SELF_CHECK")
                    .replace("%playtime%", Formatter.formatDetailed(user.getPlaytime()))
            );
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        User user = getInstance().getUserManager().getByUUID(target.getUniqueId());
        if (!pausePlaytimeStaff || !getInstance().getStaffManager().isStaffEnabled(target)) user.updatePlaytime();

        sendMessage(sender, getLanguageConfig().getString("PLAYTIME_COMMAND.TARGET_CHECK")
                .replace("%target%", target.getName())
                .replace("%playtime%", Formatter.formatDetailed(user.getPlaytime()))
        );
    }
}