package me.juanpiece.titan.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CC {

    private static final Function<String, String> REPLACER;

    static {
        if (Utils.isModernVer()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

            REPLACER = s -> {
                for (Matcher matcher = pattern.matcher(s); matcher.find(); matcher = pattern.matcher(s)) {
                    String color = s.substring(matcher.start(), matcher.end());
                    s = s.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                }
                return ChatColor.translateAlternateColorCodes('&', s);
            };

        } else {
            REPLACER = s -> ChatColor.translateAlternateColorCodes('&', s);
        }
    }

    public static String LINE = t("&7&m-------------------------");

    public static String t(String t) {
        return REPLACER.apply(t);
    }

    public static List<String> t(List<String> t) {
        return t.stream().map(REPLACER).collect(Collectors.toList());
    }
}