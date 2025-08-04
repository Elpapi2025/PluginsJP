package me.titan.core.modules.deathban.listener;

import me.titan.core.modules.deathban.Deathban;
import me.titan.core.modules.deathban.DeathbanManager;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.users.User;
import me.titan.core.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class DeathbanListener extends HCFModule<DeathbanManager> {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            return;
        }
        if (this.getManager().isDeathbanned(player)) {
            player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.CANNOT_USE_COMMAND"));
            event.setCancelled(true);
        }
    }
    
    private boolean checkSign(String[] a1, String[] a2) {
        for (int i = 0; i < a1.length; ++i) {
            if (!ChatColor.stripColor(a1[i]).equals(ChatColor.stripColor(a2[i]))) {
                return false;
            }
        }
        return true;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        Deathban deathban = user.getDeathban();
        if (deathban == null) {
            return;
        }
        if (deathban.isExpired()) {
            this.getManager().removeDeathban(player);
            return;
        }
        player.teleport(this.getManager().getArenaSpawn());
        Tasks.executeLater(this.getManager(), 20, () -> this.getManager().sendSignInfo(player));
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!this.getManager().isDeathbanned(player)) {
            this.getManager().applyDeathban(player);
            return;
        }
        Player killer = player.getKiller();
        if (killer == null) {
            return;
        }
        if (killer == player) {
            return;
        }
        if (!this.getManager().isDeathbanned(killer)) {
            return;
        }
        Deathban deathban = this.getManager().getDeathban(killer);
        int time = this.getConfig().getInt("DEATHBANS.KILL_TIME");
        deathban.setTime(System.currentTimeMillis() + deathban.getTime() - time * 60000L);
        killer.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.LOWERED_KILL").replaceAll("%mins%", String.valueOf(time)));
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        if (!this.getManager().isDeathbanned(player)) {
            return;
        }
        Sign sign = (Sign)block.getState();
        List<String> lines = this.getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.LIVES_SIGN");
        if (this.checkSign(sign.getLines(), lines.toArray(new String[0]))) {
            User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
            if (user.getLives() <= 0) {
                player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.NO_LIVES"));
                return;
            }
            this.getManager().removeDeathban(player);
            user.setLives(user.getLives() - 1);
            user.save();
        }
    }
    
    public DeathbanListener(DeathbanManager manager) {
        super(manager);
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        boolean bypass = player.hasPermission("titan.deathban.bypass");
        if (this.getManager().isDeathbanned(player) && !bypass) {
            event.setRespawnLocation(this.getManager().getArenaSpawn());
            Tasks.executeLater(this.getManager(), 20, () -> this.getManager().sendSignInfo(player));
            return;
        }
        if (bypass) {
            player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.BYPASSED_DEATHBAN"));
        }
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
        this.getInstance().getTimerManager().getPvpTimer().applyTimer(player);
        Tasks.execute(this.getManager(), () -> this.getInstance().getNametagManager().update());
    }
    
    @EventHandler
    public void onInteractSign(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!event.getClickedBlock().getType().name().contains("SIGN")) {
            return;
        }
        if (!this.getManager().isDeathbanned(player)) {
            return;
        }
        this.getManager().sendSignInfo(player);
    }
    
    @EventHandler
    public void onSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("titan.customsigns")) {
            return;
        }
        if (event.getLine(0).contains("[lives]")) {
            List<String> lines = this.getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.LIVES_SIGN");
            for (int i = 0; i < lines.size(); ++i) {
                event.setLine(i, lines.get(i));
            }
            return;
        }
        if (event.getLine(0).contains("[deathlocsign]")) {
            this.getManager().setDeathLocationSign(event.getBlock().getLocation());
            this.getManager().save();
            player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.CREATED_DEATHLOCSIGN"));
            return;
        }
        if (event.getLine(0).contains("[killedbysign]")) {
            this.getManager().setKilledBySign(event.getBlock().getLocation());
            this.getManager().save();
            player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.CREATED_KILLEDBYSIGN"));
        }
    }
}
