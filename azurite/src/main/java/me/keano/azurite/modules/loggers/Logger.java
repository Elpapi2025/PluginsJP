package me.keano.azurite.modules.loggers;

import lombok.Getter;
import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Logger extends Module<LoggerManager> {

    private final Player player;
    private final Villager villager;
    private final BukkitTask removeTask;
    private final ItemStack[] contents;
    private final ItemStack[] armorContents;
    private final float exp;

    public Logger(LoggerManager manager, Player player) {
        super(manager);

        this.player = player;
        this.villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.exp = player.getExp();
        this.removeTask = Bukkit.getScheduler().runTaskLater(
                getInstance(),
                () -> getManager().removeLogger(player),
                getConfig().getInt("LOGGERS.DESPAWN") * 20L
        );

        this.checkVillager();
    }

    private void checkVillager() {
        // Name stuff
        villager.setCustomName(getConfig().getString("LOGGERS.COLOR") + player.getName());
        villager.setCustomNameVisible(true);

        // Health stuff
        villager.setMaxHealth(calcHealth(player)); // 20 hearts
        villager.setHealth(villager.getMaxHealth());

        // Some checks to assure age
        villager.setAdult();
        villager.setBreed(false);
        villager.setProfession(Villager.Profession.FARMER);

        // in case the player is falling we apply it to the villager aswell
        villager.setFallDistance(player.getFallDistance());
        villager.setVelocity(player.getVelocity());
        villager.setRemoveWhenFarAway(true);

        // This is incase the villager spawns in lava, etc...
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                villager.addPotionEffect(effect);
            }
        }

        // So they can't move
        villager.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                Integer.MAX_VALUE,
                100,
                false
        ));
        villager.addPotionEffect(new PotionEffect(
                PotionEffectType.JUMP,
                Integer.MAX_VALUE,
                100,
                false
        ));
    }

    // Credits: HCTeams
    public double calcHealth(Player player) {
        int potions = 0;
        boolean gapple = false;
        ItemStack pot = ItemUtils.tryGetPotion(getManager(), ItemUtils.getMat("SPLASH_POTION"), 16421);

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (itemStack.isSimilar(pot)) {
                potions++;

            } else if (!gapple && !getInstance().getVersionManager().getVersion().isNotGapple(itemStack)) {
                // Only let the player have one gapple count.
                potions += 15;
                gapple = true;
            }
        }

        return ((potions * 3.5D) + player.getHealth());
    }
}