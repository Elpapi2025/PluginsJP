package me.titan.core.modules.ability.command;

import me.titan.core.modules.ability.menu.AbilityListMenu;
import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AbilitiesCommand extends Command {
    @Override
    public List<String> aliases() {
        return Arrays.asList("partneritems", "abilitymenu");
    }
    
    public AbilitiesCommand(CommandManager commandManager) {
        super(commandManager, "abilities");
        this.setPermissible("titan.abilities");
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
        new AbilityListMenu(this.getInstance().getMenuManager(), player).open();
    }
    
    @Override
    public List<String> usage() {
        return null;
    }
}
