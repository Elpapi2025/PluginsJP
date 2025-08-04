package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ToggleSoundsCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("TOGGLESOUNDS_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("sounds");
    }
    
    public ToggleSoundsCommand(CommandManager manager) {
        super(manager, "togglesounds");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        String path = "TOGGLESOUNDS_COMMAND.SOUNDS_" + (user.isPrivateMessagesSound() ? "FALSE" : "TRUE");
        user.setPrivateMessagesSound(!user.isPrivateMessagesSound());
        this.sendMessage(sender, this.getLanguageConfig().getString(path));
    }
}
