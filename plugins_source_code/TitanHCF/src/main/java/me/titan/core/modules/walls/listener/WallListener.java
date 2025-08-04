package me.titan.core.modules.walls.listener;

import me.titan.core.modules.framework.*;
import me.titan.core.modules.walls.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class WallListener extends HCFModule<WallManager> {

    public WallListener(WallManager wallManager) {
        super(wallManager);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().clearTeamMap(player);
        this.getManager().clearWalls(player);
        this.getManager().getWalls().remove(player.getUniqueId());
        this.getManager().getTeamMaps().remove(player.getUniqueId());
    }
}
