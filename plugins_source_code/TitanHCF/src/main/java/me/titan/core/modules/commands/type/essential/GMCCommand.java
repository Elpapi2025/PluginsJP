package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GMCCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("GMC_COMMAND.USAGE");
    }
    
    public GMCCommand(CommandManager manager) {
        super(manager, "gmc");
        this.setPermissible("titan.gmc");
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
        Player player = (Player)sender;
        if (args.length != 1) {
            Logger.info(player.getName() + " has switched gamemodes to creative. (/gmc)");
            player.setGameMode(GameMode.CREATIVE);
            this.sendMessage(player, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", player.getGameMode().name().toLowerCase()));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        Logger.info(player.getName() + " has switched the gamemode of " + target.getName() + " to creative. (/gmc)");
        target.setGameMode(GameMode.CREATIVE);
        this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.GM_UPDATED").replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
        this.sendMessage(target, this.getLanguageConfig().getString("GAMEMODE_COMMAND.TARGET_GM_UPDATED").replaceAll("%target%", target.getName()).replaceAll("%gamemode%", target.getGameMode().name().toLowerCase()));
    }
}
