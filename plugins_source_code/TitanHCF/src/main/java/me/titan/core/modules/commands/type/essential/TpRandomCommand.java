package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TpRandomCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TELEPORT_COMMAND.TPRANDOM_COMMAND.USAGE");
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
        List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (players.size() == 1) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPRANDOM_COMMAND.INSUFFICIENT_PLAYERS"));
            return;
        }
        Player online;
        Player selected;
        for (online = (Player)sender, selected = players.get(ThreadLocalRandom.current().nextInt(players.size())); online == selected; selected = players.get(ThreadLocalRandom.current().nextInt(players.size()))) {}
        Logger.info(online.getName() + " se ha teletransportado aleatoriamente a " + selected.getName() + ". (/tprandom)");
        online.teleport(selected);
        this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPRANDOM_COMMAND.TELEPORTED").replaceAll("%player%", selected.getName()));
    }
    
    public TpRandomCommand(CommandManager manager) {
        super(manager, "tprandom");
        this.setPermissible("titan.tprandom");
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("randomtp", "randtp");
    }
}
