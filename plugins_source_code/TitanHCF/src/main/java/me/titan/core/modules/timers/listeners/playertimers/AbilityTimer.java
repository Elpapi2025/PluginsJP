package me.titan.core.modules.timers.listeners.playertimers;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.timers.type.PlayerTimer;

public class AbilityTimer extends PlayerTimer {
    private final Ability ability;
    
    public AbilityTimer(TimerManager manager, Ability ability, String name) {
        super(manager, false, ability.getName().replaceAll(" ", ""), name, manager.getAbilitiesConfig().getInt(ability.getNameConfig() + ".COOLDOWN"));
        this.ability = ability;
    }
    
    public Ability getAbility() {
        return this.ability;
    }
}
