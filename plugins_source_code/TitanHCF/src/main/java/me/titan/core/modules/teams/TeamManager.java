package me.titan.core.modules.teams;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.staff.StaffManager;
import me.titan.core.modules.teams.claims.ClaimManager;
import me.titan.core.modules.teams.enums.TeamType;
import me.titan.core.modules.teams.extra.TeamSorting;
import me.titan.core.modules.teams.player.Member;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.*;
import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.users.User;
import me.titan.core.utils.CC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TeamManager extends Manager {
    private final TeamSorting teamSorting;
    private final Map<UUID, Team> systemTeams;
    private final Map<String, Team> stringTeams;
    private final Map<UUID, PlayerTeam> playerTeams;
    private final ClaimManager claimManager;
    private final Map<UUID, Team> teams;
    
    public TeamManager(HCF plugin) {
        super(plugin);
        this.teams = new ConcurrentHashMap<>();
        this.systemTeams = new ConcurrentHashMap<>();
        this.playerTeams = new ConcurrentHashMap<>();
        this.stringTeams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.claimManager = new ClaimManager(this);
        this.teamSorting = new TeamSorting(this);
    }
    
    public PlayerTeam getByPlayer(UUID uuid) {
        return this.playerTeams.get(uuid);
    }
    
    public PlayerTeam createTeam(String name, Player player) {
        PlayerTeam team = new PlayerTeam(this, name, player.getUniqueId());
        team.getMembers().add(new Member(player.getUniqueId(), Role.LEADER));
        team.getPlayers().add(player.getUniqueId());
        return team;
    }
    
    public boolean canHit(Player from, Player to, boolean online) {
        PlayerTeam fromTeam = this.getByPlayer(from.getUniqueId());
        PlayerTeam toTeam = this.getByPlayer(to.getUniqueId());
        TimerManager timerManager = this.getInstance().getTimerManager();
        StaffManager staffManager = this.getInstance().getStaffManager();
        if (staffManager.isStaffEnabled(from) || staffManager.isVanished(from)) {
            return false;
        }
        if (staffManager.isStaffEnabled(to) || staffManager.isVanished(to)) {
            return false;
        }
        if (staffManager.isFrozen(from)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.HIT_DENIED"));
            return false;
        }
        else if (staffManager.isFrozen(to)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.HIT_FROZEN").replace("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getSotwTimer().isActive() && !timerManager.getSotwTimer().getEnabled().contains(to.getUniqueId())) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("SOTW_TIMER.DAMAGED_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getSotwTimer().isActive() && !timerManager.getSotwTimer().getEnabled().contains(from.getUniqueId())) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("SOTW_TIMER.DAMAGER_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getPvpTimer().hasTimer(from)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("PVP_TIMER.DAMAGER_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getPvpTimer().hasTimer(to)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("PVP_TIMER.DAMAGED_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getInvincibilityTimer().hasTimer(from)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("INVINCIBILITY.DAMAGER_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (timerManager.getInvincibilityTimer().hasTimer(to)) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("INVINCIBILITY.DAMAGED_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else if (this.getClaimManager().getTeam(from.getLocation()).getType() == TeamType.SAFEZONE) {
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.DAMAGER_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
        else {
            if (this.getClaimManager().getTeam(to.getLocation()).getType() != TeamType.SAFEZONE) {
                if (fromTeam != null && toTeam != null) {
                    if (fromTeam == toTeam) {
                        if (!online) {
                            return false;
                        }
                        from.sendMessage(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.MEMBER_HURT").replaceAll("%player%", to.getName()).replaceAll("%role%", toTeam.getMember(to.getUniqueId()).getAsterisk()));
                        return false;
                    }
                    else if (fromTeam.getAllies().contains(toTeam.getUniqueID())) {
                        if (!online) {
                            return false;
                        }
                        from.sendMessage(this.getLanguageConfig().getString("PLAYER_TEAM_LISTENER.ALLY_HURT").replaceAll("%player%", to.getName()));
                        return false;
                    }
                }
                return true;
            }
            if (!online) {
                return false;
            }
            from.sendMessage(this.getLanguageConfig().getString("TEAM_LISTENER.DAMAGED_ATTACK").replaceAll("%player%", to.getName()));
            return false;
        }
    }
    
    public Team getTeam(String name) {
        return this.stringTeams.get(name);
    }

    public Team getTeam(final UUID uuid) {
        return this.teams.get(uuid);
    }
    
    public PlayerTeam getPlayerTeam(String name) {
        Team team = this.stringTeams.get(name);
        return (team instanceof PlayerTeam) ? ((PlayerTeam)team) : null;
    }
    
    public PlayerTeam getByPlayerOrTeam(String name) {
        Team team = this.stringTeams.get(name);
        if (team instanceof PlayerTeam) {
            return (PlayerTeam)team;
        }
        return this.playerTeams.get(CC.getPlayer(name).getUniqueId());
    }
    
    public MountainTeam getMountainTeam(String name) {
        Team team = this.getTeam(name);
        return (team instanceof MountainTeam) ? ((MountainTeam)team) : null;
    }
    
    public Team createTeam(String name, TeamType type) {
        switch (type) {
            case SAFEZONE: {
                return new SafezoneTeam(this, name);
            }
            case ROAD: {
                return new RoadTeam(this, name);
            }
            case EVENT: {
                return new EventTeam(this, name);
            }
            case CITADEL: {
                return new CitadelTeam(this, name);
            }
            default: {
                return null;
            }
        }
    }
    
    public CitadelTeam getCitadelTeam(String name) {
        Team team = this.getTeam(name);
        return (team instanceof CitadelTeam) ? ((CitadelTeam)team) : null;
    }
    
    public void handleDeath(Player from, Player to) {
        User user = this.getInstance().getUserManager().getByUUID(from.getUniqueId());
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(from.getUniqueId());
        int deaths = this.getTeamConfig().getInt("TEAM_POINTS.DEATH");
        int kills = this.getTeamConfig().getInt("TEAM_POINTS.KILLS");
        user.setDeaths(user.getDeaths() + 1);
        user.setKillstreak(0);
        user.save();
        if (team != null) {
            team.setPoints(Math.max(team.getPoints() - deaths, 0));
            team.setDeaths(team.getDeaths() + 1);
            team.setDtr(team.getDtr() - Config.DTR_TAKE_DEATH);
            this.getInstance().getTimerManager().getTeamRegenTimer().applyTimer(team);
            team.save();
            for (String s : this.getLanguageConfig().getStringList("PLAYER_TEAM_LISTENER.MEMBER_DEATH")) {
                team.broadcast(s.replaceAll("%player%", from.getName()).replaceAll("%dtr%", team.getDtrString()));
            }
            team.broadcast(this.getLanguageConfig().getString("DEATH_LISTENER.TEAMS_MESSAGES.LOST_POINTS").replaceAll("%points%", String.valueOf(deaths)).replaceAll("%player%", from.getName()));
        }
        if (to != null && to != from) {
            User killedUser = this.getInstance().getUserManager().getByUUID(to.getUniqueId());
            PlayerTeam killedTeam = this.getInstance().getTeamManager().getByPlayer(to.getUniqueId());
            killedUser.setKills(killedUser.getKills() + 1);
            killedUser.setKillstreak(killedUser.getKillstreak() + 1);
            if (killedUser.getKillstreak() > killedUser.getHighestKillstreak()) {
                user.setHighestKillstreak(user.getKillstreak());
            }
            killedUser.save();
            if (killedTeam != null) {
                killedTeam.setPoints(killedTeam.getPoints() + kills);
                killedTeam.setKills(killedTeam.getKills() + 1);
                killedTeam.save();
                killedTeam.broadcast(this.getLanguageConfig().getString("DEATH_LISTENER.TEAMS_MESSAGES.GAINED_POINTS").replaceAll("%points%", String.valueOf(kills)).replaceAll("%player%", from.getName()));
            }
        }
    }
    
    @Override
    public void disable() {
        this.getClaimManager().getWarzoneTeam().clearWebs();
    }
    
    public PlayerTeam getPlayerTeam(UUID player) {
        return this.playerTeams.get(player);
    }

    public void addPlayerToTeamMap(UUID playerUUID, PlayerTeam team) {
        this.playerTeams.put(playerUUID, team);
    }

    public void removePlayerFromTeamMap(UUID playerUUID) {
        this.playerTeams.remove(playerUUID);
    }
    
    public boolean canBuild(Player player, Location location) {
        Team team = this.claimManager.getTeam(location);
        if (player.hasPermission("titan.gamemode") && player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        if (player.getWorld().getEnvironment() == World.Environment.THE_END) {
            return false;
        }
        if (location.getBlock().getType().name().contains("SIGN")) {
            return true;
        }
        if (team instanceof PlayerTeam) {
            PlayerTeam playerTeam = (PlayerTeam)team;
            if (playerTeam.getPlayers().contains(player.getUniqueId())) {
                return true;
            }
            if (playerTeam.isRaidable()) {
                return true;
            }
        }
        if (team instanceof WarzoneTeam) {
            WarzoneTeam warzoneTeam = (WarzoneTeam)team;
            if (warzoneTeam.canBreak(location)) {
                return true;
            }
        }
        return team instanceof WildernessTeam;
    }
}
