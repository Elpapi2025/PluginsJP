package me.keano.azurite.modules.waypoints;

import lombok.Getter;
import lombok.Setter;
import me.keano.azurite.HCF;
import me.keano.azurite.modules.framework.Manager;
import me.keano.azurite.modules.waypoints.listener.WaypointListener;
import me.keano.azurite.utils.Serializer;
import me.keano.azurite.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class WaypointManager extends Manager {

    private Waypoint spawnWaypoint;
    private Waypoint endSpawnWaypoint;
    private Waypoint netherSpawnWaypoint;

    private Waypoint glowstoneWaypoint;
    private Waypoint oreMountainWaypoint;
    private Waypoint kothWaypoint;
    private Waypoint endExitWaypoint;

    private Waypoint hqWaypoint;
    private Waypoint focusWaypoint;
    private Waypoint rallyWaypoint;
    private Waypoint conquestWaypoint;

    private Location worldSpawn;
    private Location netherSpawn;
    private Location endSpawn;
    private Location endExit;
    private Location endWorldExit; // TODO: find a better place for this.

    public WaypointManager(HCF instance) {
        super(instance);

        this.spawnWaypoint = getWaypoint(WaypointType.SPAWN);
        this.endSpawnWaypoint = getWaypoint(WaypointType.END_SPAWN);
        this.netherSpawnWaypoint = getWaypoint(WaypointType.NETHER_SPAWN);

        this.glowstoneWaypoint = getWaypoint(WaypointType.GLOWSTONE);
        this.oreMountainWaypoint = getWaypoint(WaypointType.ORE_MOUNTAIN);
        this.kothWaypoint = getWaypoint(WaypointType.KOTH);
        this.endExitWaypoint = getWaypoint(WaypointType.END_EXIT);

        this.hqWaypoint = getWaypoint(WaypointType.HQ);
        this.focusWaypoint = getWaypoint(WaypointType.FOCUS);
        this.rallyWaypoint = getWaypoint(WaypointType.RALLY_POINT);
        this.conquestWaypoint = getWaypoint(WaypointType.CONQUEST);

        this.endExit = Serializer.deserializeLoc(getMiscConfig().getString("END_EXIT"));
        this.endWorldExit = Serializer.deserializeLoc(getMiscConfig().getString("WORLD_EXIT"));
        this.worldSpawn = Serializer.deserializeLoc(getMiscConfig().getString("OVERWORLD_SPAWN"));
        this.netherSpawn = Serializer.deserializeLoc(getMiscConfig().getString("NETHER_SPAWN"));
        this.endSpawn = Serializer.deserializeLoc(getMiscConfig().getString("END_SPAWN"));

        this.loadWorlds();
        new WaypointListener(this);
    }

    @Override
    public void reload() {
        this.spawnWaypoint = getWaypoint(WaypointType.SPAWN);
        this.endSpawnWaypoint = getWaypoint(WaypointType.END_SPAWN);
        this.netherSpawnWaypoint = getWaypoint(WaypointType.NETHER_SPAWN);
        this.glowstoneWaypoint = getWaypoint(WaypointType.GLOWSTONE);
        this.oreMountainWaypoint = getWaypoint(WaypointType.ORE_MOUNTAIN);
        this.kothWaypoint = getWaypoint(WaypointType.KOTH);
        this.endExitWaypoint = getWaypoint(WaypointType.END_EXIT);
        this.hqWaypoint = getWaypoint(WaypointType.HQ);
        this.focusWaypoint = getWaypoint(WaypointType.FOCUS);
        this.rallyWaypoint = getWaypoint(WaypointType.RALLY_POINT);
        this.conquestWaypoint = getWaypoint(WaypointType.CONQUEST);
    }

    private Waypoint getWaypoint(WaypointType type) {
        String path = "WAYPOINTS." + type.name() + ".";
        return new Waypoint(this,
                getLunarConfig().getString(path + "NAME"), type,
                getLunarConfig().getUntranslatedString(path + "COLOR"),
                getLunarConfig().getBoolean(path + "ENABLED")
        );
    }

    public void enableStaffModules(Player player) {
        getInstance().getClientHook().giveStaffModules(player);
    }

    public void disableStaffModules(Player player) {
        getInstance().getClientHook().disableStaffModules(player);
    }

    public void loadWorlds() {
        Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
            for (World world : Bukkit.getServer().getWorlds()) {
                world.setWeatherDuration(Integer.MAX_VALUE);
                world.setThundering(false);
                world.setStorm(false);
                world.setGameRuleValue("mobGriefing", "false");

                if (Utils.isModernVer()) {
                    world.setGameRuleValue("maxEntityCramming", "0");
                    world.setGameRuleValue("doTraderSpawning", "false"); // disable the wandering traders thing
                    world.setGameRuleValue("doPatrolSpawning", "false");
                    world.setGameRuleValue("doInsomnia", "false"); // phantoms
                    world.setGameRuleValue("disableRaids", "true");
                }
            }
        }, 20 * 10L);
    }
}