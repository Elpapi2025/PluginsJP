package me.titan.core.modules.staff.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.staff.StaffManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StaffBuildCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        Player player = (Player)sender;
        StaffManager manager = this.getInstance().getStaffManager();
        if (!manager.isStaffEnabled(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.NOT_IN_STAFF"));
            return;
        }
        if (manager.isStaffBuild(player)) {
            manager.getStaffBuild().remove(player.getUniqueId());
            this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.BUILD_DISABLED"));
            return;
        }
        manager.getStaffBuild().add(player.getUniqueId());
        this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.BUILD_ENABLED"));
    }
    
    public StaffBuildCommand(CommandManager manager) {
        super(manager, "staffbuild");
        this.setPermissible("titan.staffbuild");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("STAFF_BUILD_COMMAND.USAGE");
    }
}