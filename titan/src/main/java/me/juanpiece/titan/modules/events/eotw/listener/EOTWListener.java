package me.juanpiece.titan.modules.events.eotw.listener;

import me.juanpiece.titan.modules.events.eotw.EOTWManager;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.timers.event.CustomTimerExpireEvent;
import me.juanpiece.titan.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class EOTWListener extends Module<EOTWManager> {

    public EOTWListener(EOTWManager manager) {
        super(manager);
    }

    @EventHandler
    public void onExpire(CustomTimerExpireEvent e) {
        if (!e.getTimer().getName().equals("EOTW")) return;

        getManager().setActive(true);

        Tasks.execute(getManager(), () -> {
            getManager().setRaidable();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("EOTW_TIMER.COMMAND"));
        });

        for (String s : getLanguageConfig().getStringList("EOTW_TIMER.STARTED_EOTW")) {
            Bukkit.broadcastMessage(s);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (getManager().isActive() && !getManager().getWhitelisted().contains(e.getPlayer().getUniqueId()) && !e.getPlayer().hasPermission("titan.eotw.bypass")) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(String.join("\n", getLanguageConfig().getStringList("EOTW_TIMER.DEATHBANNED")));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (getManager().isActive()) {
            Player player = e.getEntity();
            player.kickPlayer(String.join("\n", getLanguageConfig().getStringList("EOTW_TIMER.DEATHBANNED")));
        }
    }
}