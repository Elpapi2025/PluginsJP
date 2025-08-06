package me.juanpiece.titan.modules.events.koth.command.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.events.koth.Koth;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KothListArg extends Argument {

    public KothListArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "list"
                )
        );
        this.setPermissible("titan.koth.list");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        for (String s : getLanguageConfig().getStringList("KOTH_COMMAND.KOTH_LIST.LIST")) {
            if (!s.equalsIgnoreCase("%koths%")) {
                sendMessage(sender, s);
                continue;
            }

            for (Koth koth : getInstance().getKothManager().getKoths().values()) {
                sendMessage(sender, getLanguageConfig().getString("KOTH_COMMAND.KOTH_LIST.FORMAT")
                        .replace("%color%", koth.getColor())
                        .replace("%koth%", koth.getName())
                        .replace("%mins%", String.valueOf(koth.getMinutes() / (60 * 1000L)))
                );
            }
        }
    }
}