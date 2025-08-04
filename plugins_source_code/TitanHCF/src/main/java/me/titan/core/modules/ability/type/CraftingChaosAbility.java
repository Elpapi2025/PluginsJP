package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CraftingChaosAbility extends Ability {
    private int chance;
    private int seconds;
    private Map<UUID, Integer> hits;
    private Map<UUID, UUID> craftingChaos;
    private int maxHits;
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        Player target = (Player)event.getDamager();
        if (!this.craftingChaos.containsKey(target.getUniqueId())) {
            return;
        }
        if (!this.craftingChaos.get(target.getUniqueId()).equals(player.getUniqueId())) {
            return;
        }
        if (this.cannotHit(target, player)) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(101) <= this.chance) {
            player.openWorkbench(null, true);
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.CRAFTING_CHAOS.MADE_OPEN")) {
                target.sendMessage(s.replaceAll("%player%", player.getName()));
            }
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.hits.remove(player.getUniqueId());
        this.craftingChaos.remove(player.getUniqueId());
    }
    
    public CraftingChaosAbility(AbilityManager manager) {
        super(manager, AbilityUseType.HIT_PLAYER, "Crafting Chaos");
        this.hits = new HashMap<>();
        this.craftingChaos = new HashMap<>();
        this.seconds = this.getAbilitiesConfig().getInt("CRAFTING_CHAOS.SECONDS");
        this.maxHits = this.getAbilitiesConfig().getInt("CRAFTING_CHAOS.HITS_REQUIRED");
        this.chance = this.getAbilitiesConfig().getInt("CRAFTING_CHAOS.CHANCE");
    }
    
    @Override
    public void onHit(Player player, Player target) {
        UUID playerUUID = player.getUniqueId();
        if (this.cannotUse(player, target)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        if (!this.hits.containsKey(playerUUID)) {
            this.hits.put(playerUUID, 0);
        }
        int hit = this.hits.get(playerUUID) + 1;
        this.hits.put(playerUUID, hit);
        if (hit == this.maxHits) {
            this.hits.remove(playerUUID);
            this.craftingChaos.put(playerUUID, target.getUniqueId());
            this.takeItem(player);
            this.applyCooldown(player);
            Tasks.executeLater(this.getManager(), 20 * this.seconds, () -> {
                this.craftingChaos.remove(playerUUID);
                for(String s : this.getLanguageConfig().getStringList("ABILITIES.CRAFTING_CHAOS.EXPIRED")) {
                    player.sendMessage(s);
                }
            });
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.CRAFTING_CHAOS.USED")) {
                player.sendMessage(s.replaceAll("%player%", target.getName()));
            }
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.CRAFTING_CHAOS.BEEN_HIT")) {
                target.sendMessage(s.replaceAll("%player%", player.getName()));
            }
        }
    }
}
