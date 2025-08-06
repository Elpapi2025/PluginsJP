package me.juanpiece.titan.modules.ability.type;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.ability.AbilityManager;
import me.juanpiece.titan.modules.ability.extra.AbilityUseType;
import me.juanpiece.titan.utils.Serializer;
import me.juanpiece.titan.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class UltimateAbility extends Ability {

    private final List<PotionEffect> effects;
    private final int seconds;

    public UltimateAbility(AbilityManager manager) {
        super(
                manager,
                AbilityUseType.INTERACT,
                "Ultimate Ability"
        );
        this.effects = getAbilitiesConfig().getStringList("ULTIMATE_ABILITY.EFFECTS").stream().map(Serializer::getEffect).collect(Collectors.toList());
        this.seconds = getAbilitiesConfig().getInt("ULTIMATE_ABILITY.SECONDS");
    }

    @Override
    public void onClick(Player player) {
        if (cannotUse(player)) return;
        if (hasCooldown(player)) return;

        takeItem(player);
        applyCooldown(player);

        Location location = player.getLocation();
        Tasks.executeLater(getManager(), 20L * seconds, () -> player.teleport(location));

        for (String s : getLanguageConfig().getStringList("ABILITIES.ULTIMATE_ABILITY.USED")) {
            player.sendMessage(s);
        }

        for (PotionEffect effect : effects) {
            getInstance().getClassManager().addEffect(player, effect);
        }
    }
}