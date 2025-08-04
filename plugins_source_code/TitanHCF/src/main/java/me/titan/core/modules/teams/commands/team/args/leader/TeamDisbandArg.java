package me.titan.core.modules.teams.commands.team.args.leader;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.claims.Claim;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.waypoints.WaypointManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.function.UnaryOperator;

public class TeamDisbandArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (!team.checkRole(player, Role.LEADER)) {
            this.sendMessage(sender, Config.INSUFFICIENT_ROLE.replaceAll("%role%", Role.LEADER.getName()));
            return;
        }
        for (Claim claim : team.getClaims()) {
            this.getInstance().getBalanceManager().giveBalance(player, this.getInstance().getTeamManager().getClaimManager().getPrice(claim, true));
        }
        WaypointManager waypointManager = this.getInstance().getWaypointManager();
        for (Player online : team.getOnlinePlayers()) {
            waypointManager.getHqWaypoint().remove(online, team.getHq(), UnaryOperator.identity());
            waypointManager.getRallyWaypoint().remove(online, team.getRallyPoint(), UnaryOperator.identity());
            if (team.getFocus() != null) {
                Team focusedTeam = team.getFocusedTeam();
                waypointManager.getFocusWaypoint().remove(online, focusedTeam.getHq(), w -> w.replaceAll("%team%", focusedTeam.getName()));
            }
        }
        team.broadcast(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_TEAM"));
        team.delete();
        for (Claim claim : team.getClaims()) {
            this.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
        }
        this.getInstance().getNametagManager().update();
        Bukkit.broadcastMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_DISBAND.DISBANDED_BROADCAST").replaceAll("%team%", team.getName()).replaceAll("%player%", player.getName()));
    }
    
    @Override
    public String usage() {
        return null;
    }
    
    public TeamDisbandArg(CommandManager manager) {
        super(manager, Collections.singletonList("disband"));
    }
}
