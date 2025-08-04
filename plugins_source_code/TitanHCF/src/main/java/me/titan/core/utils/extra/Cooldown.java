package me.titan.core.utils.extra;

import me.titan.core.utils.Formatter;
import org.bukkit.entity.*;
import me.titan.core.modules.framework.*;
import java.util.*;
import me.titan.core.utils.*;

public class Cooldown {
    
    private final Map<UUID, Long> cooldowns;
    
    public boolean hasCooldown(Player player) {
        return this.cooldowns.containsKey(player.getUniqueId()) && this.cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis();
    }
    
    public void removeCooldown(Player player) {
        this.cooldowns.remove(player.getUniqueId());
    }
    
    public void applyCooldown(Player player, int HCF) {
        this.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + HCF * 1000L);
    }
    
    public String getRemaining(Player player) {
        long strings = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.getRemaining(strings, true);
    }
    
    private void clean() {
        this.cooldowns.values().removeIf(time -> time - System.currentTimeMillis() < 0L);
    }
    
    public void applyCooldownTicks(Player player, int HCF) {
        this.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + HCF);
    }
    
    public Cooldown(Manager manager) {
        this.cooldowns = new HashMap<>();
        Tasks.executeScheduled(manager, 6000, this::clean);
    }
}
