package me.juanpiece.titan.modules.staff.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.staff.Staff;
import me.juanpiece.titan.modules.staff.StaffManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class HideStaffCommand extends Command {

    public HideStaffCommand(CommandManager manager) {
        super(
                manager,
                "hidestaff"
        );
        this.setPermissible("titan.hidestaff");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "hs"
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
        StaffManager staffManager = getInstance().getStaffManager();

        if (staffManager.isHideStaff(player)) {
            // Remove
            staffManager.getHideStaff().remove(player.getUniqueId());

            // Show if they are only vanished
            for (Staff staff : staffManager.getStaffMembers().values()) {
                if (staffManager.isVanished(staff.getPlayer())) {
                    player.showPlayer(staff.getPlayer());
                }
            }

            sendMessage(sender, getLanguageConfig().getString("HIDE_STAFF_COMMAND.DISABLED"));
            return;
        }

        staffManager.getHideStaff().add(player.getUniqueId());
        sendMessage(sender, getLanguageConfig().getString("HIDE_STAFF_COMMAND.ENABLED"));

        // Hide if they are only vanished
        for (Staff staff : staffManager.getStaffMembers().values()) {
            if (staffManager.isVanished(staff.getPlayer())) {
                player.hidePlayer(staff.getPlayer());
            }
        }
    }
}