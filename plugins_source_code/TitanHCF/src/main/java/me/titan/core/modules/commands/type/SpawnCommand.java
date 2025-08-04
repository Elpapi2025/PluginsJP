package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.timers.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpawnCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SPAWN_COMMAND.USAGE");
    }
    
    public SpawnCommand(CommandManager manager) {
        super(manager, "spawn");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        Location spawnLocation = Bukkit.getWorld("world").getSpawnLocation();
        TimerManager timerManager = this.getInstance().getTimerManager();
        if (sender.hasPermission("titan.spawn")) {
            if (args.length == 0) {
                player.teleport(spawnLocation);
                this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.SPAWNED"));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
                return;
            }
            target.teleport(spawnLocation);
            this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.SPAWNED_TARGET").replaceAll("%player%", target.getName()));
        }
        else {
            if (timerManager.getSotwTimer().isActive() && this.getConfig().getBoolean("SPAWN_TELEPORT_SOTW")) {
                timerManager.getSpawnTimer().applyTimer(player, 1L);
                return;
            }
            if (!this.getConfig().getBoolean("SPAWN_TIMER.ENABLED")) {
                this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.SPAWN_TIMER_DISABLED"));
                return;
            }
            if (timerManager.getCombatTimer().hasTimer(player)) {
                this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.COMBAT_TAGGED"));
                return;
            }
            if (timerManager.getSpawnTimer().hasTimer(player)) {
                this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.ALREADY_TELEPORTING"));
                return;
            }
            timerManager.getSpawnTimer().applyTimer(player);
            this.sendMessage(sender, this.getLanguageConfig().getString("SPAWN_COMMAND.WARPING").replaceAll("%seconds%", String.valueOf(timerManager.getSpawnTimer().getSeconds())));
        }
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
