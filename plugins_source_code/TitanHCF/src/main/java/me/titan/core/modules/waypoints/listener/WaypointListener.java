package me.titan.core.modules.waypoints.listener;

import me.titan.core.modules.framework.*;
import me.titan.core.modules.waypoints.*;
import me.titan.core.modules.teams.*;
import me.titan.core.modules.events.koth.*;
import java.util.function.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import me.titan.core.modules.teams.type.*;
import me.titan.core.modules.teams.enums.*;
import org.bukkit.event.player.*;
import com.lunarclient.bukkitapi.nethandler.client.obj.*;
import com.lunarclient.bukkitapi.serverrule.*;

public class WaypointListener extends HCFModule<WaypointManager> {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        this.getManager().getSpawnWaypoint().send(player, player.getWorld().getSpawnLocation(), UnaryOperator.identity());
        this.getManager().getEndExitWaypoint().send(player, this.getManager().getEndExit(), UnaryOperator.identity());
        this.checkSystemTeams(player);
        this.sendServerRule(player);
        for (Koth koth : this.getInstance().getKothManager().getKoths().values()) {
            if (koth.getCaptureZone() == null) {
                continue;
            }
            if (!koth.isActive()) {
                continue;
            }
            this.getManager().getKothWaypoint().send(player, koth.getCaptureZone().getCenter(), kothstring -> kothstring.replaceAll("%name%", koth.getName()));
        }
        if (team != null) {
            this.getManager().getHqWaypoint().send(player, team.getHq(), UnaryOperator.identity());
            this.getManager().getRallyWaypoint().send(player, team.getRallyPoint(), UnaryOperator.identity());
            if (team.getFocus() != null) {
                Team teamfocused = team.getFocusedTeam();
                this.getManager().getFocusWaypoint().send(player, teamfocused.getHq(), llllllllllllllllIIlIlIIIIIlIlIll -> llllllllllllllllIIlIlIIIIIlIlIll.replaceAll("%team%", teamfocused.getName()));
            }
        }
    }
    
    private void checkSystemTeams(Player player) {
        for (Team team : this.getInstance().getTeamManager().getSystemTeams().values()) {
            if (team.getType() != TeamType.MOUNTAIN) {
                continue;
            }
            MountainTeam mountain = (MountainTeam)team;
            if (mountain.getMountainType() == MountainType.GLOWSTONE) {
                this.getManager().getGlowstoneWaypoint().send(player, mountain.getHq(), UnaryOperator.identity());
            }
            else {
                this.getManager().getOreMountainWaypoint().send(player, mountain.getHq(), UnaryOperator.identity());
            }
        }
    }
    
    public WaypointListener(WaypointManager waypointManager) {
        super(waypointManager);
    }
    
    @EventHandler
    public void onChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        this.getManager().getSpawnWaypoint().remove(player, event.getFrom().getSpawnLocation(), UnaryOperator.identity());
        this.getManager().getSpawnWaypoint().send(player, player.getWorld().getSpawnLocation(), UnaryOperator.identity());
    }
    
    private void sendServerRule(Player player) {
        if (this.getManager().isLunarMissing()) {
            return;
        }
        if (this.getLunarConfig().getBoolean("LUNAR_API.FIX_1_8_HIT_DELAY")) {
            LunarClientAPIServerRule.setRule(ServerRule.LEGACY_COMBAT, Boolean.TRUE);
            LunarClientAPIServerRule.sendServerRule(player);
        }
    }
}
