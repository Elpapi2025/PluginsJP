package me.titan.core.modules.spawners.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.spawners.Spawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnerCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SPAWNER_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    public SpawnerCommand(CommandManager manager) {
        super(manager, "spawner");
        this.setPermissible("titan.spawner");
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getSpawnerManager().getSpawners().keySet().stream().map(Enum::toString).collect(Collectors.toList());
        }
        return null;
    }

    public void execute(CommandSender sender, String[] args) {
        EntityType type;
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        Player player = (Player)sender;
        try {
            type = EntityType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ignored) {
            type = null;
        }
        if (type == null || getInstance().getSpawnerManager().getSpawners().get(type) == null) {
            sendMessage(sender, getLanguageConfig().getString("SPAWNER_COMMAND.SPAWNER_NOT_FOUND").replaceAll("%type%", args[0]));
            return;
        }
        Spawner spawner = getInstance().getSpawnerManager().getSpawners().get(type);
        player.getInventory().addItem(spawner.getItemStack());
        player.updateInventory();
        player.sendMessage(getLanguageConfig().getString("SPAWNER_COMMAND.SPAWNER_GAINED").replaceAll("%type%", spawner.getName()));
    }
}
