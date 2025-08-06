package me.juanpiece.titan.modules.reclaims.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.reclaims.Reclaim;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class DailyCommand extends Command {

    private final long day;

    public DailyCommand(CommandManager manager) {
        super(
                manager,
                "daily"
        );
        this.day = TimeUnit.DAYS.toMillis(1);
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
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
        Reclaim daily = getInstance().getReclaimManager().getDaily(player);
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());

        if (daily == null) {
            sendMessage(sender, getLanguageConfig().getString("DAILY_COMMAND.NO_DAILY"));
            return;
        }

        if (user.getDailyCooldown() > System.currentTimeMillis()) {
            sendMessage(sender, getLanguageConfig().getString("DAILY_COMMAND.DAILY_COOLDOWN")
                    .replace("%time%", Formatter.getRemaining(user.getDailyCooldown() - System.currentTimeMillis(), false))
            );
            return;
        }

        for (String command : daily.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("%player%", player.getName())
            );
        }

        user.setDailyCooldown(System.currentTimeMillis() + day);
        user.save();
    }
}