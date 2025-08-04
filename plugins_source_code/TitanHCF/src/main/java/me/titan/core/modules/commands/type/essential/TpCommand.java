package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TpCommand extends Command {
    @Override
    public List<String> aliases() {
        return Collections.singletonList("teleport");
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
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        Logger.info(player.getName() + " se ha teletransportado a " + target.getName() + ". (/teleport)");
        player.teleport(target);
        player.sendMessage(this.getLanguageConfig().getString("TELEPORT_COMMAND.TP_COMMAND.TELEPORTED").replaceAll("%player%", target.getName()));
    }
    
    public TpCommand(CommandManager manager) {
        super(manager, "tp");
        this.setPermissible("titan.teleport");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TELEPORT_COMMAND.TP_COMMAND.USAGE");
    }
}
