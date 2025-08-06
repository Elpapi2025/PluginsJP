package me.juanpiece.titan.modules.board;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.board.fastboard.FastBoard;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Board extends Module<BoardManager> {

    private Player player;
    private FastBoard fastBoard;

    public Board(BoardManager manager, Player player) {
        super(manager);
        this.player = player;
        this.fastBoard = new FastBoard(player);
    }

    public void update() {
        List<String> lines = getManager().getAdapter().getLines(player);

        // We destroy the board if the lines are null or empty
        if (lines == null || lines.isEmpty()) {
            if (!fastBoard.isDeleted()) fastBoard.delete();
            return;
        }

        // create a new fast-board otherwise updating a deleted one will throw an exception.
        if (fastBoard.isDeleted()) {
            fastBoard = new FastBoard(player);
        }

        if (Config.SCOREBOARD_CHANGER_ENABLED) {
            fastBoard.setTitle(getManager().getTitle().getCurrent());

        } else fastBoard.setTitle(getManager().getAdapter().getTitle(player));

        fastBoard.setLines(lines);
    }
}