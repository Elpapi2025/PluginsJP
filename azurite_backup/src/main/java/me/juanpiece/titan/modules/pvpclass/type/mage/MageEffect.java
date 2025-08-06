package me.juanpiece.titan.modules.pvpclass.type.mage;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.pvpclass.PvPClassManager;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.teams.type.SafezoneTeam;
import me.juanpiece.titan.utils.Serializer;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class MageEffect extends Module<PvPClassManager> {

    private PotionEffect effect;

    private int mageDistance;
    private int energyRequired;

    private boolean effectFriendlies;
    private boolean effectSelf;
    private boolean effectEnemies;

    public MageEffect(PvPClassManager manager, Map<String, Object> map) {
        super(manager);
        this.effect = Serializer.getEffect((String) map.get("EFFECT"));
        this.mageDistance = getClassesConfig().getInt("MAGE_CLASS.MAGE_DISTANCE");
        this.energyRequired = (int) map.get("ENERGY_REQUIRED");
        this.effectFriendlies = (boolean) map.get("EFFECT_FRIENDLIES");
        this.effectSelf = (boolean) map.get("EFFECT_SELF");
        this.effectEnemies = (boolean) map.get("EFFECT_ENEMIES");
    }

    public void applyEffect(Player player) {
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());

        player.sendMessage(getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.USED_EFFECT")
                .replace("%effect%", Utils.convertName(effect.getType()))
                .replace("%energy%", String.valueOf(energyRequired))
        );

        if (effectSelf) {
            getManager().addEffect(player, effect);
        }

        int halfDist = mageDistance / 2; // Half the distance on y-axis.

        for (Entity nearbyEntity : player.getNearbyEntities(mageDistance, halfDist, mageDistance)) {
            if (!(nearbyEntity instanceof Player)) continue;
            if (nearbyEntity == player) continue;

            Player nearby = (Player) nearbyEntity;
            Team atNearby = getInstance().getTeamManager().getClaimManager().getTeam(nearby.getLocation());

            // never apply any effects to players in safezones.
            if (atNearby instanceof SafezoneTeam) continue;

            if (effectFriendlies && pt != null && pt.getPlayers().contains(nearby.getUniqueId())) {
                getManager().addEffect(nearby, effect);
            }

            if (effectEnemies) {
                // Don't affect allies or teammates
                if (pt != null) {
                    if (pt.isAlly(nearby)) continue;
                    if (pt.getPlayers().contains(nearby.getUniqueId())) continue;
                }

                getManager().addEffect(nearby, effect);
            }
        }
    }
}