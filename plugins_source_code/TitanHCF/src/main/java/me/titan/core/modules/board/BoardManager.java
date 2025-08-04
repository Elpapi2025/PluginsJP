package me.titan.core.modules.board;

import lombok.Getter;
import me.titan.core.modules.framework.*;
import java.util.*;
import me.titan.core.*;
import java.util.concurrent.*;
import me.titan.core.modules.board.adapter.*;
import me.titan.core.modules.board.listener.*;
import me.titan.core.modules.board.thread.*;

@Getter
public class BoardManager extends Manager {
    private final long titleChangerTicks;
    private final BoardAdapter adapter;
    private final List<String> titleChanges;
    private final Map<UUID, Board> boards;
    
    public BoardManager(HCF plugin) {
        super(plugin);
        this.boards = new ConcurrentHashMap<>();
        this.adapter = new TitanBoard(this);
        this.titleChangerTicks = this.getScoreboardConfig().getLong("TITLE_CONFIG.CHANGER_TICKS");
        this.titleChanges = this.getScoreboardConfig().getStringList("TITLE_CONFIG.CHANGES");
        new BoardListener(this);
        new BoardThread(this);
    }
}
