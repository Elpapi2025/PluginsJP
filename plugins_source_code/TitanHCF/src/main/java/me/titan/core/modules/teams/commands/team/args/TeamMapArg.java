package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamMapArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (user.isClaimsShown()) {
            user.setClaimsShown(false);
            this.getInstance().getWallManager().clearTeamMap(player);
            this.sendMessage(player, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_MAP.MAP_HIDDEN"));
            return;
        }
        user.setClaimsShown(true);
        this.getInstance().getWallManager().sendTeamMap(player);
    }
    
    public TeamMapArg(CommandManager manager) {
        super(manager, Arrays.asList("map", "claims"));
    }
}
