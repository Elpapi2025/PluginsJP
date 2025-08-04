package me.titan.core.modules.reclaims.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.reclaims.Reclaim;
import me.titan.core.modules.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReclaimCommand extends Command {
    @Override
    public List<String> usage() {
        return null;
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("claim");
    }
    
    public ReclaimCommand(CommandManager manager) {
        super(manager, "reclaim");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        Reclaim reclaim = this.getInstance().getReclaimManager().getReclaim(player);
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (reclaim == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RECLAIM_COMMAND.NO_RECLAIM"));
            return;
        }
        if (user.isReclaimed()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RECLAIM_COMMAND.ALREADY_RECLAIMED"));
            return;
        }
        for(String s : reclaim.getCommands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", player.getName()));
        }
        user.setReclaimed(true);
        user.save();
    }
}
