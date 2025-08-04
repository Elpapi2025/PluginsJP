package me.titan.core.modules.ability;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.ability.listener.AbilityListener;
import me.titan.core.modules.ability.type.*;
import me.titan.core.modules.framework.Manager;
import me.titan.core.utils.extra.Cooldown;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AbilityManager extends Manager {
    private Cooldown globalCooldown;
    private Map<String, Ability> abilities;
    
    public AbilityManager(HCF plugin) {
        super(plugin);
        this.abilities = new HashMap<>();
        this.globalCooldown = new Cooldown(this);
        new AbilityListener(this);
        new SwitcherAbility(this);
        new AntiBuildAbility(this);
        new TimeWarpAbility(this);
        new PocketBardAbility(this);
        new InvisibilityAbility(this);
        new PortableArcherAbility(this);
        new LightningAbility(this);
        new RageBallAbility(this);
        new CraftingChaosAbility(this);
        new ComboAbility(this);
        new FocusModeAbility(this);
        new LuckyModeAbility(this);
        new NinjaAbility(this);
        new RegenerativeAuraAbility(this);
        new SamuraiEdgeAbility(this);
        new GuardianShieldAbility(this);
        new PortableBardAbility(this);
    }
    
    public Ability getAbility(String ability) {
        return this.abilities.get(ability.toUpperCase());
    }
}