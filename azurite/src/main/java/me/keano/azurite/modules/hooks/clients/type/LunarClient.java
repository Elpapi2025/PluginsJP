package me.keano.azurite.modules.hooks.clients.type;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderCreateNew;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderRemove;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.lunarclient.bukkitapi.serverrule.LunarClientAPIServerRule;
import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.hooks.clients.Client;
import me.keano.azurite.modules.hooks.clients.ClientHook;
import me.keano.azurite.modules.teams.claims.Claim;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.modules.waypoints.Waypoint;
import me.keano.azurite.modules.waypoints.WaypointType;
import me.keano.azurite.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class LunarClient extends Module<ClientHook> implements Client {

    public LunarClient(ClientHook manager) {
        super(manager);
    }

    @Override
    public void overrideNametags(Player target, Player viewer, List<String> tag) {
        LunarClientAPI.getInstance().overrideNametag(target, tag, viewer);
    }

    @Override
    public void sendWaypoint(Player player, Location location, Waypoint waypoint, UnaryOperator<String> replacer) {
        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                replacer.apply(waypoint.getName()),
                (waypoint.getWaypointType() == WaypointType.KOTH || waypoint.getWaypointType() == WaypointType.CONQUEST ? location.subtract(0, 1, 0) : location),
                Color.decode(replacer.apply(waypoint.getColor())).getRGB(),
                true,
                true
        ));
    }

    @Override
    public void removeWaypoint(Player player, Location location, Waypoint waypoint, UnaryOperator<String> replacer) {
        LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                replacer.apply(waypoint.getName()),
                (waypoint.getWaypointType() == WaypointType.KOTH || waypoint.getWaypointType() == WaypointType.CONQUEST ? location.subtract(0, 1, 0) : location),
                Color.decode(replacer.apply(waypoint.getColor())).getRGB(),
                true,
                true
        ));
    }

    @Override
    public void handleJoin(Player player) {
        if (getLunarConfig().getBoolean("LUNAR_API.FIX_1_8_HIT_DELAY")) {
            LunarClientAPIServerRule.setRule(ServerRule.LEGACY_COMBAT, true);
            LunarClientAPIServerRule.sendServerRule(player);
        }
    }

    @Override
    public void sendTeamViewer(Player player, PlayerTeam pt) {
        if (!getLunarConfig().getBoolean("LUNAR_API.TEAM_VIEWER")) return;

        for (Player member : pt.getOnlinePlayers(true)) {
            if (member.isDead()) continue;
            if (member.getWorld() != player.getWorld()) continue;
            if (member == player) continue;
            if (getInstance().getStaffManager().isStaffEnabled(member)) continue;

            pt.getTeamViewer().put(member.getUniqueId(), Utils.getTeamViewData(member));
            LCPacketTeammates packet = new LCPacketTeammates(pt.getLeader(), 1L, pt.getTeamViewer());
            LunarClientAPI.getInstance().sendTeammates(member, packet);
        }
    }

    @Override
    public void clearTeamViewer(Player player) {
        if (!getLunarConfig().getBoolean("LUNAR_API.TEAM_VIEWER")) return;
        // Send empty team viewer
        LCPacketTeammates packet = new LCPacketTeammates(player.getUniqueId(), 1L, new HashMap<>());
        LunarClientAPI.getInstance().sendTeammates(player, packet);
    }

    @Override
    public void giveStaffModules(Player player) {
        LunarClientAPI.getInstance().giveAllStaffModules(player);
    }

    @Override
    public void disableStaffModules(Player player) {
        LunarClientAPI.getInstance().disableAllStaffModules(player);
    }

    @Override
    public void sendBorderPacket(Player player, Claim claim, Color color) {
        LCPacketWorldBorderCreateNew packet = new LCPacketWorldBorderCreateNew(claim.getTeam().toString(),
                claim.getWorld().getUID().toString(), true, false, false,
                color.getRGB(), claim.getMinimumX(), claim.getMinimumZ(), claim.getMaximumX(), claim.getMaximumZ()
        );

        LunarClientAPI.getInstance().sendPacket(player, packet);
    }

    @Override
    public void sendRemoveBorderPacket(Player player, UUID id) {
        LCPacketWorldBorderRemove packet = new LCPacketWorldBorderRemove(id.toString());
        LunarClientAPI.getInstance().sendPacket(player, packet);
    }
}