package me.titan.core.modules.pvpclass;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.pvpclass.listener.ArmorLegacyListener;
import me.titan.core.modules.pvpclass.listener.ArmorListener;
import me.titan.core.modules.pvpclass.listener.PvPClassListener;
import me.titan.core.modules.pvpclass.type.archer.ArcherClass;
import me.titan.core.modules.pvpclass.type.bard.BardClass;
import me.titan.core.modules.pvpclass.type.mage.MageClass;
import me.titan.core.modules.pvpclass.type.miner.MinerClass;
import me.titan.core.modules.pvpclass.type.rogue.RogueClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PvPClassManager extends Manager {
    private MinerClass minerClass;
    private final Table<UUID, PotionEffectType, PotionEffect> restores;
    private MageClass mageClass;
    private RogueClass rogueClass;
    private BardClass bardClass;
    private final Map<String, PvPClass> classes;
    private final Map<UUID, PvPClass> activeClasses;
    private ArcherClass archerClass;
    
    private void load() {
        if (this.getClassesConfig().getBoolean("BARD_CLASS.ENABLED")) {
            this.bardClass = new BardClass(this);
        }
        if (this.getClassesConfig().getBoolean("MAGE_CLASS.ENABLED")) {
            this.mageClass = new MageClass(this);
        }
        if (this.getClassesConfig().getBoolean("ARCHER_CLASS.ENABLED")) {
            this.archerClass = new ArcherClass(this);
        }
        if (this.getClassesConfig().getBoolean("MINER_CLASS.ENABLED")) {
            this.minerClass = new MinerClass(this);
        }
        if (this.getClassesConfig().getBoolean("ROGUE_CLASS.ENABLED")) {
            this.rogueClass = new RogueClass(this);
        }
    }
    
    public void checkArmor(Player player) {
        for (PvPClass pvpClass : this.getClasses().values()) {
            pvpClass.checkArmor(player);
        }
    }
    
    public void addEffect(Player player, PotionEffect effect) {
        if (!player.hasPotionEffect(effect.getType())) {
            player.addPotionEffect(effect);
            return;
        }
        for (PotionEffect activeEffect : player.getActivePotionEffects()) {
            if (!activeEffect.getType().equals(effect.getType())) {
                continue;
            }
            if (activeEffect.getAmplifier() > effect.getAmplifier()) {
                break;
            }
            if (activeEffect.getAmplifier() == effect.getAmplifier() && activeEffect.getDuration() > effect.getDuration()) {
                break;
            }
            if (activeEffect.getDuration() > effect.getDuration()) {
                this.restores.put(player.getUniqueId(), activeEffect.getType(), activeEffect);
                player.removePotionEffect(activeEffect.getType());
            }
            player.addPotionEffect(effect, true);
            break;
        }
    }
    
    @Override
    public void disable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            PvPClass pvpClass = this.getActiveClass(online);
            if (pvpClass == null) {
                continue;
            }
            pvpClass.unEquip(online);
        }
    }
    
    public PvPClass getActiveClass(Player player) {
        return this.activeClasses.get(player.getUniqueId());
    }
    
    public PvPClassManager(HCF plugin) {
        super(plugin);
        this.restores = HashBasedTable.create();
        this.activeClasses = new HashMap<>();
        this.classes = new HashMap<>();
        this.load();
        new PvPClassListener(this);
        if (this.getInstance().getVersionManager().isVer16()) {
            new ArmorListener(this);
        }
        else {
            new ArmorLegacyListener(this);
        }
    }
}