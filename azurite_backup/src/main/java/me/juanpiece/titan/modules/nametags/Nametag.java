package me.juanpiece.titan.modules.nametags;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.nametags.packet.NametagPacket;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Nametag extends Module<NametagManager> {

    private final Player player;
    private final NametagPacket packet;
    private final int protocolVersion;

    public Nametag(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
        this.packet = manager.createPacket(player);
        this.protocolVersion = Utils.getProtocolVersion(player);
    }

    public void delete() {
        packet.delete();
    }
}