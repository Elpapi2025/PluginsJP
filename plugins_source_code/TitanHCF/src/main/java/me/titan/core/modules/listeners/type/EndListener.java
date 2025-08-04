package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class EndListener extends HCFModule<ListenerManager> {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = event.getTo().getBlock();
        if (player.getWorld().getEnvironment() == World.Environment.THE_END && block.getType().name().contains("WATER")) {
            player.teleport(this.getInstance().getWaypointManager().getEndWorldExit());
            player.sendMessage(this.getLanguageConfig().getString("END_LISTENER.ENTERED"));
        }
    }
    
    public EndListener(ListenerManager manager) {
        super(manager);
    }
}
