package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BroadcastCommand extends Command {
    public BroadcastCommand(CommandManager manager) {
        super(manager, "broadcast");
        this.setPermissible("titan.broadcast");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        String text = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("BROADCAST_COMMAND.BROADCAST_FORMAT").replaceAll("%message%", CC.t(text)));
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("BROADCAST_COMMAND.USAGE");
    }
}
