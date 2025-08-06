package me.juanpiece.titan.modules.board;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface BoardAdapter {

    String getTitle(Player player);

    List<String> getLines(Player player);

}