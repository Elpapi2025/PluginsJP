package me.juanpiece.titan.modules.nametags.adapter;

import me.juanpiece.titan.modules.events.king.KingManager;
import me.juanpiece.titan.modules.events.sotw.SOTWManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.nametags.Nametag;
import me.juanpiece.titan.modules.nametags.NametagAdapter;
import me.juanpiece.titan.modules.nametags.NametagManager;
import me.juanpiece.titan.modules.nametags.extra.NameVisibility;
import me.juanpiece.titan.modules.staff.StaffManager;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TitanNametags extends Module<NametagManager> implements NametagAdapter {

    public TitanNametags(NametagManager manager) {
        super(manager);
    }

    @Override
    public String getAndUpdate(Player player, Player target) {
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        SOTWManager sotwManager = getInstance().getSotwManager();
        KingManager kingManager = getInstance().getKingManager();
        StaffManager staffManager = getInstance().getStaffManager();

        if (staffManager.isStaffEnabled(target)) {
            return createTeam(player, target, "staff", Config.PREFIX_STAFF, Config.RELATION_STAFF);

        } else if (kingManager.isActive() && kingManager.getKing() == target) {
            return createTeam(player, target, "king", Config.PREFIX_KING, Config.RELATION_KING);

        } else if (pt != null && pt.getPlayers().contains(target.getUniqueId()) || player == target) {
            return createTeam(player, target, "team", "", Config.RELATION_TEAMMATE);

        } else if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) && !staffManager.isStaffEnabled(player)) {
            return createTeam(player, target, "invis", "", "", NameVisibility.NEVER);

        } else if (pt != null && pt.isAlly(target)) {
            return createTeam(player, target, "allies", "", Config.RELATION_ALLIED);

        } else if (sotwManager.isActive() && !sotwManager.getEnabled().contains(target.getUniqueId())) {
            return createTeam(player, target, "sotw", "", Config.RELATION_SOTW);

        } else if (getInstance().getTimerManager().getArcherTagTimer().hasTimer(target)) {
            return createTeam(player, target, "tags", "", Config.RELATION_ARCHERTAG);

        } else if (pt != null && (pt.isFocused(target) || pt.getSingularFocus().contains(target.getUniqueId()))) {
            return createTeam(player, target, "focused", "", Config.RELATION_FOCUSED);

        } else if (getInstance().getTimerManager().getPvpTimer().hasTimer(target)) {
            return createTeam(player, target, "pvptimers", "", Config.RELATION_PVPTIMER);

        } else if (getInstance().getTimerManager().getInvincibilityTimer().hasTimer(target)) {
            return createTeam(player, target, "invinc", "", Config.RELATION_INVINCIBLES);
        }

        return createTeam(player, target, "enemies", "", Config.RELATION_ENEMY);
    }

    private String createTeam(Player player, Player target, String name, String prefix, String color) {
        return createTeam(player, target, name, prefix, color, NameVisibility.ALWAYS);
    }

    // yes it needs color because 1.16 is stupid
    private String createTeam(Player player, Player target, String name, String prefix, String color, NameVisibility visibility) {
        Nametag nametag = getManager().getNametags().get(player.getUniqueId());
        String formattedPrefix = (prefix.isEmpty() ? "" : prefix
                .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(target)))
                .replace("%rank-name%", CC.t(getInstance().getRankHook().getRankName(target))));

        if (nametag != null) {
            nametag.getPacket().create(name, color, formattedPrefix, "", true, visibility);
            nametag.getPacket().addToTeam(target, name);
        }

        return formattedPrefix + color;
    }
}