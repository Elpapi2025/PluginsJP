package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import me.titan.core.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("WORLD_COMMAND.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("WORLD_COMMAND.WORLD_NOT_FOUND").replaceAll("%name%", args[0]));
            return;
        }
        Logger.info(player.getName() + " has changed worlds to " + world.getName() + ". (/world)");
        World wd = player.getLocation().clone().getWorld();
        player.teleport(world.getSpawnLocation());
        Bukkit.getPluginManager().callEvent(new PlayerChangedWorldEvent(player, wd));
        this.sendMessage(sender, this.getLanguageConfig().getString("WORLD_COMMAND.WORLD_CHANGED").replaceAll("%name%", args[0]));
    }
    
    public WorldCommand(CommandManager manager) {
        super(manager, "world");
        this.setPermissible("titan.world");
        this.completions.add(new TabCompletion(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()), 0));
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
}
