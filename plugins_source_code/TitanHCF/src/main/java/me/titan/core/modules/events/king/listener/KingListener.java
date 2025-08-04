package me.titan.core.modules.events.king.listener;

import me.titan.core.modules.events.king.KingManager;
import me.titan.core.modules.framework.HCFModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KingListener extends HCFModule<KingManager> {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.getManager().getKing() == player) {
            this.getManager().stopKing(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.getManager().getKing() == player) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setDeathMessage(null);
            this.getManager().stopKing(false);
            this.getInstance().getNametagManager().update();
        }
    }
    
    public KingListener(KingManager manager) {
        super(manager);
    }
}
