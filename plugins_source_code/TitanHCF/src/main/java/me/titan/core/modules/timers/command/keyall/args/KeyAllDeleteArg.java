package me.titan.core.modules.timers.command.keyall.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.timers.listeners.servertimers.KeyAllTimer;
import me.titan.core.modules.timers.type.CustomTimer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class KeyAllDeleteArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_DELETE.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        CustomTimer timer = this.getInstance().getTimerManager().getCustomTimer(args[0]);
        if (timer == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_DELETE.NOT_FOUND").replaceAll("%name%", args[0]));
            return;
        }
        if (!(timer instanceof KeyAllTimer)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_DELETE.NOT_FOUND").replaceAll("%name%", args[0]));
            return;
        }
        this.getInstance().getTimerManager().getCustomTimers().remove(timer.getName());
        this.sendMessage(sender, this.getLanguageConfig().getString("KEY_ALL_COMMAND.KEY_ALL_DELETE.DELETED").replaceAll("%name%", args[0]));
    }
    
    public KeyAllDeleteArg(CommandManager manager) {
        super(manager, Arrays.asList("delete", "remove", "stop"));
    }
}
