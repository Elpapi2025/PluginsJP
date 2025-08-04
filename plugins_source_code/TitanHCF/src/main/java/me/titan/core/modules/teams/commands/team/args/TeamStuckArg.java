package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.timers.listeners.playertimers.StuckTimer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamStuckArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_STUCK.USAGE");
    }
    
    public TeamStuckArg(CommandManager manager) {
        super(manager, Collections.singletonList("stuck"));
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        StuckTimer timer = this.getInstance().getTimerManager().getStuckTimer();
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_STUCK.CANNOT_STUCK"));
            return;
        }
        if (timer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_STUCK.ALREADY_STUCKING"));
            return;
        }
        if (!this.getConfig().getBoolean("COMBAT_TIMER.STUCK_TELEPORT") && this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_STUCK.COMBAT_TAGGED"));
            return;
        }
        timer.applyTimer(player);
        this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_STUCK.STARTED_STUCK").replaceAll("%seconds%", String.valueOf(timer.getSeconds())).replaceAll("%blocks%", String.valueOf(timer.getMaxMoveBlocks())));
    }
}
