package me.titan.core.modules.staff.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.staff.StaffManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class VanishCommand extends Command {
    public VanishCommand(CommandManager manager) {
        super(manager, "vanish");
        this.setPermissible("titan.vanish");
    }
    
    @Override
    public List<String> usage() {
        return null;
    }
    
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
        if (manager.isVanished(player)) {
            manager.disableVanish(player);
            player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.DISABLED_VANISH"));
            return;
        }
        manager.enableVanish(player);
        player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.ENABLED_VANISH"));
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("v", "vanished");
    }
}