package me.titan.core.modules.board.listener;

import me.titan.core.modules.framework.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import me.titan.core.modules.board.*;

public class BoardListener extends HCFModule<BoardManager> {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getBoards().remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getManager().getBoards().put(player.getUniqueId(), new Board(this.getManager(), player));
    }
    
    public BoardListener(BoardManager manager) {
        super(manager);
    }
}
