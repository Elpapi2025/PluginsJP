package com.ejemplo.economiaplugin.utils;

import org.bukkit.ChatColor;

public class ColorUtils {

    public static String translateColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}