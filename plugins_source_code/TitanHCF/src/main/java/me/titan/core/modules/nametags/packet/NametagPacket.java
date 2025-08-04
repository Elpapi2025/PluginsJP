package me.titan.core.modules.nametags.packet;

import lombok.Getter;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.nametags.NametagManager;
import me.titan.core.modules.nametags.extra.NameVisibility;
import org.bukkit.entity.Player;

@Getter
public abstract class NametagPacket extends HCFModule<NametagManager> {
    protected Player player;
    
    public abstract void addToTeam(Player player, String team);
    
    public NametagPacket(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
    }
    
    public abstract void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibilitt);
}
