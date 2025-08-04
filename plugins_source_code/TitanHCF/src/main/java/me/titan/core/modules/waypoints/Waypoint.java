package me.titan.core.modules.waypoints;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.function.*;
import com.lunarclient.bukkitapi.*;
import com.lunarclient.bukkitapi.object.*;

@Getter
@Setter
public class Waypoint {

    private final int color;
    private final String name;
    private final boolean enabled;
    private final WaypointType waypointType;
    
    public Waypoint(String name, WaypointType waypointType, int color, boolean enabled) {
        this.name = name;
        this.waypointType = waypointType;
        this.color = color;
        this.enabled = enabled;
    }
    
    public void send(Player player, Location location, UnaryOperator<String> unaryOperator) {
        if (location == null) {
            return;
        }
        if (!this.enabled) {
            return;
        }
        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(unaryOperator.apply(this.name), (this.waypointType == WaypointType.KOTH) ? location.subtract(0.0, 1.0, 0.0) : location, this.color, true, true));
    }
    
    public void remove(Player player, Location location, UnaryOperator<String> unaryOperator) {
        if (location == null) {
            return;
        }
        if (!this.enabled) {
            return;
        }
        LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(unaryOperator.apply(this.name), (this.waypointType == WaypointType.KOTH) ? location.subtract(0.0, 1.0, 0.0) : location, this.color, true, true));
    }
}
