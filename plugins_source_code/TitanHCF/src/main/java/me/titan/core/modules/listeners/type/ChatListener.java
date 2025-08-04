package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import me.titan.core.modules.teams.player.Role;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.users.User;
import me.titan.core.modules.users.settings.TeamChatSetting;
import me.titan.core.utils.CC;
import me.titan.core.utils.extra.Cooldown;
import me.titan.core.utils.extra.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.stream.Collectors;

public class ChatListener extends HCFModule<ListenerManager> {
    private final String coLeaderChatFormat;
    private final String teamChatFormat;
    private final String officerChatFormat;
    private final Cooldown chatCooldown;
    private final List<String> deniedChatMessages;
    private final Map<String, TeamChatSetting> shortcuts;
    private final String allyChatFormat;
    
    private void load() {
        this.deniedChatMessages.addAll(this.getConfig().getStringList("CHAT_FORMAT.DENIED_WORDS").stream().map(String::toUpperCase).collect(Collectors.toList()));
        TeamChatSetting[] settings = TeamChatSetting.values();
        for(TeamChatSetting setting : settings) {
            if (setting != TeamChatSetting.PUBLIC) {
                String s = this.getConfig().getString("CHAT_FORMAT." + setting.name() + "_CHAT.SHORTCUT");
                if (!s.isEmpty()) {
                    this.shortcuts.put(s, setting);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        String prefix = this.getInstance().getUserManager().getPrefix(player);
        String message = event.getMessage();
        String kTop = "";
        String fTop = "";
        Pair<TeamChatSetting, String> pair = this.getShortcut(message);
        TeamChatSetting chatSetting = user.getTeamChatSetting();
        String pPrefix = CC.t(this.getInstance().getRankManager().getRankPrefix(player));
        String pSuffix = CC.t(this.getInstance().getRankManager().getRankSuffix(player));
        String pColor = CC.t(this.getInstance().getRankManager().getRankColor(player));
        String pTag = CC.t(this.getInstance().getTagManager().getTag(player));
        boolean bypass = player.hasPermission("titan.chat.bypass");
        if (team != null) {
            if (pair != null) {
                chatSetting = pair.getKey();
                message = pair.getValue();
            }
            String position = team.getTeamPosition();
            fTop = ((position == null) ? "" : this.getConfig().getString("CHAT_FORMAT.FTOP_FORMAT").replace("%ftop%", position));
        }
        if (prefix != null) {
            kTop = this.getConfig().getString("CHAT_FORMAT.KILL_TOP_FORMAT").replace("%killtop%", prefix);
        }
        if (event.isCancelled() && chatSetting == TeamChatSetting.PUBLIC) {
            return;
        }
        if (!player.hasPermission("titan.profanity.bypass") && chatSetting == TeamChatSetting.PUBLIC) {
            for (String s : this.deniedChatMessages) {
                if (!message.toUpperCase().contains(s)) {
                    continue;
                }
                event.setCancelled(true);
                player.sendMessage(this.getLanguageConfig().getString("CHAT_LISTENER.FORBIDDEN_MESSAGE"));
                return;
            }
        }
        if (this.chatCooldown.hasCooldown(player) && !bypass) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("CHAT_LISTENER.COOLDOWN").replaceAll("%seconds%", this.chatCooldown.getRemaining(player)));
            return;
        }
        if (!bypass && chatSetting == TeamChatSetting.PUBLIC) {
            this.chatCooldown.applyCooldown(player, this.getConfig().getInt("CHAT_FORMAT.COOLDOWN"));
        }
        event.setCancelled(true);
        switch (chatSetting) {
            case PUBLIC: {
                String msg = "";
                for (Player online : event.getRecipients()) {
                    User userRecipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                    if (!userRecipient.isPublicChat()) {
                        continue;
                    }
                    if (team == null) {
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    else {
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()).replace("%team%", team.getDisplayName(online)), message);
                        online.sendMessage(msg);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case TEAM: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    msg = String.format(this.teamChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case ALLY: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    msg = String.format(this.allyChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                for (UUID allie : team.getAllies()) {
                    PlayerTeam teamAllie = this.getInstance().getTeamManager().getPlayerTeam(allie);
                    if (teamAllie == null) {
                        continue;
                    }
                    for (Player allieOnline : teamAllie.getOnlinePlayers()) {
                        allieOnline.sendMessage(msg);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case OFFICER: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    if (!team.checkRole(teamOnline, Role.CAPTAIN)) {
                        continue;
                    }
                    msg = String.format(this.officerChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
            case CO_LEADER: {
                String msg = "";
                if (team == null) {
                    user.setTeamChatSetting(TeamChatSetting.PUBLIC);
                    user.save();
                    for (Player online : event.getRecipients()) {
                        User recipient = this.getInstance().getUserManager().getByUUID(online.getUniqueId());
                        if (!recipient.isPublicChat()) {
                            continue;
                        }
                        msg = String.format(this.getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%tag%", pTag).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()), message);
                        online.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                    return;
                }
                for (Player teamOnline : team.getOnlinePlayers()) {
                    if (!team.checkRole(teamOnline, Role.CO_LEADER)) {
                        continue;
                    }
                    msg = String.format(this.coLeaderChatFormat.replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%player%", player.getName()), message);
                    teamOnline.sendMessage(msg);
                }
                Bukkit.getConsoleSender().sendMessage(msg);
                break;
            }
        }
    }
    
    public ChatListener(ListenerManager manager) {
        super(manager);
        this.chatCooldown = new Cooldown(manager);
        this.shortcuts = new HashMap<>();
        this.deniedChatMessages = new ArrayList<>();
        this.teamChatFormat = this.getConfig().getString("CHAT_FORMAT.TEAM_CHAT.FORMAT");
        this.allyChatFormat = this.getConfig().getString("CHAT_FORMAT.ALLY_CHAT.FORMAT");
        this.officerChatFormat = this.getConfig().getString("CHAT_FORMAT.OFFICER_CHAT.FORMAT");
        this.coLeaderChatFormat = this.getConfig().getString("CHAT_FORMAT.CO_LEADER_CHAT.FORMAT");
        this.load();
    }
    
    private Pair<TeamChatSetting, String> getShortcut(String shortcut) {
        if (shortcut.isEmpty()) {
            return null;
        }
        TeamChatSetting setting = this.shortcuts.get(String.valueOf(shortcut.charAt(0)));
        if (setting == null) {
            return null;
        }
        for (String s : this.shortcuts.keySet()) {
            shortcut = shortcut.replaceAll(s, "");
        }
        return new Pair<>(setting, shortcut);
    }
}
