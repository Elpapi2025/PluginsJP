package me.titan.core.modules.timers.listeners.playertimers;

import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.timers.type.PlayerTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class GappleTimer extends PlayerTimer {
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (this.getInstance().getVersionManager().getVersion().isNotGapple(stack)) {
            return;
        }
        if (this.hasTimer(player)) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("GAPPLE_TIMER.COOLDOWN").replaceAll("%seconds%", this.getRemainingString(player)));
            return;
        }
        this.applyTimer(player);
    }
    
    public GappleTimer(TimerManager manager) {
        super(manager, false, "Gapple", "PLAYER_TIMERS.GAPPLE", manager.getConfig().getInt("TIMERS_COOLDOWN.GAPPLE"));
    }
}
