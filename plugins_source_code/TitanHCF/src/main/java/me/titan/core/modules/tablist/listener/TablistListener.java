package me.titan.core.modules.tablist.listener;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.tablist.Tablist;
import me.titan.core.modules.tablist.TablistManager;
import me.titan.core.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TablistListener extends HCFModule<TablistManager> {
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getTablists().remove(player.getUniqueId());
    }
    
    public TablistListener(TablistManager tablistManager) {
        super(tablistManager);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Tablist tablist = new Tablist(this.getManager(), player);
        Tasks.executeLater(this.getManager(), 10, () -> this.getManager().getTablists().put(player.getUniqueId(), tablist));
    }
}
