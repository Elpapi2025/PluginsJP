package me.keano.azurite.modules.timers;

import lombok.Getter;
import lombok.Setter;
import me.keano.azurite.modules.framework.Module;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Timer extends Module<TimerManager> {

    protected String name;
    protected String scoreboardPath;
    protected String scoreboard;
    protected int seconds;

    public Timer(TimerManager manager, String name, String scoreboardPath, int seconds) {
        super(manager);
        this.name = name;
        this.scoreboardPath = scoreboardPath;
        this.seconds = seconds;
        this.fetchScoreboard();
    }

    public void fetchScoreboard() {
        scoreboard = (scoreboardPath.isEmpty() ? "" : (getScoreboardConfig().getString(scoreboardPath).isEmpty() ?
                null :
                getScoreboardConfig().getString(scoreboardPath))
        );
    }

    public void reload() {
    }
}