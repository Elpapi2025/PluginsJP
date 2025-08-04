package me.titan.core.modules.pvpclass.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.pvpclass.PvPClassManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ArmorListener extends HCFModule<PvPClassManager> {
    @EventHandler
    public void onEquip(PlayerArmorChangeEvent event) {
        if (event.getNewItem() != null && event.getOldItem() != null && event.getNewItem().getType() == event.getOldItem().getType()) {
            return;
        }
        this.getManager().checkArmor(event.getPlayer());
    }
    
    @EventHandler
    public void onEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED && event.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION) {
            PotionEffect effect = event.getOldEffect();
            if (effect == null) {
                return;
            }
            PotionEffect effect2 = this.getManager().getRestores().remove(player.getUniqueId(), effect.getType());
            if (effect2 != null) {
                player.addPotionEffect(effect2, true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquipFix(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getOldItem();
        if (stack == null) {
            return;
        }
        this.getManager().getRestores().rowKeySet().remove(player.getUniqueId());
    }
    
    public ArmorListener(PvPClassManager manager) {
        super(manager);
    }
}
