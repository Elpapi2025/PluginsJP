package me.titan.core.modules.tablist;

import org.bukkit.entity.Player;

public interface TablistAdapter {
    String[] getHeader(Player player);

    Tablist getInfo(Player player);

    String[] getFooter(Player player);
}