package me.titan.core.modules.pvpclass.cooldown;

import lombok.Getter;
import me.titan.core.modules.pvpclass.PvPClass;
import org.bukkit.potion.PotionEffect;

@Getter
public class ClassBuff extends CustomCooldown {
    private final PotionEffect effect;
    private final int cooldown;
    
    public ClassBuff(PvPClass pvpClass, String name, PotionEffect effect, int cooldown) {
        super(pvpClass, name);
        this.effect = effect;
        this.cooldown = cooldown;
    }
    
}