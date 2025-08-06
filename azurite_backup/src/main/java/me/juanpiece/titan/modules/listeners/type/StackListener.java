package me.juanpiece.titan.modules.listeners.type;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.listeners.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Iterator;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class StackListener extends Module<ListenerManager> {

    public StackListener(ListenerManager manager) {
        super(manager);
        getManager().getTasks().add(Bukkit.getScheduler().runTaskTimer(getInstance(), this::clean, 0L, (20 * 60) * 5));
    }

    // Used to clean the entities.
    private void clean() {
        for (World world : Bukkit.getWorlds()) {
            Iterator<LivingEntity> iterator = world.getLivingEntities().iterator();

            while (iterator.hasNext()) {
                LivingEntity entity = iterator.next();

                if (getAmount(entity) == -1) continue;

                entity.remove();
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!getConfig().getBoolean("MOB_STACKING.ENABLED")) return;
        if (e.getEntity() instanceof Villager) return;

        LivingEntity entity = e.getEntity();
        int amount = getAmount(entity);

        if (amount == -1) return;
        if (amount == 1) return; // just let them die

        // Spawn it again
        LivingEntity livingEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());

        if (livingEntity instanceof Ageable) {
            Ageable ageable = (Ageable) livingEntity;

            // Make sure it's an adult
            if (!ageable.isAdult()) {
                ageable.setAdult();
            }
        }

        setAmount(livingEntity, amount - 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (!getConfig().getBoolean("MOB_STACKING.ENABLED")) return;
        if (e.getEntity() instanceof Villager) return;

        LivingEntity entity = e.getEntity();

        boolean newStack = true;
        int radius = getConfig().getInt("MOB_STACKING.RADIUS");

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (entity.getType() == EntityType.ENDERMAN) return; // don't stack endermen.
        if (entity.getType() == EntityType.VILLAGER) return; // don't stack villagers

        for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
            if (!(nearbyEntity instanceof LivingEntity)) continue;
            if (nearbyEntity.getType() != entity.getType()) continue;

            LivingEntity nearby = (LivingEntity) nearbyEntity;
            int amount = getAmount(nearby);

            if (amount == -1) continue;

            if (amount < getConfig().getInt("MOB_STACKING.MAX_STACK")) { // limit stack
                e.setCancelled(true); // just add it to the other stack.
                setAmount(nearby, amount + 1);
                newStack = false;
            }
        }

        if (newStack) {
            setAmount(entity, 1); // create a new one if none nearby to stack to.
        }
    }

    private int getAmount(LivingEntity entity) {
        String name = entity.getCustomName();

        if (name == null) return -1;

        try {

            return Integer.parseInt(ChatColor.stripColor(name).replace("x", ""));

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void setAmount(LivingEntity livingEntity, int amount) {
        livingEntity.setCustomName(getConfig().getString("MOB_STACKING.COLOR") + amount + "x");
        livingEntity.setCustomNameVisible(true);
    }
}