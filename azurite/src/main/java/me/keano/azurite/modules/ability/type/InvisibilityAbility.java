package me.keano.azurite.modules.ability.type;

import lombok.Getter;
import me.keano.azurite.modules.ability.Ability;
import me.keano.azurite.modules.ability.AbilityManager;
import me.keano.azurite.modules.ability.extra.AbilityUseType;
import me.keano.azurite.utils.Serializer;
import me.keano.azurite.utils.Tasks;
import me.keano.azurite.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class InvisibilityAbility extends Ability {

    private final Set<UUID> invisible;
    private final PotionEffect invisEffect;
    private final boolean removeInvis;

    public InvisibilityAbility(AbilityManager manager) {
        super(
                manager,
                AbilityUseType.INTERACT,
                "Invisibility"
        );
        this.invisible = new HashSet<>();
        this.invisEffect = Serializer.getEffect(getAbilitiesConfig().getString("INVISIBILITY.INVIS_EFFECT"));
        this.removeInvis = getAbilitiesConfig().getBoolean("INVISIBILITY.TAKE_INVIS_ON_HIT");
    }

    @Override
    public void onClick(Player player) {
        if (cannotUse(player)) return;
        if (hasCooldown(player)) return;

        takeItem(player);
        applyCooldown(player);
        hideArmor(player);

        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            getInstance().getClassManager().addEffect(player, invisEffect);
        }

        // Ticks later
        Tasks.executeLater(getManager(), 5L, () -> invisible.add(player.getUniqueId()));

        for (String s : getLanguageConfig().getStringList("ABILITIES.INVISIBILITY.USED")) {
            player.sendMessage(s);
        }

        player.setFireTicks(0);
        getInstance().getVersionManager().getVersion().clearArrows(player);

        Tasks.executeLater(getManager(), invisEffect.getDuration(), () -> {
            if (player.isOnline() && invisible.remove(player.getUniqueId())) {
                player.sendMessage(getLanguageConfig().getString("ABILITIES.INVISIBILITY.EXPIRED"));
            }

            // Ticks later
            Tasks.executeLater(getManager(), 5L, () -> showArmor(player));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (UUID uuid : invisible) {
            Player toRemove = Bukkit.getPlayer(uuid);
            if (toRemove != null) hideArmor(player);
        }

        getInstance().getVersionManager().getVersion().handleNettyListener(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player damaged = (Player) e.getEntity();
        Player damager = Utils.getDamager(e.getDamager());

        if (damager == null) return;
        if (damager == damaged) return;

        if (invisible.remove(damaged.getUniqueId())) {
            if (removeInvis) {
                damaged.removePotionEffect(PotionEffectType.INVISIBILITY);
            }

            damaged.sendMessage(getLanguageConfig().getString("ABILITIES.INVISIBILITY.DAMAGED"));
            Tasks.executeLater(getManager(), 5L, () -> showArmor(damaged));
        }
    }

    private void hideArmor(Player player) {
        getInstance().getVersionManager().getVersion().hideArmor(player);
    }

    private void showArmor(Player player) {
        getInstance().getVersionManager().getVersion().showArmor(player);
    }
}