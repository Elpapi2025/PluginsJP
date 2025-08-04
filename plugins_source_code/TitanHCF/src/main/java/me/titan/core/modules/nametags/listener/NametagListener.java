package me.titan.core.modules.nametags.listener;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.nametags.Nametag;
import me.titan.core.modules.nametags.NametagManager;
import me.titan.core.modules.versions.type.Version1_8_R3;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;

import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class NametagListener extends HCFModule<NametagManager> {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getNametags().remove(player.getUniqueId());
    }
    
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getManager().getNametags().put(player.getUniqueId(), new Nametag(this.getManager(), player));
        this.getManager().update();
    }
    
    public NametagListener(NametagManager manager) {
        super(manager);
    }
}
