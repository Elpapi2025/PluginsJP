package me.titan.core.modules.teams.extra;

import lombok.Getter;
import me.titan.core.modules.teams.type.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.titan.core.modules.users.settings.*;
import me.titan.core.modules.users.*;
import me.titan.core.utils.*;
import me.titan.core.modules.framework.*;
import me.titan.core.modules.teams.*;
import java.util.*;

@Getter
public class TeamSorting extends HCFModule<TeamManager> {
    private final List<PlayerTeam> lowestOnlineSorted;
    private final List<PlayerTeam> highestDTRSorted;
    private final List<PlayerTeam> teamTop;
    private final List<PlayerTeam> lowestDTRSorted;
    private final List<PlayerTeam> highestOnlineSorted;
    
    public List<PlayerTeam> getTeamTop() {
        return this.teamTop;
    }
    
    public void remove(PlayerTeam team) {
        this.teamTop.remove(team);
        this.lowestDTRSorted.remove(team);
        this.highestDTRSorted.remove(team);
        this.lowestOnlineSorted.remove(team);
        this.highestOnlineSorted.remove(team);
    }
    
    public List<PlayerTeam> getList(CommandSender sender) {
        User user = (sender instanceof Player) ? this.getInstance().getUserManager().getByUUID(((Player)sender).getUniqueId()) : null;
        TeamListSetting setting = (sender instanceof Player) ? user.getTeamListSetting() : null;
        return (sender instanceof Player) ? ((setting == TeamListSetting.LOWEST_DTR) ? this.lowestDTRSorted : ((setting == TeamListSetting.HIGHEST_DTR) ? this.highestDTRSorted : ((setting == TeamListSetting.ONLINE_HIGH) ? this.highestOnlineSorted : this.lowestOnlineSorted))) : this.lowestOnlineSorted;
    }
    
    public TeamListSetting getSetting(CommandSender sender) {
        User user = (sender instanceof Player) ? this.getInstance().getUserManager().getByUUID(((Player)sender).getUniqueId()) : null;
        return (sender instanceof Player) ? user.getTeamListSetting() : TeamListSetting.ONLINE_HIGH;
    }
    
    public TeamSorting(TeamManager manager) {
        super(manager);
        this.teamTop = new ArrayList<>();
        this.lowestDTRSorted = new ArrayList<>();
        this.highestDTRSorted = new ArrayList<>();
        this.highestOnlineSorted = new ArrayList<>();
        this.lowestOnlineSorted = new ArrayList<>();
        Tasks.executeScheduledAsync(manager, 600, this::sort);
    }
    
    private void sort() {
        List<PlayerTeam> teams = new ArrayList<>();
        for (Team team : this.getInstance().getTeamManager().getTeams().values()) {
            if (!(team instanceof PlayerTeam)) {
                continue;
            }
            teams.add((PlayerTeam)team);
        }
        teams.sort(Comparator.comparingInt(PlayerTeam::getPoints).reversed());
        if (!teams.equals(this.teamTop)) {
            this.getInstance().getNametagManager().update();
        }
        this.teamTop.clear();
        this.teamTop.addAll(teams);
        teams.removeIf(team -> team.getOnlinePlayers().size() <= 0);
        teams.sort(Comparator.comparingDouble(PlayerTeam::getDtr));
        this.lowestDTRSorted.clear();
        this.lowestDTRSorted.addAll(teams);
        teams.sort(Comparator.comparingDouble(PlayerTeam::getDtr).reversed());
        this.highestDTRSorted.clear();
        this.highestDTRSorted.addAll(teams);
        teams.sort(Comparator.comparingInt(PlayerTeam::getOnlinePlayersSize));
        this.lowestOnlineSorted.clear();
        this.lowestOnlineSorted.addAll(teams);
        teams.sort(Comparator.comparingInt(PlayerTeam::getOnlinePlayersSize).reversed());
        this.highestOnlineSorted.clear();
        this.highestOnlineSorted.addAll(teams);
    }
}
