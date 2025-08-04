package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CraftCommand extends Command {
    @Override
    public List<String> aliases() {
        return Arrays.asList("workbench", "craftitem");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CRAFT_COMMAND.USAGE");
    }
    
    public CraftCommand(CommandManager manager) {
        super(manager, "craft");
        this.setPermissible("titan.craft");
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
        Player player = (Player)sender;
        player.openWorkbench(null, true);
    }
}
