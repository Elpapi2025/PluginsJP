package me.juanpiece.titan.modules.nametags.packet;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.nametags.NametagManager;
import me.juanpiece.titan.modules.nametags.extra.NameVisibility;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class NametagPacket extends Module<NametagManager> {

    protected final Player player;

    public NametagPacket(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
    }

    public abstract void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility);

    public abstract void addToTeam(Player target, String team);

    public abstract void delete();
}