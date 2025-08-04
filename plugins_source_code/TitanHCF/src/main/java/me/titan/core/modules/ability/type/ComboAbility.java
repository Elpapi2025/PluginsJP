package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ComboAbility extends Ability {
    private int seconds;
    private int amountPerHit;
    private Set<UUID> combo;
    private Map<UUID, Integer> hits;
    private int maxHits;
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        Player target = (Player)event.getDamager();
        UUID targetUUID = target.getUniqueId();
        if (this.cannotHit(target, player)) {
            return;
        }
        if (this.combo.contains(targetUUID)) {
            this.hits.putIfAbsent(targetUUID, 0);
            int hit = this.hits.get(targetUUID);
            if (hit < this.maxHits) {
                this.hits.put(targetUUID, hit + this.amountPerHit);
            }
        }
    }
    
    private void handleEffect(Player player) {
        Integer hit = this.hits.remove(player.getUniqueId());
        this.combo.remove(player.getUniqueId());
        if (hit != null) {
            String[] effects = this.getAbilitiesConfig().getString("COMBO_ABILITY.EFFECT").split(", ");
            PotionEffectType effect = PotionEffectType.getByName(effects[0]);
            int amplifier = Integer.parseInt(effects[1]) - 1;
            this.getInstance().getClassManager().addEffect(player, new PotionEffect(effect, 20 * hit, amplifier));
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.COMBO_ABILITY.GAINED_EFFECT")) {
                player.sendMessage(s.replaceAll("%amount%", String.valueOf(hit)));
            }
        }
    }
    
    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        this.combo.add(player.getUniqueId());
        this.takeItem(player);
        this.applyCooldown(player);
        Tasks.executeLater(this.getManager(), 20 * this.seconds, () -> this.handleEffect(player));
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.COMBO_ABILITY.USED")) {
            player.sendMessage(s);
        }
    }
    
    public ComboAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Combo Ability");
        this.combo = new HashSet<>();
        this.hits = new HashMap<>();
        this.maxHits = this.getAbilitiesConfig().getInt("COMBO_ABILITY.MAX_HITS");
        this.amountPerHit = this.getAbilitiesConfig().getInt("COMBO_ABILITY.AMOUNT_PER_HIT");
        this.seconds = this.getAbilitiesConfig().getInt("COMBO_ABILITY.SECONDS");
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.hits.remove(player.getUniqueId());
        this.combo.remove(player.getUniqueId());
    }
}
