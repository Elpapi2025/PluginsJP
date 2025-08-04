package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.modules.teams.type.PlayerTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GuardianShieldAbility extends Ability {

    public GuardianShieldAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Guardian Shield");
    }

    @Override
    public void onClick(Player p) {
        if (hasCooldown(p)) {
            p.sendMessage(getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", getDisplayName()).replaceAll("%time%", abilityCooldown.getRemainingString(p)));
            return;
        }

        PlayerTeam team = getInstance().getTeamManager().getPlayerTeam(p.getUniqueId());

        int radius = getAbilitiesConfig().getInt(getNameConfig() + ".RADIUS");
        int duration = 15 * 20; // 15 seconds in ticks
        int absorptionAmplifier = 1; // Absorption II (0 is I, 1 is II)
        int resistanceAmplifier = 0; // Resistance I (0 is I)

        if (team == null) {
            // Player is not in a team, apply effect to themselves
            getInstance().getClassManager().addEffect(p, new PotionEffect(PotionEffectType.ABSORPTION, duration, absorptionAmplifier));
            getInstance().getClassManager().addEffect(p, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, resistanceAmplifier));
            for (String s : getLanguageConfig().getStringList("ABILITIES.GUARDIAN_SHIELD.USED_SOLO")) {
                p.sendMessage(s);
            }
        } else {
            // Player is in a team, apply effect to teammates within radius
            for (String s : getLanguageConfig().getStringList("ABILITIES.GUARDIAN_SHIELD.USED")) {
                p.sendMessage(s);
            }
            for (Player online : team.getOnlinePlayers()) {
                if (online.getLocation().distance(p.getLocation()) <= radius) {
                    getInstance().getClassManager().addEffect(online, new PotionEffect(PotionEffectType.ABSORPTION, duration, absorptionAmplifier));
                    getInstance().getClassManager().addEffect(online, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, resistanceAmplifier));
                    if (!online.getUniqueId().equals(p.getUniqueId())) {
                        for (String s : getLanguageConfig().getStringList("ABILITIES.GUARDIAN_SHIELD.TEAMMATE_RECEIVED")) {
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
