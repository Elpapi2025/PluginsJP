package me.juanpiece.titan.modules.deathban;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.deathban.listener.DeathbanListener;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.timers.listeners.playertimers.PvPTimer;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.Serializer;
import me.juanpiece.titan.utils.Tasks;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class DeathbanManager extends Manager {

    private final Map<String, Long> deathbanTimes;

    private Location arenaSpawn;
    private Location killedBySign;
    private Location deathLocationSign;

    public DeathbanManager(HCF instance) {
        super(instance);

        this.deathbanTimes = new HashMap<>();
        this.load();

        new DeathbanListener(this);
    }

    private void load() {
        for (String s : getConfig().getStringList("DEATHBANS.TIMES")) {
            String[] split = s.split(", ");
            deathbanTimes.put("titan.deathban." + split[0].toLowerCase(), Integer.parseInt(split[1]) * (60 * 1000L));
        }

        this.arenaSpawn = Serializer.deserializeLoc(getMiscConfig().getString("ARENA_SPAWN"));
        this.killedBySign = Serializer.deserializeLoc(getMiscConfig().getString("KILLED_BY_SIGN"));
        this.deathLocationSign = Serializer.deserializeLoc(getMiscConfig().getString("DEATH_LOC_SIGN"));
    }

    private long getDeathbanTime(Player player) {
        long deathbanTime = getConfig().getInt("DEATHBANS.DEFAULT_TIME") * (60 * 1000L);

        for (Map.Entry<String, Long> entry : deathbanTimes.entrySet()) {
            String perm = entry.getKey();
            Long time = entry.getValue();

            // we only want the lowest
            if (player.hasPermission(perm) && time < deathbanTime) {
                deathbanTime = time;
            }
        }

        return deathbanTime;
    }

    public void save() {
        getMiscConfig().set("ARENA_SPAWN", Serializer.serializeLoc(arenaSpawn));
        getMiscConfig().set("KILLED_BY_SIGN", Serializer.serializeLoc(killedBySign));
        getMiscConfig().set("DEATH_LOC_SIGN", Serializer.serializeLoc(deathLocationSign));
        getMiscConfig().save();
    }

    public void sendSignInfo(Player player) {
        Block deathLoc = deathLocationSign.getBlock();
        Block killedBy = killedBySign.getBlock();
        Deathban deathban = getDeathban(player);

        if (!(deathLoc.getState() instanceof Sign)) return;
        if (!(killedBy.getState() instanceof Sign)) return;

        List<String> killedLines = getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.KILLED_BY");
        List<String> deathLocationLines = getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.DEATH_LOCATION");

        killedLines.replaceAll(s -> s.replace("%reason%", deathban.getReason()));
        deathLocationLines.replaceAll(s -> s.replace("%location%", Utils.formatLocation(deathban.getLocation())));

        player.sendSignChange(deathLocationSign, deathLocationLines.toArray(new String[4]));
        player.sendSignChange(killedBySign, killedLines.toArray(new String[4]));
    }

    public Deathban getDeathban(Player player) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        return user.getDeathban();
    }

    public void removeDeathban(Player player) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setDeathban(null);
        user.save();

        getInstance().getTimerManager().getCombatTimer().removeTimer(player);
        player.sendMessage(getLanguageConfig().getString("DEATHBAN_LISTENER.REVIVED"));

        Tasks.execute(this, () -> {
            player.teleport(getInstance().getWaypointManager().getWorldSpawn().clone().add(0.5, 0, 0.5));
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();

            PvPTimer pvPTimer = getInstance().getTimerManager().getPvpTimer();
            if (pvPTimer.getSeconds() != 0) pvPTimer.applyTimer(player);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        });
    }

    public boolean isDeathbanned(Player player) {
        return getDeathban(player) != null;
    }

    public void applyDeathban(Player player) {
        if (player.hasPermission("titan.deathban.bypass")) return;

        EntityDamageEvent cause = player.getLastDamageCause();
        String reason = (player.getKiller() != null ? player.getKiller().getName() : (cause == null ? "Unknown" : cause.getCause().toString()));
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setDeathban(new Deathban(this, player.getUniqueId(), getDeathbanTime(player), reason, player.getLocation()));
        user.save();
    }
}