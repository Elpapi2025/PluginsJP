package me.juanpiece.titan.modules.ability.type;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.ability.AbilityManager;
import me.juanpiece.titan.modules.ability.extra.AbilityUseType;
import me.juanpiece.titan.utils.Serializer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class MedKitAbility extends Ability {

    private final List<PotionEffect> effects;

    public MedKitAbility(AbilityManager manager) {
        super(
                manager,
                AbilityUseType.INTERACT,
                "Med Kit"
        );
        this.effects = getAbilitiesConfig().getStringList("MED_KIT.EFFECTS").stream().map(Serializer::getEffect).collect(Collectors.toList());
    }

    @Override
    public void onClick(Player player) {
        if (cannotUse(player)) return;
        if (hasCooldown(player)) return;

        takeItem(player);
        applyCooldown(player);

        for (PotionEffect effect : effects) {
            getInstance().getClassManager().addEffect(player, effect);
        }

        for (String s : getLanguageConfig().getStringList("ABILITIES.MED_KIT.USED")) {
            player.sendMessage(s);
        }
    }
}