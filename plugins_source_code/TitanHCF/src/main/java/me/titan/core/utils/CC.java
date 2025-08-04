package me.titan.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CC {
    public static String LINE;
    
    static {
        CC.LINE = t("&7&m-------------------");
    }
    
    public static OfflinePlayer getPlayer(String offline) {
        Player player = Bukkit.getPlayer(offline);
        if (player != null) {
            return player;
        }
        return Bukkit.getOfflinePlayer(offline);
    }
    
    public static List<String> t(List<String> translate) {
        return translate.stream().map(CC::t).collect(Collectors.toList());
    }
    
    public static String t(String translate) {
        return ChatColor.translateAlternateColorCodes('&', translate);
    }
}
