package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TLCommand extends Command {

    public TLCommand(CommandManager manager) {
        super(
                manager,
                "tl"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "telllocation",
                "tellloc"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        Player player = (Player) sender;
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

        if (pt == null) {
            sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }

        pt.broadcast(getLanguageConfig().getString("TELL_LOC_COMMAND.FORMAT")
                .replace("%player%", player.getName())
                .replace("%location%", Utils.formatLocation(player.getLocation()))
        );
    }
}