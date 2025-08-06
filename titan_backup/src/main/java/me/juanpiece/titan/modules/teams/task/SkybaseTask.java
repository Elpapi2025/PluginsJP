package me.juanpiece.titan.modules.teams.task;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.claims.Claim;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class SkybaseTask extends Module<TeamManager> {

    private final UUID uniqueId;
    private final Claim claim;
    private final ItemStack wall;

    public SkybaseTask(TeamManager manager, UUID uniqueId, Claim claim, ItemStack wall) {
        super(manager);
        this.uniqueId = uniqueId;
        this.claim = claim;
        this.wall = wall;
        this.run();
    }

    private void run() {
        // Placeholder for skybase generation logic
        Location corner1 = claim.getMinimumPoint();
        Location corner2 = claim.getMaximumPoint();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        int y = getManager().getTeamConfig().getInt("SKYBASE_CONFIG.Y_LEVEL");

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                new Location(corner1.getWorld(), x, y, z).getBlock().setType(wall.getType());
                new Location(corner1.getWorld(), x, y, z).getBlock().setData(wall.getData().getData());
            }
        }
    }
}
