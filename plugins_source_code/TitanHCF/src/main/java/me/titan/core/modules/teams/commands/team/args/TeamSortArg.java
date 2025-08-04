package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.menus.TeamSortMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamSortArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        new TeamSortMenu(this.getInstance().getMenuManager(), (Player)sender).open();
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SORT.USAGE");
    }
    
    public TeamSortArg(CommandManager manager) {
        super(manager, Arrays.asList("sort", "filter"));
    }
}
