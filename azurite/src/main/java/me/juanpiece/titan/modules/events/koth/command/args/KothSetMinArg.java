package me.juanpiece.titan.modules.events.koth.command.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.koth.Koth;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KothSetMinArg extends Argument {

    public KothSetMinArg(CommandManager manager) {
        super(
                manager,
                Arrays.asList(
                        "setminutes",
                        "setmin",
                        "setmins"
                )
        );
        this.setPermissible("titan.koth.setminutes");
    }

    @Override
    public String usage() {
        return getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETMIN.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        Koth koth = getInstance().getKothManager().getKoth(args[0]);
        Integer mins = getInt(args[1]);

        if (koth == null) {
            sendMessage(sender, getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND")
                    .replace("%koth%", args[0])
            );
            return;
        }

        if (mins == null) {
            sendMessage(sender, Config.NOT_VALID_NUMBER
                    .replace("%number%", args[1])
            );
            return;
        }

        koth.setMinutes((1000 * 60L) * mins);
        koth.save();

        if (koth.isActive() && koth.getCapturing() == null) {
            koth.setRemaining(koth.getMinutes());
        }

        sendMessage(sender, getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETMIN.UPDATED_MIN")
                .replace("%koth%", koth.getName())
                .replace("%min%", String.valueOf(mins))
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String string = args[args.length - 1];
            return getInstance().getKothManager().getKoths().values()
                    .stream()
                    .map(Koth::getName)
                    .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }

        return super.tabComplete(sender, args);
    }
}