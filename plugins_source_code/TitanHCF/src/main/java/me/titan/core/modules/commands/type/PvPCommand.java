package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import me.titan.core.modules.timers.listeners.playertimers.InvincibilityTimer;
import me.titan.core.modules.timers.listeners.playertimers.PvPTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PvPCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("PVPTIMER_COMMAND.USAGE");
    }
    
    public PvPCommand(CommandManager manager) {
        super(manager, "pvp");
        this.completions.add(new TabCompletion(Arrays.asList("enable", "clear", "time"), 0));
        this.completions.add(new TabCompletion(Arrays.asList("enablefor", "clearfor"), 0, "titan.pvptimer.admin"));
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
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        InvincibilityTimer inviTimer = this.getInstance().getTimerManager().getInvincibilityTimer();
        PvPTimer pvpTimer = this.getInstance().getTimerManager().getPvpTimer();
        switch (args[0].toLowerCase()) {
            case "clear":
            case "enable": {
                if (!inviTimer.hasTimer(player) && !pvpTimer.hasTimer(player)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.NO_TIMERS"));
                    return;
                }
                if (inviTimer.hasTimer(player)) {
                    inviTimer.removeTimer(player);
                }
                if (pvpTimer.hasTimer(player)) {
                    pvpTimer.removeTimer(player);
                }
                this.getInstance().getNametagManager().update();
                this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.ENABLED"));
                return;
            }
            case "time": {
                if (!inviTimer.hasTimer(player) && !pvpTimer.hasTimer(player)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.NO_TIMERS"));
                    return;
                }
                if (inviTimer.hasTimer(player)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.TIME_FORMAT").replaceAll("%remaining%", inviTimer.getRemainingString(player)));
                    return;
                }
                if (pvpTimer.hasTimer(player)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.TIME_FORMAT").replaceAll("%remaining%", pvpTimer.getRemainingString(player)));
                    return;
                }
                return;
            }
            case "enablefor":
            case "clearfor": {
                if (!sender.hasPermission("titan.pvptimer.admin")) {
                    this.sendMessage(sender, Config.INSUFFICIENT_PERM);
                    return;
                }
                if (args.length < 2) {
                    this.sendUsage(sender);
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
                    return;
                }
                if (!inviTimer.hasTimer(target) && !pvpTimer.hasTimer(target)) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.ENABLE_FOR_NO_TIMER").replaceAll("%target%", target.getName()));
                    return;
                }
                if (inviTimer.hasTimer(target)) {
                    inviTimer.removeTimer(target);
                }
                if (pvpTimer.hasTimer(target)) {
                    pvpTimer.removeTimer(target);
                }
                this.getInstance().getNametagManager().update();
                this.sendMessage(sender, this.getLanguageConfig().getString("PVPTIMER_COMMAND.ENABLED_FOR").replaceAll("%target%", target.getName()));
                return;
            }
        }
        this.sendUsage(sender);
    }
}
