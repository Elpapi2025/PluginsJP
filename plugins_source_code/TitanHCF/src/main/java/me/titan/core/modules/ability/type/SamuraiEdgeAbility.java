package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SamuraiEdgeAbility extends Ability {

    private final Map<UUID, UUID> lastDamager = new HashMap<>();
    private final Set<UUID> blockedInteractions = new HashSet<>();

    public SamuraiEdgeAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Samurai Edge");
    }

    @Override
    public void onClick(Player p) {
        if (hasCooldown(p)) {
            p.sendMessage(getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", getDisplayName()).replaceAll("%time%", abilityCooldown.getRemainingString(p)));
            return;
        }

        if (!lastDamager.containsKey(p.getUniqueId())) {
            p.sendMessage(getLanguageConfig().getString("ABILITIES.SAMURAI_EDGE.NO_LAST_HIT"));
            return;
        }

        Player target = Bukkit.getPlayer(lastDamager.get(p.getUniqueId()));

        if (target == null || !target.isOnline()) {
            p.sendMessage(getLanguageConfig().getString("ABILITIES.SAMURAI_EDGE.NO_LAST_HIT"));
            return;
        }

        // Start teleportation countdown
        new BukkitRunnable() {
            int countdown = 5;
            @Override
            public void run() {
                if (countdown <= 0) {
                    // Teleport and apply effects
                    p.teleport(target.getLocation());

                    int damagerStrengthDuration = 8 * 20; // 8 seconds in ticks
                    int damagerStrengthAmplifier = 1; // Strength II (0 is I, 1 is II)
                    int damagerResistanceDuration = 8 * 20; // 8 seconds in ticks
                    int damagerResistanceAmplifier = 0; // Resistance I (0 is I)

                    getInstance().getClassManager().addEffect(p, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, damagerStrengthDuration, damagerStrengthAmplifier));
                    getInstance().getClassManager().addEffect(p, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, damagerResistanceDuration, damagerResistanceAmplifier));

                    int targetBlindnessDuration = 8 * 20; // 8 seconds in ticks
                    int targetBlindnessAmplifier = 0; // Blindness I (0 is I)

                    getInstance().getClassManager().addEffect(target, new PotionEffect(PotionEffectType.BLINDNESS, targetBlindnessDuration, targetBlindnessAmplifier));

                    blockedInteractions.add(target.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            blockedInteractions.remove(target.getUniqueId());
                        }
                    }.runTaskLater(getInstance(), targetBlindnessDuration);

                    for (String s : getLanguageConfig().getStringList("ABILITIES.SAMURAI_EDGE.USED")) {
                        p.sendMessage(s.replaceAll("%player%", target.getName()));
                    }

                    for (String s : getLanguageConfig().getStringList("ABILITIES.SAMURAI_EDGE.BEEN_HIT")) {
                        target.sendMessage(s.replaceAll("%player%", p.getName()));
                    }

                    applyCooldown(p);
                    cancel();
                } else {
                    p.sendMessage(getLanguageConfig().getString("ABILITIES.SAMURAI_EDGE.TELEPORTING").replaceAll("%seconds%", String.valueOf(countdown)).replaceAll("%player%", target.getName()));
                    target.sendMessage(getLanguageConfig().getString("ABILITIES.SAMURAI_EDGE.TARGET_TELEPORTING").replaceAll("%player%", p.getName()).replaceAll("%seconds%", String.valueOf(countdown)));
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                    target.playSound(target.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                    countdown--;
                }
            }
        }.runTaskTimer(getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();

            lastDamager.put(damaged.getUniqueId(), damager.getUniqueId());
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();

        if (abilityCooldown.hasTimer(p)) {
            e.setCancelled(true);
            p.sendMessage(getLanguageConfig().getString("ABILITIES.SAMURAI_EDGE.CANNOT_USE"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (blockedInteractions.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
