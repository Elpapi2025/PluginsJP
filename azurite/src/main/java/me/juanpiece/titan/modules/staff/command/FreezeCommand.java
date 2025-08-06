package me.juanpiece.titan.modules.staff.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.staff.StaffManager;
import me.juanpiece.titan.modules.staff.task.FreezeMessageTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class FreezeCommand extends Command {

    private final Map<UUID, FreezeMessageTask> tasks;

    public FreezeCommand(CommandManager manager) {
        super(
                manager,
                "freeze"
        );
        this.tasks = new HashMap<>();
        this.setPermissible("titan.freeze");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "ss"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("FREEZE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        StaffManager staffManager = getInstance().getStaffManager();
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        if (target.hasPermission("titan.freeze.bypass")) {
            sendMessage(sender, getLanguageConfig().getString("FREEZE_COMMAND.CANNOT_FREEZE"));
            return;
        }

        if (staffManager.isFrozen(target)) {
            staffManager.unfreezePlayer(target);
            tasks.get(target.getUniqueId()).cancel();
            sendMessage(sender, getLanguageConfig().getString("FREEZE_COMMAND.UNFROZE_PLAYER")
                    .replace("%player%", target.getName())
            );
            return;
        }

        staffManager.freezePlayer(target);
        tasks.put(target.getUniqueId(), new FreezeMessageTask(getInstance().getStaffManager(), target));
        sendMessage(sender, getLanguageConfig().getString("FREEZE_COMMAND.FROZE_PLAYER")
                .replace("%player%", target.getName())
        );
    }
}