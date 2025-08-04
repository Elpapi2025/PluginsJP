package me.titan.core.modules.board.thread;

import me.titan.core.modules.board.Board;
import me.titan.core.modules.board.BoardManager;
import me.titan.core.modules.timers.type.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BoardThread extends Thread {
    private final BoardManager manager;
    
    @Override
    public void run() {
        while (true) {
            for (PlayerTimer timer : this.manager.getInstance().getTimerManager().getPlayerTimers().values()) {
                try {
                    timer.tick();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                Board board = this.manager.getBoards().get(player.getUniqueId());
                if (board == null) {
                    continue;
                }
                try {
                    board.update();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public BoardThread(BoardManager manager) {
        super("Titan - BoardThread");
        this.manager = manager;
        this.start();
    }
}
