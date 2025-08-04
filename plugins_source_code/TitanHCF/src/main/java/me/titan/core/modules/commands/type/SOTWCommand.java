package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import me.titan.core.modules.timers.listeners.servertimers.SOTWTimer;
import me.titan.core.utils.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SOTWCommand extends Command {
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    @Override
    public void sendUsage(CommandSender sender) {
        if (sender.hasPermission("titan.sotw")) {
            for (String s : this.getLanguageConfig().getStringList("SOTW_COMMAND.USAGE_ADMIN")) {
                sender.sendMessage(s);
            }
        }
        else {
            for (String s : this.getLanguageConfig().getStringList("SOTW_COMMAND.USAGE_DEFAULT")) {
                sender.sendMessage(s);
            }
        }
    }
    
    public SOTWCommand(CommandManager manager) {
        super(manager, "SOTW");
        this.completions.add(new TabCompletion(Collections.singletonList("enable"), 0));
        this.completions.add(new TabCompletion(Arrays.asList("start", "end", "stop", "extend"), 0, "titan.sotw"));
    }
    
    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        SOTWTimer timer = this.getInstance().getTimerManager().getSotwTimer();
        switch (args[0].toLowerCase()) {
            case "start": {
                if (!sender.hasPermission("titan.sotw")) {
                    this.sendMessage(sender, Config.INSUFFICIENT_PERM);
                    return;
                }
                if (args.length < 2) {
                    this.sendUsage(sender);
                    return;
                }
                Long time = Formatter.parse(args[1]);
                if (time == null) {
                    this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
                    return;
                }
                if (timer.isActive()) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_START.ALREADY_ACTIVE"));
                    return;
                }
                timer.startSOTW(time);
                this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_START.STARTED"));
                return;
            }
            case "extend": {
                if (!sender.hasPermission("titan.sotw")) {
                    this.sendMessage(sender, Config.INSUFFICIENT_PERM);
                    return;
                }
                if (args.length < 2) {
                    this.sendUsage(sender);
                    return;
                }
                Long time = Formatter.parse(args[1]);
                if (time == null) {
                    this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
                    return;
                }
                if (!timer.isActive()) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.NOT_ACTIVE"));
                    return;
                }
                timer.extendSOTW(time);
                this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_EXTEND.EXTENDED"));
                return;
            }
            case "stop":
            case "end": {
                if (!sender.hasPermission("titan.sotw")) {
                    this.sendMessage(sender, Config.INSUFFICIENT_PERM);
                    return;
                }
                if (!timer.isActive()) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.NOT_ACTIVE"));
                    return;
                }
                timer.endSOTW();
                timer.getEnabled().clear();
                this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_END.ENDED"));
                return;
            }
            case "enable": {
                if (!(sender instanceof Player)) {
                    this.sendMessage(sender, Config.PLAYER_ONLY);
                    return;
                }
                if (!timer.isActive()) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.NOT_ACTIVE"));
                    return;
                }
                Player player = (Player)sender;
                if (timer.getEnabled().contains(player.getUniqueId())) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_ENABLE.ALREADY_ENABLED"));
                    return;
                }
                timer.getEnabled().add(player.getUniqueId());
                this.sendMessage(sender, this.getLanguageConfig().getString("SOTW_COMMAND.SOTW_ENABLE.ENABLED"));
                this.getInstance().getNametagManager().update();
                return;
            }
        }
        this.sendUsage(sender);
    }
}
