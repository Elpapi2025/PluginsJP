package me.titan.core.modules.board;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.framework.*;
import org.bukkit.entity.*;
import me.titan.core.modules.board.fastboard.*;
import java.util.*;

@Getter
@Setter
public class Board extends HCFModule<BoardManager> {
    private long titleChanger;
    private final Player player;
    private FastBoard fastBoard;
    private final BoardManager boardManager;
    private int titleIndex;

    public Board(BoardManager manager, Player player) {
        super(manager);
        this.player = player;
        this.boardManager = manager;
        this.fastBoard = new FastBoard(player);
        this.titleChanger = System.currentTimeMillis();
        this.titleIndex = 0;
    }
    
    private String getTitle() {
        List<String> lines = this.getBoardManager().getTitleChanges();
        if (this.titleIndex == lines.size()) {
            this.titleIndex = 0;
            return lines.get(0);
        }
        String s = lines.get(this.titleIndex);
        ++this.titleIndex;
        return s;
    }
    
    private void tickTitle() {
        if (this.boardManager.getScoreboardConfig().getBoolean("TITLE_CONFIG.CHANGER_ENABLED")) {
            if (this.titleChanger < System.currentTimeMillis()) {
                this.titleChanger = System.currentTimeMillis() + this.getBoardManager().getTitleChangerTicks();
                this.fastBoard.setTitle(this.getTitle());
            }
        }
        else {
            String s = this.boardManager.getAdapter().getTitle(this.player);
            if (!this.fastBoard.getTitle().equals(s)) {
                this.fastBoard.setTitle(s);
            }
        }
    }
    
    public BoardManager getBoardManager() {
        return this.boardManager;
    }

    public void update() {
        List<String> lines = this.getManager().getAdapter().getLines(this.player);
        if (lines == null || lines.isEmpty()) {
            if (!this.fastBoard.isDeleted()) {
                this.fastBoard.delete();
            }
            return;
        }
        if (this.fastBoard.isDeleted()) {
            this.fastBoard = new FastBoard(this.player);
        }
        this.tickTitle();
        this.fastBoard.setLines(this.boardManager.getAdapter().getLines(this.player));
    }

}