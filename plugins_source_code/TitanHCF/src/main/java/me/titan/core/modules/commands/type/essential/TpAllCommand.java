package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpAllCommand extends Command {
    public TpAllCommand(CommandManager manager) {
        super(manager, "tpall");
        this.setPermissible("titan.tpall");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
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
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() == 1) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPALL_COMMAND.INSUFFICIENT_PLAYERS"));
            return;
        }
        Player player = (Player)sender;
        String msg = this.getLanguageConfig().getString("TELEPORT_COMMAND.TPALL_COMMAND.TELEPORTED").replaceAll("%player%", player.getName());
        for (Player online : players) {
            if (online == player) {
                continue;
            }
            online.teleport(player);
            online.sendMessage(msg);
        }
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TELEPORT_COMMAND.TPALL_COMMAND.USAGE");
    }
}
