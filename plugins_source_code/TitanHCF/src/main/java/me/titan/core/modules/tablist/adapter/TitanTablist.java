package me.titan.core.modules.tablist.adapter;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.tablist.Tablist;
import me.titan.core.modules.tablist.TablistAdapter;
import me.titan.core.modules.tablist.TablistManager;
import me.titan.core.modules.tablist.extra.TablistEntry;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.player.Member;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.users.User;
import me.titan.core.modules.users.settings.TeamListSetting;
import me.titan.core.utils.Formatter;
import me.titan.core.utils.Utils;
import me.titan.core.utils.extra.FastReplaceString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class TitanTablist extends HCFModule<TablistManager> implements TablistAdapter {
    private final List<String> farRightTablist;
    private final List<String> leftTablist;
    private final List<String> middleTablist;
    private final List<String> rightTablist;
    private final String[] footer;
    private final String[] header;
    
    @Override
    public Tablist getInfo(Player player) {
        Tablist tablist = this.manager.getTablists().get(player.getUniqueId());
        User user = this.instance.getUserManager().getByUUID(player.getUniqueId());
        Team team = this.instance.getTeamManager().getClaimManager().getTeam(player.getLocation());
        Location location = player.getLocation();
        List<PlayerTeam> teams = this.instance.getTeamManager().getTeamSorting().getList(player);
        PlayerTeam playerTeam = this.instance.getTeamManager().getByPlayer(player.getUniqueId());
        for (int i = 0; i < 20; ++i) {
            tablist.add(0, i, this.leftTablist.get(i));
            tablist.add(1, i, this.middleTablist.get(i));
            tablist.add(2, i, this.rightTablist.get(i));
            tablist.add(3, i, this.farRightTablist.get(i));
        }
        for (TablistEntry entry : tablist.getEntries().values()) {
            String text = entry.getText();
            if (text.isEmpty()) {
                continue;
            }
            if (playerTeam != null) {
                List<String> teamFormat = this.getTablistConfig().getStringList("TEAM_FORMAT.IN_TEAM");
                List<Member> teamMembers = playerTeam.getOnlineMembers();
                teamMembers.sort((x, y) -> y.getRole().ordinal() - x.getRole().ordinal());
                text = Utils.fastReplace(text, "%team%", playerTeam.getDisplayName(player));
                for (int i = 0; i < teamFormat.size(); ++i) {
                    String dtrColor = new FastReplaceString(teamFormat.get(i)).replaceAll("%team-dtr-color%", playerTeam.getDtrColor()).replaceAll("%team-dtr%", playerTeam.getDtrString()).replaceAll("%team-dtr-symbol%", playerTeam.getDtrSymbol()).replaceAll("%team-hq%", playerTeam.getHQFormatted()).endResult();
                    text = Utils.fastReplace(text, "%teaminfo-" + i + "%", dtrColor);
                }
                for (int i = 0; i < teamMembers.size(); ++i) {
                    Member member = teamMembers.get(i);
                    String memberFormat = new FastReplaceString(this.getTablistConfig().getString("TEAM_FORMAT.MEMBER_FORMAT")).replaceAll("%role%", member.getAsterisk()).replaceAll("%player%", Bukkit.getPlayer(member.getUniqueID()).getName()).endResult();
                    text = Utils.fastReplace(text, "%member-" + i + "%", memberFormat);
                }
            }
            else {
                List<String> noTeamFormat = this.getTablistConfig().getStringList("TEAM_FORMAT.NO_TEAM");
                text = Utils.fastReplace(text, "%team%", "");
                for (int i = 0; i < noTeamFormat.size(); ++i) {
                    String tt = noTeamFormat.get(i);
                    text = Utils.fastReplace(text, "%teaminfo-" + i + "%", tt);
                }
            }
            for (int i = 0; i < teams.size() && i != 19; ++i) {
                TeamListSetting setting = user.getTeamListSetting();
                PlayerTeam targetTeam = teams.get(i);
                String dtr = setting.name().contains("DTR") ? "DTR" : "ONLINE";
                String listFormat = new FastReplaceString(this.getTablistConfig().getString("TEAM_FORMAT.LIST_FORMAT." + dtr)).replaceAll("%team-name%", targetTeam.getDisplayName(player)).replaceAll("%dtr-color%", targetTeam.getDtrColor()).replaceAll("%dtr%", targetTeam.getDtrString()).replaceAll("%dtr-symbol%", targetTeam.getDtrSymbol()).replaceAll("%max-dtr%", Formatter.formatDtr(targetTeam.getMaxDtr())).replaceAll("%team-online%", String.valueOf(targetTeam.getOnlinePlayers().size())).replaceAll("%team-max-online%", String.valueOf(targetTeam.getPlayers().size())).endResult();
                text = Utils.fastReplace(text, "%team-" + i + "%", listFormat);
            }
            if (text.contains("%team-") || text.contains("%teaminfo-") || text.contains("%member-")) {
                entry.setText("");
            }
            else {
                entry.setText(new FastReplaceString(text).replaceAll("%lives%", String.valueOf(user.getLives())).replaceAll("%kills%", String.valueOf(user.getKills())).replaceAll("%deaths%", String.valueOf(user.getDeaths())).replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(Bukkit.getMaxPlayers())).replaceAll("%location%", location.getBlockX() + ", " + location.getBlockZ()).replaceAll("%claim%", team.getDisplayName(player)).endResult());
            }
        }
        return tablist;
    }
    
    private void load() {
        for (int i = 0; i < 20; ++i) {
            String[] left = this.leftTablist.get(i).split(";");
            this.leftTablist.set(i, (left.length == 1) ? "" : left[1]);
            String[] middle = this.middleTablist.get(i).split(";");
            this.middleTablist.set(i, (middle.length == 1) ? "" : middle[1]);
            String[] right = this.rightTablist.get(i).split(";");
            this.rightTablist.set(i, (right.length == 1) ? "" : right[1]);
            String[] farRight = this.farRightTablist.get(i).split(";");
            this.farRightTablist.set(i, (farRight.length == 1) ? "" : farRight[1]);
        }
    }
    
    @Override
    public String[] getFooter(Player player) {
        String[] footer = this.footer.clone();
        for (int i = 0; i < footer.length; ++i) {
            String text = footer[i];
            footer[i] = text.replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
        return footer;
    }
    
    public TitanTablist(TablistManager manager) {
        super(manager);
        this.header = this.getTablistConfig().getStringList("TABLIST_INFO.HEADER").toArray(new String[0]);
        this.footer = this.getTablistConfig().getStringList("TABLIST_INFO.FOOTER").toArray(new String[0]);
        this.leftTablist = this.getTablistConfig().getStringList("LEFT");
        this.middleTablist = this.getTablistConfig().getStringList("MIDDLE");
        this.rightTablist = this.getTablistConfig().getStringList("RIGHT");
        this.farRightTablist = this.getTablistConfig().getStringList("FAR_RIGHT");
        this.load();
    }
    
    @Override
    public String[] getHeader(Player player) {
        String[] header = this.header.clone();
        for (int i = 0; i < header.length; ++i) {
            String text = header[i];
            header[i] = text.replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
        return header;
    }
}