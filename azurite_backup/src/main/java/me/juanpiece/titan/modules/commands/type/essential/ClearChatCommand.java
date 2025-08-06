package me.juanpiece.titan.modules.commands.type.essential;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ClearChatCommand extends Command {

    private final String clearString; // we can cache instead of creating each time.

    public ClearChatCommand(CommandManager manager) {
        super(
                manager,
                "clearchat"
        );

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 300; i++) builder.append(CC.t("&7 &c &f &3 &b &9 &6 \n"));

        this.clearString = builder.toString();
        this.setPermissible("titan.clearchat");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "cc"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }

        Bukkit.broadcastMessage(clearString);
    }
}
