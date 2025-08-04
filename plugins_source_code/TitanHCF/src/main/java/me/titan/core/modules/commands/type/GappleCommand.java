package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.listeners.playertimers.GappleTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GappleCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        GappleTimer timer = this.getInstance().getTimerManager().getGappleTimer();
        if (!timer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("GAPPLE_COMMAND.NO_TIMER"));
            return;
        }
        this.sendMessage(sender, this.getLanguageConfig().getString("GAPPLE_COMMAND.FORMAT").replaceAll("%remaining%", timer.getRemainingString(player)));
    }
    
    public GappleCommand(CommandManager manager) {
        super(manager, "gapple");
    }
    
    @Override
    public List<String> usage() {
        return null;
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("gopple");
    }
}
