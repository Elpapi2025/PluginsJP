package me.juanpiece.titan.modules.pvpclass.cooldown;

import lombok.Getter;
import me.juanpiece.titan.modules.pvpclass.PvPClass;
import me.juanpiece.titan.utils.extra.Cooldown;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class CustomCooldown extends Cooldown {

    private final String displayName;

    public CustomCooldown(PvPClass pvpClass, String displayName) {
        super(pvpClass.getManager());
        this.displayName = (displayName.isEmpty() ? null : displayName);
        pvpClass.getCustomCooldowns().add(this);
    }
}