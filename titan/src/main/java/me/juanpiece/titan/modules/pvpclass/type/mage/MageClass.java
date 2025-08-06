package me.juanpiece.titan.modules.pvpclass.type.mage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import me.juanpiece.titan.modules.pvpclass.PvPClass;
import me.juanpiece.titan.modules.pvpclass.PvPClassManager;
import me.juanpiece.titan.modules.pvpclass.cooldown.CustomCooldown;
import me.juanpiece.titan.modules.pvpclass.cooldown.EnergyCooldown;
import me.juanpiece.titan.modules.teams.type.SafezoneTeam;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class MageClass extends PvPClass {

    // uses maps/tables for instant lookup times.
    private final Map<UUID, EnergyCooldown> mageEnergy;
    private final Table<Material, Short, MageEffect> clickableEffects;

    private CustomCooldown mageEffectCooldown;
    private int maxMageEnergy;
    private int mageCooldown;

    public MageClass(PvPClassManager manager) {
        super(manager, "Mage");

        this.mageEnergy = new HashMap<>();
        this.clickableEffects = HashBasedTable.create();

        this.load();
    }

    @Override
    public void load() {
        this.mageEffectCooldown = new CustomCooldown(this, getScoreboardConfig().getString("MAGE_CLASS.MAGE_EFFECT"));
        this.maxMageEnergy = getClassesConfig().getInt("MAGE_CLASS.MAX_ENERGY");
        this.mageCooldown = getClassesConfig().getInt("MAGE_CLASS.MAGE_COOLDOWN");

        getClassesConfig().getConfigurationSection("MAGE_CLASS.CLICKABLE_EFFECTS").getKeys(false).forEach(s -> {
            String path = "MAGE_CLASS.CLICKABLE_EFFECTS." + s;
            String material = getClassesConfig().getString(path + ".MATERIAL");
            Map<String, Object> map = getClassesConfig().getConfigurationSection(path).getValues(false);

            clickableEffects.put(
                    ItemUtils.getMat(material),
                    (short) getClassesConfig().getInt(path + ".DATA"),
                    new MageEffect(getManager(), map)
            );
        });
    }

    @Override
    public void handleEquip(Player player) {
        mageEnergy.put(player.getUniqueId(), new EnergyCooldown(player.getUniqueId(), maxMageEnergy));
    }

    @Override
    public void handleUnequip(Player player) {
        mageEnergy.remove(player.getUniqueId());
    }

    @Override
    public void reload() {
        clickableEffects.clear();
        this.load();
        this.loadEffectsArmor();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT")) return;

        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (item != null && players.contains(player.getUniqueId())) {
            short data = (short) getManager().getData(item);
            MageEffect effect = clickableEffects.get(item.getType(), data);

            if (item.hasItemMeta() && item.getItemMeta().hasLore()) return;
            if (effect == null) return;

            if (mageEffectCooldown.hasCooldown(player)) {
                player.sendMessage(getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.BUFF_COOLDOWN")
                        .replace("%seconds%", mageEffectCooldown.getRemaining(player))
                );
                return;
            }

            if (getEnergyCooldown(player).checkEnergy(effect.getEnergyRequired())) {
                player.sendMessage(getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.INSUFFICIENT_ENERGY")
                        .replace("%energy%", String.valueOf(effect.getEnergyRequired()))
                );
                return;
            }

            if (checkMage(player)) return;

            getInstance().getTimerManager().getCombatTimer().applyTimer(player);
            getEnergyCooldown(player).takeEnergy(effect.getEnergyRequired());
            getManager().takeItemInHand(player, 1);

            mageEffectCooldown.applyCooldown(player, mageCooldown);
            effect.applyEffect(player);
        }
    }

    private boolean checkMage(Player player) {
        TimerManager timerManager = getInstance().getTimerManager();

        if (timerManager.getPvpTimer().hasTimer(player) || timerManager.getInvincibilityTimer().hasTimer(player)) {
            player.sendMessage(getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_PVPTIMER"));
            return true;

        } else if (getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation()) instanceof SafezoneTeam) {
            player.sendMessage(getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_SAFEZONE"));
            return true;
        }

        return false;
    }

    public EnergyCooldown getEnergyCooldown(Player player) {
        return mageEnergy.get(player.getUniqueId());
    }
}