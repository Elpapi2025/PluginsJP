package me.juanpiece.titan.modules.teams.commands.team.args.co_leader;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamOpenArg extends Argument {

    private final Cooldown cooldown;

    public TeamOpenArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "open"
                )
        );
        this.cooldown = new Cooldown(manager);
    }

    @Override
    public String usage() {
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

        if (!pt.checkRole(player, Role.CO_LEADER)) {
            sendMessage(sender, Config.INSUFFICIENT_ROLE
                    .replace("%role%", Role.CO_LEADER.getName())
            );
            return;
        }

        if (cooldown.hasCooldown(player)) {
            sendMessage(sender, getLanguageConfig().getString("TEAM_COMMAND.TEAM_OPEN.ON_COOLDOWN")
                    .replace("%time%", cooldown.getRemaining(player))
            );
            return;
        }

        if (pt.isOpen()) {
            cooldown.applyCooldown(player, getConfig().getInt("TIMERS_COOLDOWN.OPEN_FAC"));
            pt.setOpen(false);
            Bukkit.broadcastMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_OPEN.CLOSED")
                    .replace("%team%", pt.getName())
            );
            return;
        }

        cooldown.applyCooldown(player, getConfig().getInt("TIMERS_COOLDOWN.OPEN_FAC"));
        pt.setOpen(true);
        Bukkit.broadcastMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_OPEN.OPENED")
                .replace("%team%", pt.getName())
        );
    }
}