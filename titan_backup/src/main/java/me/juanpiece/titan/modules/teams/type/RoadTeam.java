package me.juanpiece.titan.modules.teams.type;

import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.enums.TeamType;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class RoadTeam extends Team {

    public RoadTeam(TeamManager manager, Map<String, Object> map) {
        super(
                manager,
                map,
                true,
                TeamType.ROAD
        );
    }

    public RoadTeam(TeamManager manager, String name) {
        super(
                manager,
                name,
                UUID.randomUUID(),
                true,
                TeamType.ROAD
        );
    }

    @Override
    public String getDisplayName(Player player) {
        return Config.DISPLAY_NAME_ROAD.replace("%team%", super.getDisplayName(player));
    }

    public boolean canInteract(Location location) {
        Block block = location.getBlock();
        return block.getType() == ItemUtils.getMat("WOOD_PLATE") || block.getType() == ItemUtils.getMat("STONE_PLATE") ||
                block.getType().name().contains("FENCE_GATE");
    }
}