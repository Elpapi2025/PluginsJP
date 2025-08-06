package me.juanpiece.titan.modules.kits.commands.args;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Argument;
import me.juanpiece.titan.modules.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KitListArg extends Argument {

    public KitListArg(CommandManager manager) {
        super(
                manager,
                Collections.singletonList(
                        "list"
                )
        );
        this.setPermissible("titan.kit.list");
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

        for (String s : getLanguageConfig().getStringList("KIT_COMMAND.KIT_LIST.KITS_LIST")) {
            if (!s.equalsIgnoreCase("%kits%")) {
                sendMessage(sender, s);
                continue;
            }

            for (Kit kit : getInstance().getKitManager().getKits().values()) {
                sendMessage(sender, getLanguageConfig().getString("KIT_COMMAND.KIT_LIST.KITS_FORMAT")
                        .replace("%kit%", kit.getName())
                        .replace("%seconds%", String.valueOf(kit.getSeconds()))
                );
            }
        }
    }
}