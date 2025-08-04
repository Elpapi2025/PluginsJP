package me.titan.core.modules.ability.listener;

import me.titan.core.modules.framework.*;
import me.titan.core.modules.ability.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import me.titan.core.modules.ability.extra.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import me.titan.core.modules.ability.type.*;

public class AbilityListener extends HCFModule<AbilityManager> {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        for (Ability ability : this.getManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(player)) {
                continue;
            }
            event.setCancelled(true);
            break;
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();
        Player damaged = (Player)event.getEntity();
        ItemStack stack = this.getManager().getItemInHand(damager);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        for (Ability ability : this.getManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(damager)) {
                continue;
            }
            if (ability.getUseType() != AbilityUseType.HIT_PLAYER) {
                continue;
            }
            ability.onHit(damager, damaged);
            break;
        }
    }
    
    public AbilityListener(AbilityManager manager) {
        super(manager);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        if (event.getAction().name().contains("RIGHT")) {
            for (Ability ability : this.getManager().getAbilities().values()) {
                if (!ability.hasAbilityInHand(player)) {
                    continue;
                }
                if (ability.getUseType() != AbilityUseType.INTERACT) {
                    continue;
                }
                event.setCancelled(true);
                ability.onClick(player);
                break;
            }
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            for (Ability ability : this.getManager().getAbilities().values()) {
                if (ability instanceof PocketBardAbility) {
                    PocketBardAbility pockedBardAbility = (PocketBardAbility)ability;
                    if (pockedBardAbility.getPocketBardInHand(player) != null && pockedBardAbility.getAbilityCooldown().hasTimer(player)) {
                        player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                        break;
                    }
                }
                if (ability.hasAbilityInHand(player) && ability.getAbilityCooldown().hasTimer(player)) {
                    player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().hasLore() || !stack.getItemMeta().hasDisplayName()) {
            return;
        }
        for (Ability ability : this.getManager().getAbilities().values()) {
            if (ability instanceof SamuraiEdgeAbility) {
                SamuraiEdgeAbility samuraiEdgeAbility = (SamuraiEdgeAbility) ability;
                if (samuraiEdgeAbility.hasAbilityInHand(player)) {
                    samuraiEdgeAbility.onConsume(event);
                    break;
                }
            }
        }
    }
}
