package me.titan.core.modules.waypoints;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.framework.*;
import java.awt.*;
import java.awt.Color;

import org.bukkit.*;
import me.titan.core.*;
import me.titan.core.utils.*;
import me.titan.core.modules.waypoints.listener.*;

@Getter
@Setter
public class WaypointManager extends Manager {
    
    private Waypoint glowstoneWaypoint;
    private Waypoint kothWaypoint;
    private Waypoint hqWaypoint;
    private Waypoint rallyWaypoint;
    private Location endWorldExit;
    private Waypoint oreMountainWaypoint;
    private Waypoint spawnWaypoint;
    private Waypoint focusWaypoint;
    private Waypoint endExitWaypoint;
    private Location endExit;
    
    private Waypoint getWaypoint(WaypointType waypointType) {
        String waypoint = "WAYPOINTS." + waypointType.name() + ".";
        return new Waypoint(this.getLunarConfig().getString(waypoint + "NAME"), waypointType, Color.decode(this.getLunarConfig().getString(waypoint + "COLOR")).getRGB(), !this.isLunarMissing() && this.getLunarConfig().getBoolean(waypoint + "ENABLED"));
    }
    
    public boolean isLunarMissing() {
        return !this.getLunarConfig().getBoolean("LUNAR_API.ENABLED") || Bukkit.getServer().getPluginManager().getPlugin("LunarClient-API") == null;
    }
    
    
    public WaypointManager(HCF plugin) {
        super(plugin);
        this.spawnWaypoint = this.getWaypoint(WaypointType.SPAWN);
        this.glowstoneWaypoint = this.getWaypoint(WaypointType.GLOWSTONE);
        this.oreMountainWaypoint = this.getWaypoint(WaypointType.ORE_MOUNTAIN);
        this.kothWaypoint = this.getWaypoint(WaypointType.KOTH);
        this.endExitWaypoint = this.getWaypoint(WaypointType.END_EXIT);
        this.hqWaypoint = this.getWaypoint(WaypointType.HQ);
        this.focusWaypoint = this.getWaypoint(WaypointType.FOCUS);
        this.rallyWaypoint = this.getWaypoint(WaypointType.RALLY_POINT);
        this.endExit = Serializer.deserializeLoc(this.getConfig().getString("LOCATIONS.END_EXITS.END_EXIT"));
        this.endWorldExit = Serializer.deserializeLoc(this.getConfig().getString("LOCATIONS.END_EXITS.WORLD_EXIT"));
        new WaypointListener(this);
    }
}
