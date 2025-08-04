package me.titan.core.modules.nametags;

import org.bukkit.entity.*;

public interface NametagAdapter {
    String getAndUpdate(Player from, Player to);
}