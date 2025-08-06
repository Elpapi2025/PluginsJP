package me.juanpiece.titan.modules.framework.commands;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Setter
@Getter
public abstract class Argument extends Module<Manager> {

    protected List<String> names;
    protected String permissible;
    protected boolean async;

    public Argument(CommandManager manager, List<String> names) {
        super(manager);
        this.names = names;
        this.async = false;
        this.permissible = null;
    }

    public abstract String usage();

    public void sendMessage(CommandSender sender, String... s) {
        for (String string : s) {
            sender.sendMessage(CC.t(string));
        }
    }

    public Integer getInt(String string) {
        try {

            return Integer.parseInt(string);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDouble(String string) {
        try {

            return Double.parseDouble(string);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(CC.t(usage()));
    }

    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, "woah command argument! - juanpiece..");
    }

    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}