package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.teams.type.SafezoneTeam;
import me.titan.core.modules.timers.listeners.playertimers.HQTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamHQArg extends Argument {
    @Override
    public String usage() {
        return null;
    }
    
    public TeamHQArg(CommandManager manager) {
        super(manager, Arrays.asList("hq", "home"));
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        HQTimer timer = this.getInstance().getTimerManager().getHqTimer();
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        if (team.getHq() == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.NO_HQ"));
            return;
        }
        if (timer.hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.ALREADY_WARPING"));
            return;
        }
        if (this.getConfig().getBoolean("COMBAT_TIMER.HQ_TELEPORT") && this.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.COMBAT_TAGGED"));
            return;
        }
        if (this.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(player) && !this.getConfig().getBoolean("INVINCIBILITY.HQ_TELEPORT")) {
            player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.PVP_TIMER").replaceAll("%team%", team.getName()));
            return;
        }
        if (this.getInstance().getTimerManager().getPvpTimer().hasTimer(player) && !this.getConfig().getBoolean("PVP_TIMER.HQ_TELEPORT")) {
            player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.PVP_TIMER").replaceAll("%team%", team.getName()));
            return;
        }
        Team claimTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (!this.getConfig().getBoolean("HQ_TIMER.ALLOW_TP_OTHER_CLAIM") && claimTeam instanceof PlayerTeam && !((PlayerTeam)claimTeam).getPlayers().contains(player.getUniqueId())) {
            player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.NOT_ALLOWED"));
            return;
        }
        if (claimTeam instanceof SafezoneTeam && this.getConfig().getBoolean("HQ_TIMER.INSTANT_TP_SPAWN")) {
            timer.tpHq(player);
            return;
        }
        timer.applyTimer(player);
        player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_HQ.WARPING").replaceAll("%team%", team.getName()));
    }
}
