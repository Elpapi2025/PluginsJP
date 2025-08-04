package me.titan.core.modules.timers.command.customtimer.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.timers.type.CustomTimer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CTimerListArg extends Argument {
    public CTimerListArg(CommandManager manager) {
        super(manager, Arrays.asList("names", "list"));
    }
    
    @Override
    public String usage() {
        return null;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String s : this.getLanguageConfig().getStringList("CTIMER_COMMAND.CTIMER_LIST.TIMERS_LIST")) {
            if (!s.equalsIgnoreCase("%timers%")) {
                this.sendMessage(sender, s);
            }
            else {
                for (CustomTimer timer : this.getInstance().getTimerManager().getCustomTimers().values()) {
                    this.sendMessage(sender, this.getLanguageConfig().getString("CTIMER_COMMAND.CTIMER_LIST.TIMERS_FORMAT").replaceAll("%name%", timer.getName()).replaceAll("%remaining%", timer.getRemainingString()).replaceAll("%displayName%", timer.getDisplayName()));
                }
            }
        }
    }
}
