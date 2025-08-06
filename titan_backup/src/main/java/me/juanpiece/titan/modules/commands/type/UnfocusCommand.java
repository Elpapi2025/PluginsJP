package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class UnfocusCommand extends Command {

    public UnfocusCommand(CommandManager manager) {
        super(
                manager,
                "unfocus"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("UNFOCUS_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        if (target == null) {
            sendMessage(sender, Config.PLAYER_NOT_FOUND
                    .replace("%player%", args[0])
            );
            return;
        }

        if (!pt.getSingularFocus().contains(target.getUniqueId())) {
            sendMessage(sender, getLanguageConfig().getString("UNFOCUS_COMMAND.NOT_FOCUSED"));
            return;
        }

        pt.getSingularFocus().remove(target.getUniqueId());
        pt.broadcast(getLanguageConfig().getString("UNFOCUS_COMMAND.UNFOCUSED")
                .replace("%player%", player.getName())
                .replace("%target%", target.getName())
        );
    }
}