package me.titan.core.modules.timers.listeners.playertimers;

import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.timers.type.PlayerTimer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class AppleTimer extends PlayerTimer {
    public AppleTimer(TimerManager timer) {
        super(timer, false, "Apple", "PLAYER_TIMERS.APPLE", timer.getConfig().getInt("TIMERS_COOLDOWN.APPLE"));
    }
    
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack.getType() == Material.GOLDEN_APPLE && this.getInstance().getVersionManager().getVersion().isNotGapple(stack)) {
            if (this.hasTimer(player)) {
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("APPLE_TIMER.COOLDOWN").replaceAll("%seconds%", this.getRemainingString(player)));
                return;
            }
            this.applyTimer(player);
        }
    }
}
