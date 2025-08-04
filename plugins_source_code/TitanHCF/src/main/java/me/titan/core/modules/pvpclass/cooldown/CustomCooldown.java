package me.titan.core.modules.pvpclass.cooldown;

import lombok.Getter;
import me.titan.core.modules.pvpclass.PvPClass;
import me.titan.core.utils.extra.Cooldown;

@Getter
public class CustomCooldown extends Cooldown {
    private final String displayName;
    
    public CustomCooldown(PvPClass pvpClass, String name) {
        super(pvpClass.getManager());
        this.displayName = (name.isEmpty() ? null : name);
        pvpClass.getCustomCooldowns().add(this);
    }
    
}