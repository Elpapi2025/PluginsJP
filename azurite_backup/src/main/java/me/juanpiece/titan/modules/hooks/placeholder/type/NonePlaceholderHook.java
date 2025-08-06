package me.juanpiece.titan.modules.hooks.placeholder.type;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.hooks.placeholder.Placeholder;
import me.juanpiece.titan.modules.hooks.placeholder.PlaceholderHook;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NonePlaceholderHook extends Module<PlaceholderHook> implements Placeholder {

    public NonePlaceholderHook(PlaceholderHook manager) {
        super(manager);
    }

    @Override
    public String replace(Player player, String string) {
        return string;
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        return list;
    }
}