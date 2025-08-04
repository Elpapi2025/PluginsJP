package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class StrengthListener extends HCFModule<ListenerManager> {
    public static double strengthNerf;
    
    public StrengthListener(ListenerManager manager) {
        super(manager);
        StrengthListener.strengthNerf = this.getConfig().getDouble("STRENGTH_FIX.MULTIPLIER");
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getDamager();
        if (this.getConfig().getBoolean("STRENGTH_FIX.ENABLED") && player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            event.setDamage(event.getDamage() * StrengthListener.strengthNerf);
        }
    }
    
    static {
        StrengthListener.strengthNerf = 0.0;
    }
}
