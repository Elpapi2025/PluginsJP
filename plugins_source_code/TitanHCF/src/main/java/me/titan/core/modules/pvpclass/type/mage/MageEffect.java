package me.titan.core.modules.pvpclass.type.mage;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.pvpclass.PvPClassManager;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.teams.type.SafezoneTeam;
import me.titan.core.utils.Serializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

@Getter
@Setter
public class MageEffect extends HCFModule<PvPClassManager> {
    private int energyRequired;
    private int mageDistance;
    private PotionEffect effect;
    
    public void applyEffect(Player player) {
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        int distance = this.mageDistance / 2;
        player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.USED_EFFECT").replaceAll("%effect%", this.effect.getType().getName()).replaceAll("%energy%", String.valueOf(this.energyRequired)));
        for (Entity entity : player.getNearbyEntities(this.mageDistance, distance, this.mageDistance)) {
            if (!(entity instanceof Player)) {
                continue;
            }
            Player target = (Player)entity;
            Team targetTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(target.getLocation());
            if (targetTeam instanceof SafezoneTeam) {
                continue;
            }
            if (team != null) {
                if (team.getPlayers().contains(player.getUniqueId())) {
                    continue;
                }
                if (team.isAlly(player)) {
                    continue;
                }
            }
            this.getManager().addEffect(target, this.effect);
        }
    }
    
    public MageEffect(PvPClassManager manager, Map<String, Object> map) {
        super(manager);
        this.effect = Serializer.getEffect((String) map.get("EFFECT"));
        this.energyRequired = (int) map.get("ENERGY_REQUIRED");
        this.mageDistance = this.getClassesConfig().getInt("MAGE_CLASS.MAGE_DISTANCE");
    }
}