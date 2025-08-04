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

public class HealCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("HEAL_COMMAND.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                Logger.info(sender.getName() + " has healed themselves. (/heal)");
                player.setHealth(player.getMaxHealth());
                player.sendMessage(this.getLanguageConfig().getString("HEAL_COMMAND.HEALED_SELF"));
                return;
            }
            this.sendUsage(sender);
        }
        else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            Logger.info(sender.getName() + " has healed " + target.getName() + ". (/heal)");
            target.setHealth(target.getMaxHealth());
            this.sendMessage(target, this.getLanguageConfig().getString("HEAL_COMMAND.TARGET_MESSAGE").replaceAll("%player%", sender.getName()));
            this.sendMessage(sender, this.getLanguageConfig().getString("HEAL_COMMAND.HEALED_TARGET").replaceAll("%player%", target.getName()));
        }
    }
    
    public HealCommand(CommandManager manager) {
        super(manager, "heal");
        this.setPermissible("titan.heal");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
