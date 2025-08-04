package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import me.titan.core.modules.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LivesManageCommand extends Command {

    public LivesManageCommand(CommandManager manager) {
        super(manager, "livesmanage");
        this.setPermissible("titan.livesmanage");
        this.completions.add(new TabCompletion(Arrays.asList("set", "add", "plus", "remove", "take"), 0));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 3) {
            this.sendUsage(sender);
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        Integer lives = this.getInt(args[2]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
            return;
        }
        if (lives == null || lives <= 0) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        User tUser = this.getInstance().getUserManager().getByUUID(target.getUniqueId());
        switch (args[0].toLowerCase()) {
            case "take":
            case "remove": {
                tUser.setLives(tUser.getLives() - lives);
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVESMANAGE_COMMAND.REMOVED_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%target%", target.getName()));
                return;
            }
            case "plus":
            case "add": {
                tUser.setLives(tUser.getLives() + lives);
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVESMANAGE_COMMAND.ADDED_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%target%", target.getName()));
                return;
            }
            case "set": {
                tUser.setLives(lives);
                this.sendMessage(sender, this.getLanguageConfig().getString("LIVESMANAGE_COMMAND.SET_LIVES").replaceAll("%amount%", String.valueOf(lives)).replaceAll("%target%", target.getName()));
                return;
            }
        }
        this.sendUsage(sender);
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("livemanage");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("LIVESMANAGE_COMMAND.USAGE");
    }
}
