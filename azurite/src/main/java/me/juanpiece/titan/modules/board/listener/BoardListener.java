package me.juanpiece.titan.modules.board.listener;

import me.juanpiece.titan.modules.board.Board;
import me.juanpiece.titan.modules.board.BoardManager;
import me.juanpiece.titan.modules.framework.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardListener extends Module<BoardManager> {

    public BoardListener(BoardManager manager) {
        super(manager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        getManager().getBoards().put(player.getUniqueId(), new Board(getManager(), player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        getManager().getBoards().remove(player.getUniqueId());
    }
}