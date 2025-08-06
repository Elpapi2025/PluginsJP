package me.juanpiece.titan.utils;

import org.bukkit.Bukkit;

public class Logger {

    public static final String LINE_CONSOLE = CC.t("&4=========================");

    public static void state(String state, int managers, int teams, int users, int kits, int koths) {
        print(LINE_CONSOLE);
        print("- &cLoading Titan HCF...");
        print("- &c" + convert(state) + " &f" + managers + " &cmanagers.");
        print("- &c" + convert(state) + " &f" + teams + " &cteams.");
        print("- &c" + convert(state) + " &f" + users + " &cusers");
        print("- &c" + convert(state) + " &f" + koths + " &ckoths");
        print("- &c" + convert(state) + " &f" + kits + " &ckits");
        print(LINE_CONSOLE);
        print("- &cAuthor&f: juanpiece");
        print("- &cVersion&f: " + Bukkit.getPluginManager().getPlugin("Titan").getDescription().getVersion());
        print("- &cState&f: " + state);
        print(LINE_CONSOLE);
    }

    public static void print(String... message) {
        for (String s : message) {
            Bukkit.getServer().getConsoleSender().sendMessage(CC.t(s));
        }
    }

    private static String convert(String string) {
        return string.equalsIgnoreCase("enabled") ? "Loaded" : "Saved";
    }
}