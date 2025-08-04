package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.utils.Serializer;
import me.titan.core.utils.Tasks;
import me.titan.core.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;


import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InvisibilityAbility extends Ability
{
    private Set<UUID> invisible;
    private PotionEffect invisEffect;
    
    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        this.takeItem(player);
        this.applyCooldown(player);
        this.hideArmor(player);
        this.invisible.add(player.getUniqueId());
        this.getInstance().getClassManager().addEffect(player, this.invisEffect);
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.INVISIBILITY.USED")) {
            player.sendMessage(s);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        Player damager = Utils.getDamager(event.getDamager());
        if (damager == null) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        if (this.invisible.contains(player.getUniqueId())) {
            this.showArmor(player);
            this.invisible.remove(player.getUniqueId());
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.INVISIBILITY.DAMAGED"));
        }
    }
    
    private void load() {
        if (this.getInstance().getVersionManager().isVer16()) {
            this.getManager().registerListener(new Listener() {
                @EventHandler
                public void onExpire(EntityPotionEffectEvent event) {
                    if (!(event.getEntity() instanceof Player)) {
                        return;
                    }
                    if (event.getAction() != EntityPotionEffectEvent.Action.REMOVED) {
                        return;
                    }
                    if (event.getCause() != EntityPotionEffectEvent.Cause.EXPIRATION) {
                        return;
                    }
                    if (event.getOldEffect() == null || event.getOldEffect().getType() != PotionEffectType.INVISIBILITY) {
                        return;
                    }
                    Player player = (Player)event.getEntity();
                    if (InvisibilityAbility.this.invisible.remove(player.getUniqueId())) {
                        InvisibilityAbility.this.showArmor(player);
                        player.sendMessage(InvisibilityAbility.this.getLanguageConfig().getString("ABILITIES.INVISIBILITY.EXPIRED"));
                    }
                }
            });
        }
        else {
            // No PotionEffectExpireEvent in 1.8.8, so this functionality is disabled for older versions.
        }
    }
    
    private void hideArmor(Player player) {
        this.getInstance().getVersionManager().getVersion().hideArmor(player);
    }
    
    public InvisibilityAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Invisibility");
        this.invisible = new HashSet<>();
        this.invisEffect = Serializer.getEffect(this.getAbilitiesConfig().getString("INVISIBILITY.INVIS_EFFECT"));
        this.load();
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.invisible.contains(player.getUniqueId())) {
            this.getInstance().getVersionManager().getVersion().hideArmor(player);
        }
    }
    
    private void showArmor(Player player) {
        this.getInstance().getVersionManager().getVersion().showArmor(player);
    }
}
