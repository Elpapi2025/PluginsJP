package me.titan.core.modules.nametags;

import lombok.Getter;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.nametags.packet.NametagPacket;
import org.bukkit.entity.Player;

@Getter
public class Nametag extends HCFModule<NametagManager> {
    private final Player player;
    private final NametagPacket packet;
    
    public Nametag(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
        this.packet = manager.createPacket(player);
    }
}
