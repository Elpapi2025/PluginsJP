package me.juanpiece.titan.modules.board;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.board.adapter.TitanBoard;
import me.juanpiece.titan.modules.board.extra.AnimatedString;
import me.juanpiece.titan.modules.board.listener.BoardListener;
import me.juanpiece.titan.modules.board.task.BoardTask;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.utils.NameThreadFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class BoardManager extends Manager {

    private final Map<UUID, Board> boards;
    private final ScheduledExecutorService executor;

    private BoardAdapter adapter;
    private AnimatedString title;
    private AnimatedString footer;

    public BoardManager(HCF instance) {
        super(instance);

        this.boards = new ConcurrentHashMap<>();
        this.adapter = new TitanBoard(this);

        this.title = new AnimatedString(this, getStringList("TITLE_CONFIG.CHANGES"), getLong("TITLE_CONFIG.CHANGER_TICKS"));
        this.footer = new AnimatedString(this, getStringList("FOOTER_CONFIG.CHANGES"), getLong("FOOTER_CONFIG.CHANGER_TICKS"));

        this.executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("Titan - BoardThread"));
        this.executor.scheduleAtFixedRate(new BoardTask(this), 0L, 100L, TimeUnit.MILLISECONDS);

        new BoardListener(this);
    }

    @Override
    public void disable() {
        executor.shutdown();
    }

    @Override
    public void reload() {
        this.adapter = new TitanBoard(this); // re-fetch the values
        this.title = new AnimatedString(this, getStringList("TITLE_CONFIG.CHANGES"), getLong("TITLE_CONFIG.CHANGER_TICKS"));
        this.footer = new AnimatedString(this, getStringList("FOOTER_CONFIG.CHANGES"), getLong("FOOTER_CONFIG.CHANGER_TICKS"));
    }

    public List<String> getStringList(String path) {
        return getScoreboardConfig().getStringList(path);
    }

    public long getLong(String path) {
        return getScoreboardConfig().getLong(path);
    }
}