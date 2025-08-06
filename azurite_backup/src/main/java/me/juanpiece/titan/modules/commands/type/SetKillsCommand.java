package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.users.User;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SetKillsCommand extends Command {

    public SetKillsCommand(CommandManager manager) {
        super(
                manager,
                "setkills"
        );
        this.setPermissible("titan.setkills");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("SET_KILLS_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        User target = getInstance().getUserManager().getByName(args[0]);
        Integer amount = getInt(args[1]);

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        if (amount == null) {
            sendMessage(sender, Config.NOT_VALID_NUMBER
                    .replace("%number%", args[1])
            );
            return;
        }

        target.setKills(amount);
        target.save();
        sendMessage(sender, getLanguageConfig().getString("SET_KILLS_COMMAND.SET")
                .replace("%player%", target.getName())
                .replace("%amount%", String.valueOf(amount))
        );
    }
}
