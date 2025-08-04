package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.users.User;
import me.titan.core.utils.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StatsCommand extends Command {
    @Override
    public List<String> aliases() {
        return Collections.singletonList("statistics");
    }
    
    public StatsCommand(CommandManager manager) {
        super(manager, "stats");
        this.setAsync(true);
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("STATS_COMMAND.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                this.sendUsage(sender);
                return;
            }
            OfflinePlayer target = CC.getPlayer(args[0]);
            User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
            if (tUser == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_TARGET")) {
                this.sendMessage(sender, s.replaceAll("%kills%", String.valueOf(tUser.getKills())).replaceAll("%deaths%", String.valueOf(tUser.getDeaths())).replaceAll("%kdr%", String.valueOf(tUser.getKDRString())).replaceAll("%killstreak%", String.valueOf(tUser.getKillstreak())));
            }
        }
        else {
            Player player = (Player)sender;
            User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
            if (args.length == 0) {
                for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_SELF")) {
                    this.sendMessage(sender, s.replaceAll("%kills%", String.valueOf(user.getKills())).replaceAll("%deaths%", String.valueOf(user.getDeaths())).replaceAll("%kdr%", String.valueOf(user.getKDRString())).replaceAll("%killstreak%", String.valueOf(user.getKillstreak())));
                }
                return;
            }
            OfflinePlayer target = CC.getPlayer(args[0]);
            User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
            if (tUser == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            for (String s : this.getLanguageConfig().getStringList("STATS_COMMAND.STATS_TARGET")) {
                this.sendMessage(sender, s.replaceAll("%kills%", String.valueOf(tUser.getKills())).replaceAll("%deaths%", String.valueOf(tUser.getDeaths())).replaceAll("%kdr%", String.valueOf(tUser.getKDRString())).replaceAll("%killstreak%", String.valueOf(tUser.getKillstreak())));
            }
        }
    }
}
