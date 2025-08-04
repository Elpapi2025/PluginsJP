package me.titan.core.modules.ability.type;

import me.titan.core.modules.ability.Ability;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.ability.extra.AbilityUseType;
import me.titan.core.modules.teams.type.PlayerTeam;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PortableBardAbility extends Ability implements Listener {

    private final Map<UUID, Zombie> summonedBards = new HashMap<>();
    private final Map<UUID, BukkitRunnable> bardTasks = new HashMap<>();
    private final List<BardEffectData> bardEffects = new ArrayList<>();

    public PortableBardAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Portable Bard");
        loadBardEffects();
        getInstance().getServer().getPluginManager().registerEvents(this, getInstance());
    }

    private void loadBardEffects() {
        bardEffects.add(new BardEffectData(Material.GHAST_TEAR, PotionEffectType.REGENERATION));
        bardEffects.add(new BardEffectData(Material.IRON_INGOT, PotionEffectType.DAMAGE_RESISTANCE));
        bardEffects.add(new BardEffectData(Material.BLAZE_POWDER, PotionEffectType.INCREASE_DAMAGE));
    }

    @Override
    public void onClick(Player player) {
        if (hasCooldown(player)) {
            player.sendMessage(getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", getDisplayName()).replaceAll("%time%", abilityCooldown.getRemainingString(player)));
            return;
        }

        if (summonedBards.containsKey(player.getUniqueId())) {
            summonedBards.get(player.getUniqueId()).remove();
            bardTasks.get(player.getUniqueId()).cancel();
            summonedBards.remove(player.getUniqueId());
            bardTasks.remove(player.getUniqueId());
        }

        Zombie bard = player.getWorld().spawn(player.getLocation(), Zombie.class);
        setBardProperties(bard, player);

        summonedBards.put(player.getUniqueId(), bard);
        player.sendMessage(getLanguageConfig().getString("ABILITIES.PORTABLE_BARD.SUMMONED"));

        BukkitRunnable task = new BardTask(player, bard);
        task.runTaskTimer(getInstance(), 0L, 20L);
        bardTasks.put(player.getUniqueId(), task);

        applyCooldown(player);
        takeItem(player);
    }

    private void setBardProperties(Zombie bard, Player owner) {
        bard.setCustomNameVisible(true);
        bard.setCustomName(getLanguageConfig().getString("ABILITIES.PORTABLE_BARD.BARD_NAME").replace("%player%", owner.getName()));
        bard.setCanPickupItems(false);

        EntityEquipment equipment = bard.getEquipment();
        equipment.clear(); // Clear default equipment (like bows)
        equipment.setHelmet(new ItemStack(Material.GOLD_HELMET));
        equipment.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLD_BOOTS));
        equipment.setItemInHand(new ItemStack(Material.GHAST_TEAR)); // Set initial item

        // NMS to make the zombie silent
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bard).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setBoolean("Silent", true);
        tag.setBoolean("NoAI", true);
        nmsEntity.f(tag);

        
    }

    @EventHandler
    public void onBardDeath(EntityDeathEvent event) {
        if (summonedBards.containsValue(event.getEntity())) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    

    private class BardTask extends BukkitRunnable {
        private final Player owner;
        private final Zombie bard;
        
        private int ticks = 0;
        private int energy = 0;
        private final int maxEnergy = 100;
        private final int energyCost = 30;
        private int effectIndex = 0;

        public BardTask(Player owner, Zombie bard) {
            this.owner = owner;
            this.bard = bard;
        }

        @Override
        public void run() {
            if (!bard.isValid() || bard.isDead()) {
                cancel();
                summonedBards.remove(owner.getUniqueId());
                bardTasks.remove(owner.getUniqueId());
                owner.sendMessage(getLanguageConfig().getString("ABILITIES.PORTABLE_BARD.BARD_DIED"));
                return;
            }
            
            

            if (ticks >= 1200) {
                bard.remove();
                cancel();
                summonedBards.remove(owner.getUniqueId());
                bardTasks.remove(owner.getUniqueId());
                owner.sendMessage(getLanguageConfig().getString("ABILITIES.PORTABLE_BARD.BARD_DESPAWNED"));
                return;
            }

            if (energy < maxEnergy) {
                energy++;
            }

            if (ticks % 20 == 0) { // Rotate item every 1 second
                effectIndex = (effectIndex + 1) % bardEffects.size();
                Material nextMaterial = bardEffects.get(effectIndex).getMaterial();
                net.minecraft.server.v1_8_R3.ItemStack nmsStack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(new ItemStack(nextMaterial));
                PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(bard.getEntityId(), 0, nmsStack);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(packet);
                }
            }

            List<Player> affectedPlayers = getAffectedPlayers(owner, bard);

            for (Player affectedPlayer : affectedPlayers) {
                // Apply all passive effects constantly
                for (BardEffectData effectData : bardEffects) {
                    getInstance().getClassManager().addEffect(affectedPlayer, new PotionEffect(effectData.getPotionEffectType(), 40, 0));
                }

                // Apply one random active effect if energy is sufficient
                if (energy >= energyCost) {
                    int randomEffectIndex = ThreadLocalRandom.current().nextInt(bardEffects.size());
                    BardEffectData activeEffect = bardEffects.get(randomEffectIndex);
                    getInstance().getClassManager().addEffect(affectedPlayer, new PotionEffect(activeEffect.getPotionEffectType(), 100, 1));
                }
            }
             if (energy >= energyCost) {
                    energy -= energyCost;
                }


            ticks++;
        }
    }

    private List<Player> getAffectedPlayers(Player owner, Zombie bard) {
        return Collections.singletonList(owner);
    }

    private static class BardEffectData {
        private final Material material;
        private final PotionEffectType potionEffectType;


        public BardEffectData(Material material, PotionEffectType potionEffectType) {
            this.material = material;
            this.potionEffectType = potionEffectType;
        }

        public Material getMaterial() {
            return material;
        }

        public PotionEffectType getPotionEffectType() {
            return potionEffectType;
        }
    }
}