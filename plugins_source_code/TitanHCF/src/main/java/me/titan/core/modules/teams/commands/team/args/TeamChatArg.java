package me.titan.core.modules.teams.commands.team.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.users.User;
import me.titan.core.modules.users.settings.TeamChatSetting;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamChatArg extends Argument {
    private final List<TeamChatSetting> chatSettings;
    
    public TeamChatArg(CommandManager manager) {
        super(manager, Collections.singletonList("chat"));
        this.chatSettings = new ArrayList<>(Arrays.asList(TeamChatSetting.values()));
        this.load();
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player)sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (team == null) {
            this.sendMessage(sender, Config.NOT_IN_TEAM);
            return;
        }
        user.setTeamChatSetting(this.getSetting(user.getTeamChatSetting()));
        player.sendMessage(this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", user.getTeamChatSetting().name().toLowerCase()));
    }
    
    private void load() {
        Iterator<TeamChatSetting> settings = this.chatSettings.iterator();
        while (settings.hasNext()) {
            TeamChatSetting setting = settings.next();
            if (setting == TeamChatSetting.PUBLIC) {
                continue;
            }
            if (this.manager.getConfig().getBoolean("CHAT_FORMAT." + setting.name() + "_CHAT.ENABLED")) {
                continue;
            }
            settings.remove();
        }
    }
    
    private TeamChatSetting getSetting(TeamChatSetting setting) {
        int i = this.chatSettings.indexOf(setting);
        if (i == this.chatSettings.size() - 1) {
            return this.chatSettings.get(0);
        }
        return this.chatSettings.get(i + 1);
    }
    
    @Override
    public String usage() {
        return null;
    }
}
