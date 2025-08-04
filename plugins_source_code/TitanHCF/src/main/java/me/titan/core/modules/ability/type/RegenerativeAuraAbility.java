package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.modules.teams.type.PlayerTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RegenerativeAuraAbility extends Ability {

    public RegenerativeAuraAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Regenerative Aura");
    }

    @Override
    public void onClick(Player p) {
        if (hasCooldown(p)) {
            p.sendMessage(getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", getDisplayName()).replaceAll("%time%", abilityCooldown.getRemainingString(p)));
            return;
        }

        PlayerTeam team = getInstance().getTeamManager().getPlayerTeam(p.getUniqueId());

        int radius = getAbilitiesConfig().getInt(getNameConfig() + ".RADIUS");
        int duration = 10 * 20; // 10 seconds in ticks
        int amplifier = 1; // Regeneration II (0 is I, 1 is II)

        if (team == null) {
            // Player is not in a team, apply effect to themselves
            getInstance().getClassManager().addEffect(p, new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            for (String s : getLanguageConfig().getStringList("ABILITIES.REGENERATIVE_AURA.USED_SOLO")) {
                p.sendMessage(s);
            }
        } else {
            // Player is in a team, apply effect to teammates within radius
            for (String s : getLanguageConfig().getStringList("ABILITIES.REGENERATIVE_AURA.USED")) {
                p.sendMessage(s);
            }
            for (Player online : team.getOnlinePlayers()) {
                if (online.getLocation().distance(p.getLocation()) <= radius) {
                    getInstance().getClassManager().addEffect(online, new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
                    if (!online.getUniqueId().equals(p.getUniqueId())) {
                        for (String s : getLanguageConfig().getStringList("ABILITIES.REGENERATIVE_AURA.TEAMMATE_RECEIVED")) {
                            online.sendMessage(s.replaceAll("%player%", p.getName()));
                        }
                    }
                }
            }
        }

        applyCooldown(p);
        takeItem(p);
    }
}