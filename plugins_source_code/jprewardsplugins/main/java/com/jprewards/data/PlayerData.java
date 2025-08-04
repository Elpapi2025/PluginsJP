package com.jprewards.data;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private long lastClaimedDay;
    private int currentStreak;

    public PlayerData(UUID uuid, long lastClaimedDay, int currentStreak) {
        this.uuid = uuid;
        this.lastClaimedDay = lastClaimedDay;
        this.currentStreak = currentStreak;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getLastClaimedDay() {
        return lastClaimedDay;
    }

    public void setLastClaimedDay(long lastClaimedDay) {
        this.lastClaimedDay = lastClaimedDay;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }
}