package me.juanpiece.titan.modules.spawners.listener;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.spawners.Spawner;
import me.juanpiece.titan.modules.spawners.SpawnerManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SpawnerListener extends Module<SpawnerManager> {

    public SpawnerListener(SpawnerManager manager) {
        super(manager);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlockPlaced();
        ItemStack hand = e.getItemInHand();

        if (block.getState() instanceof CreatureSpawner && hand != null) {
            if (!player.hasPermission("titan.spawner.bypass") && player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                e.setCancelled(true);
                player.sendMessage(getLanguageConfig().getString("SPAWNER_LISTENER.CANNOT_PLACE"));
                return;
            }

            Spawner spawner = getManager().getByItem(hand);

            if (spawner == null) return;

            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            creatureSpawner.setSpawnedType(spawner.getType());
            creatureSpawner.update();

            player.sendMessage(getLanguageConfig().getString("SPAWNER_LISTENER.PLACED_SPAWNER")
                    .replace("%type%", spawner.getName())
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (block.getState() instanceof CreatureSpawner) {
            if (!player.hasPermission("titan.spawner.bypass") && player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                e.setCancelled(true);
                player.sendMessage(getLanguageConfig().getString("SPAWNER_LISTENER.CANNOT_BREAK"));
                return;
            }

            if (!player.hasPermission("titan.spawner.break")) {
                e.setCancelled(true);
                player.sendMessage(getLanguageConfig().getString("SPAWNER_LISTENER.CANNOT_BREAK_CROWBAR"));
                return;
            }

            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            Spawner spawner = getManager().getSpawners().get(creatureSpawner.getSpawnedType());

            if (spawner == null) return;

            e.setCancelled(true);

            block.setType(Material.AIR);
            block.getState().update();
            block.getWorld().dropItemNaturally(block.getLocation(), spawner.getItemStack());

            player.sendMessage(getLanguageConfig().getString("SPAWNER_LISTENER.BREAK_SPAWNER")
                    .replace("%type%", spawner.getName())
            );
        }
    }
}