package me.juanpiece.titan.modules.ability.type;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.ability.AbilityManager;
import me.juanpiece.titan.modules.ability.extra.AbilityUseType;
import me.juanpiece.titan.modules.ability.task.TeleportTask;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NinjaAbility extends Ability {

    private final int seconds;
    private final int hitsValid;

    public NinjaAbility(AbilityManager manager) {
        super(
                manager,
                AbilityUseType.INTERACT,
                "Ninja Ability"
        );
        this.seconds = getAbilitiesConfig().getInt("NINJA_ABILITY.SECONDS");
        this.hitsValid = getAbilitiesConfig().getInt("NINJA_ABILITY.HITS_VALID");
    }

    @Override
    public void onClick(Player player) {
        if (cannotUse(player)) return;
        if (hasCooldown(player)) return;

        // I already coded a way to get last damager for focus mode, so just use that.
        Player damager = ((FocusModeAbility) getManager().getAbility("FocusMode")).getDamager(player, hitsValid);

        if (damager == null) {
            player.sendMessage(getLanguageConfig().getString("ABILITIES.NINJA_ABILITY.NO_LAST_HIT"));
            return;
        }

        takeItem(player);
        applyCooldown(player);

        new TeleportTask(this, () -> {
            player.teleport(damager);

            for (String s : getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTED_SUCCESSFULLY")) {
                player.sendMessage(s
                        .replace("%player%", damager.getName())
                );
            }
        }, (i) -> {
            for (String s : getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTING"))
                player.sendMessage(s
                        .replace("%player%", damager.getName())
                        .replace("%seconds%", String.valueOf(seconds - i))
                );

            for (String s : getLanguageConfig().getStringList("ABILITIES.NINJA_ABILITY.TELEPORTING_ATTACKER"))
                damager.sendMessage(s
                        .replace("%player%", player.getName())
                        .replace("%target%", damager.getName())
                        .replace("%seconds%", String.valueOf(seconds - i))
                );
        }, seconds);
    }
}