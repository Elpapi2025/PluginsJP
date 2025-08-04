package me.titan.core.utils.extra;

import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.utils.Formatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamCooldown {

    private final Map<UUID, Long> cooldowns;

    public void removeCooldown(PlayerTeam playerteam) {
        this.cooldowns.remove(playerteam.getUniqueID());
    }

    public void applyCooldown(PlayerTeam playerteam, int time) {
        this.cooldowns.put(playerteam.getUniqueID(), System.currentTimeMillis() + time * 1000L);
    }
    
    public TeamCooldown() {
        this.cooldowns = new HashMap<>();
    }
    
    public boolean hasCooldown(PlayerTeam playerteam) {
        return this.cooldowns.containsKey(playerteam.getUniqueID()) && this.cooldowns.get(playerteam.getUniqueID()) >= System.currentTimeMillis();
    }
    
    public String getRemaining(PlayerTeam playerteam) {
        long time = this.cooldowns.get(playerteam.getUniqueID()) - System.currentTimeMillis();
        return Formatter.getRemaining(time, true);
    }
}
