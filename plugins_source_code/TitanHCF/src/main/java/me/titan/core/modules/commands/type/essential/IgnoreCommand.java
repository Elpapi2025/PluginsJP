package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class IgnoreCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        Player target = Bukkit.getPlayer(args[0]);
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (user.getIgnoring().contains(target.getUniqueId())) {
            user.getIgnoring().remove(target.getUniqueId());
            this.sendMessage(sender, this.getLanguageConfig().getString("IGNORE_COMMAND.IGNORE_REMOVE").replaceAll("%player%", target.getName()));
            return;
        }
        user.getIgnoring().add(target.getUniqueId());
        this.sendMessage(sender, this.getLanguageConfig().getString("IGNORE_COMMAND.IGNORE_ADD").replaceAll("%player%", target.getName()));
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    public IgnoreCommand(CommandManager manager) {
        super(manager, "ignore");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("IGNORE_COMMAND.USAGE");
    }
}
