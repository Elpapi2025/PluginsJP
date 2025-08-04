package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.users.menu.SettingsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SettingsCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        new SettingsMenu(this.getInstance().getMenuManager(), (Player)sender).open();
    }
    
    public SettingsCommand(CommandManager manager) {
        super(manager, "setting");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SETTINGS_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("settings");
    }
}
