package me.titan.core.modules.tablist.packet.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.titan.core.modules.tablist.Tablist;
import me.titan.core.modules.tablist.TablistManager;
import me.titan.core.modules.tablist.extra.TablistEntry;
import me.titan.core.modules.tablist.extra.TablistSkin;
import me.titan.core.modules.tablist.packet.TablistPacket;
import me.titan.core.utils.ReflectionUtils;
import me.titan.core.utils.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

public class TablistPacketV1_8_R3 extends TablistPacket {

    private static boolean LOADED;
    private static final Table<Integer, Integer, EntityPlayer> FAKE_PLAYERS;
    private String footer;
    private final int maxColumns;
    private static final Field FOOTER_FIELD;
    private String header;
    
    @Override
    public void update() {
        this.sendHeaderFooter();
        Tablist tablist = this.getManager().getAdapter().getInfo(this.player);
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                TablistEntry tablistEntry = tablist.getEntries(f, i);
                EntityPlayer entityPlayer = TablistPacketV1_8_R3.FAKE_PLAYERS.get(f, i);
                if (entityPlayer.ping != tablistEntry.getPing()) {
                    entityPlayer.ping = tablistEntry.getPing();
                    this.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, entityPlayer));
                }
                this.handleTeams(entityPlayer.getBukkitEntity(), tablistEntry.getText(), this.calcSlot(f, i));
            }
        }
    }
    
    private void sendPacket(Packet<?> packet) {
        PlayerConnection playerConnection = ((CraftPlayer)this.player).getHandle().playerConnection;
        if (playerConnection != null) {
            playerConnection.sendPacket(packet);
        }
    }
    
    static {
        FAKE_PLAYERS = HashBasedTable.create();
        FOOTER_FIELD = ReflectionUtils.accessField(PacketPlayOutPlayerListHeaderFooter.class, "b");
        TablistPacketV1_8_R3.LOADED = false;
    }
    
    public TablistPacketV1_8_R3(TablistManager tablistManager, Player player) {
        super(tablistManager, player);
        this.header = "";
        this.footer = "";
        this.maxColumns = ((Utils.getProtocolVersion(player) >= 47) ? 4 : 3);
        this.loadFakes();
        this.init();
    }
    
    private void sendHeaderFooter() {
        if (this.maxColumns == 3) {
            return;
        }
        String header = String.join("\n", this.getManager().getAdapter().getHeader(this.player));
        String footer = String.join("\n", this.getManager().getAdapter().getFooter(this.player));
        if (this.footer.equals(footer) && this.header.equals(header)) {
            return;
        }
        this.header = header;
        this.footer = footer;
        PacketPlayOutPlayerListHeaderFooter packetPlayOutPlayerListHeaderFooter = new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}"));
        try {
            TablistPacketV1_8_R3.FOOTER_FIELD.set(packetPlayOutPlayerListHeaderFooter, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}"));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.sendPacket(packetPlayOutPlayerListHeaderFooter);
    }
    
    private void loadFakes() {
        if (!TablistPacketV1_8_R3.LOADED) {
            TablistPacketV1_8_R3.LOADED = true;
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.getWorldServer(0);
            for (int i = 0; i < 20; ++i) {
                for (int f = 0; f < 4; ++f) {
                    String part = (f == 0) ? "LEFT" : ((f == 1) ? "MIDDLE" : ((f == 2) ? "RIGHT" : "FAR_RIGHT"));
                    List<String> partLines = this.getTablistConfig().getStringList(part);
                    String line = (i < partLines.size()) ? partLines.get(i).split(";")[0] : "";
                    GameProfile profile = new GameProfile(UUID.randomUUID(), this.getName(f, i));
                    EntityPlayer player = new EntityPlayer(minecraftServer, worldServer, profile, new PlayerInteractManager(worldServer));
                    TablistSkin skin = this.getManager().getSkins().get(line);
                    profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
                    TablistPacketV1_8_R3.FAKE_PLAYERS.put(f, i, player);
                }
            }
        }
    }
    
    private void init() {
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                EntityPlayer player = TablistPacketV1_8_R3.FAKE_PLAYERS.get(f, i);
                this.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player));
            }
        }
    }
}
