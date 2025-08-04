package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PlaytimeCommand extends Command {
    public PlaytimeCommand(CommandManager manager) {
        super(manager, "playtime");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("PLAYTIME_COMMAND.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            Player player = (Player)sender;
            this.sendMessage(sender, this.getLanguageConfig().getString("PLAYTIME_COMMAND.SELF_CHECK").replaceAll("%playtime%", Formatter.formatDetailed(this.calculatePlaytime(player))));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        this.sendMessage(sender, this.getLanguageConfig().getString("PLAYTIME_COMMAND.TARGET_CHECK").replaceAll("%target%", target.getName()).replaceAll("%playtime%", Formatter.formatDetailed(this.calculatePlaytime(target))));
    }
    
    private long calculatePlaytime(Player player) {
        if (this.getInstance().getVersionManager().isVer16()) {
            return player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE")) * 50L;
        }
        return player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK")) * 50L;
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("playertime", "played");
    }
}
