package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import me.titan.core.utils.Utils;
import me.titan.core.utils.extra.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlitchListener extends HCFModule<ListenerManager> {
    private final Cooldown hitCooldown;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player player = Utils.getDamager(event.getDamager());
        if (player == null) {
            return;
        }
        if (this.hitCooldown.hasCooldown(player) && !player.hasLineOfSight(event.getEntity())) {
            this.hitCooldown.removeCooldown(player);
            event.setCancelled(true);
        }
    }
    
    public GlitchListener(ListenerManager manager) {
        super(manager);
        this.hitCooldown = new Cooldown(manager);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        if (event.useInteractedBlock() == Event.Result.DENY || !this.getInstance().getTeamManager().canBuild(player, location)) {
            this.hitCooldown.applyCooldownTicks(player, 950);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        this.hitCooldown.removeCooldown(event.getPlayer());
    }
    
    public Cooldown getHitCooldown() {
        return this.hitCooldown;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        if (event.isCancelled() || !this.getInstance().getTeamManager().canBuild(player, location)) {
            this.hitCooldown.applyCooldownTicks(player, 950);
        }
    }
}
