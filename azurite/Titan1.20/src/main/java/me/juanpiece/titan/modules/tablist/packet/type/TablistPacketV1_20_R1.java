package me.juanpiece.titan.modules.tablist.packet.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.juanpiece.titan.modules.tablist.Tablist;
import me.juanpiece.titan.modules.tablist.TablistManager;
import me.juanpiece.titan.modules.tablist.extra.TablistEntry;
import me.juanpiece.titan.modules.tablist.extra.TablistSkin;
import me.juanpiece.titan.modules.tablist.packet.TablistPacket;
import me.juanpiece.titan.utils.Utils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("unused")
public class TablistPacketV1_20_R1 extends TablistPacket {

    private final Table<Integer, Integer, EntityPlayer> fakePlayers;
    private final int maxColumns;

    // Cache them to see if we don't have to send a packet again.
    private String header;
    private String footer;

    public TablistPacketV1_20_R1(TablistManager manager, Player player) {
        super(manager, player);

        this.fakePlayers = HashBasedTable.create();
        this.header = "";
        this.footer = "";
        this.maxColumns = (Utils.getProtocolVersion(player) <= 5 ? 3 : 4);

        this.loadFakes();
        this.init();
    }

    @Override
    public void update() {
        this.sendHeaderFooter();

        Tablist tablist = getManager().getAdapter().getInfo(player);

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                TablistEntry entry = tablist.getEntry(col, row);
                TablistSkin newSkin = entry.getSkin();
                EntityPlayer fake = fakePlayers.get(col, row);

                if (fake == null) continue;

                if (fake.f != entry.getPing()) {
                    fake.f = entry.getPing();
                    sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.e, fake));
                }

                if (newSkin == null) {
                    newSkin = getManager().getDefaultSkins().get(col, row);
                }

                updateSkin(fake, newSkin);
                handleTeams(fake.getBukkitEntity(), entry.getText(), calcSlot(col, row));
            }
        }
    }

    private void updateSkin(EntityPlayer fake, TablistSkin skin) {
        GameProfile profile = fake.fM();
        Property old = profile.getProperties().get("textures").stream().findFirst().orElse(null);

        // Verify if we should update
        if (old != null && old.getValue().equals(skin.getValue()) && old.getSignature().equals(skin.getSignature())) {
            return;
        }

        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        sendPacket(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(profile.getId())));
        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, fake));
        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.d, fake));
    }

    private void loadFakes() {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer worldServer = server.F().iterator().next();

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 4; col++) {
                GameProfile profile = new GameProfile(UUID.randomUUID(), getName(col, row));
                EntityPlayer fake = new EntityPlayer(server, worldServer, profile);
                TablistSkin skin = getManager().getDefaultSkins().get(col, row);
                profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
                fakePlayers.put(col, row, fake);
            }
        }
    }

    private void init() {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                EntityPlayer fake = fakePlayers.get(col, row);
                if (fake == null) continue;
                // Some reason u have to send an add_packet and then following it with update display name.
                sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, fake));
                sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.d, fake));
            }
        }
    }

    private void sendPacket(Packet<?> packet) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().c;
        connection.a(packet);
    }

    private void sendHeaderFooter() {
        String headerJoined = String.join("\n", getManager().getAdapter().getHeader(player));
        String footerJoined = String.join("\n", getManager().getAdapter().getFooter(player));

        // Refrain sending a packet again if same.
        if (footer.equals(headerJoined) && header.equals(headerJoined)) return;

        this.header = headerJoined;
        this.footer = footerJoined;

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(
                CraftChatMessage.fromJSONOrString(headerJoined, true),
                CraftChatMessage.fromJSONOrString(footerJoined, true)
        );

        sendPacket(packet);
    }
}