package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.listeners.playertimers.LogoutTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LogoutCommand extends Command {
    
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
        Player player = (Player)sender;
        LogoutTimer logoutTimer = this.getInstance().getTimerManager().getLogoutTimer();
        if (logoutTimer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("LOGOUT_COMMAND.ALREADY_ACTIVE"));
            return;
        }
        if (this.getConfig().getBoolean("COMBAT_TIMER.LOGOUT_COMMAND") && this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("LOGOUT_COMMAND.COMBAT_TAGGED"));
            return;
        }
        logoutTimer.applyTimer(player);
        this.sendMessage(player, this.getLanguageConfig().getString("LOGOUT_COMMAND.STARTED_LOGOUT").replaceAll("%seconds%", String.valueOf(logoutTimer.getSeconds())));
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("LOGOUT_COMMAND.USAGE");
    }
    
    public LogoutCommand(CommandManager commandManager) {
        super(commandManager, "logout");
    }
}
