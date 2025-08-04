package me.titan.core.utils;

import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class Logger {
    public static final String LINE_CONSOLE;
    private static final SimpleDateFormat format;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.global;

    public static void error(String ... input) {
        Arrays.stream(input).forEach(logger::warning);
    }

    public static void print(String ... input) {
        for (String s : input) {
            Bukkit.getServer().getConsoleSender().sendMessage(CC.t(s));
        }
    }

    static {
        format = new SimpleDateFormat("dd MMMM hh:mm z");
        LINE_CONSOLE = CC.t("&b====================");
    }

    public Logger() {
    }

    public static void info(String ... input) {
        Arrays.stream(input).forEach(logger::info);
    }

    private static String convert(String input) {
        return input.equalsIgnoreCase("enabled") ? "Cargado" : "Guardado";
    }

    public static void state(String input, int managers, int teams, int users) {
        Logger.print(LINE_CONSOLE);
        Logger.print("- &dLoading Titan HCF...");
        Logger.print("- &d" + Logger.convert(input) + " &f" + managers + " &dmanagers.");
        Logger.print("- &d" + Logger.convert(input) + " &f" + teams + " &dteams.");
        Logger.print("- &d" + Logger.convert(input) + " &f" + users + " &dusers");
        Logger.print(LINE_CONSOLE);
        Logger.print("- &dAuthor&f: JuanPiece");
        Logger.print("- &dVersion&f: 1.0-BETA");
        Logger.print("- &dState&f: " + input);
        Logger.print(LINE_CONSOLE);
    }
}
