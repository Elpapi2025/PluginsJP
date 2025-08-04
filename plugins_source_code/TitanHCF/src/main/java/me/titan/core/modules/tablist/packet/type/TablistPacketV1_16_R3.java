package me.titan.core.modules.tablist.packet.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.titan.core.modules.tablist.Tablist;
import me.titan.core.modules.tablist.TablistManager;
import me.titan.core.modules.tablist.extra.TablistEntry;
import me.titan.core.modules.tablist.extra.TablistSkin;
import me.titan.core.modules.tablist.packet.TablistPacket;
import me.titan.core.utils.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TablistPacketV1_16_R3 extends TablistPacket {
    private static boolean LOADED;
    private String footer;
    private String header;
    private final int maxColumns;
    private static final Table<Integer, Integer, EntityPlayer> FAKE_PLAYERS;
    
    public void loadFakes() {
        if (!TablistPacketV1_16_R3.LOADED) {
            TablistPacketV1_16_R3.LOADED = true;
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.getWorlds().iterator().next();
            for (int i = 0; i < 20; ++i) {
                for (int f = 0; f < 4; ++f) {
                    String part = (f == 0) ? "LEFT" : ((f == 1) ? "MIDDLE" : ((f == 2) ? "RIGHT" : "FAR_RIGHT"));
                    String line = this.getTablistConfig().getStringList(part).get(i).split(";")[0];
                    GameProfile profile = new GameProfile(UUID.randomUUID(), this.getName(f, i));
                    EntityPlayer player = new EntityPlayer(minecraftServer, worldServer, profile, new PlayerInteractManager(worldServer));
                    TablistSkin skin = this.getManager().getSkins().get(line);
                    profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
                    TablistPacketV1_16_R3.FAKE_PLAYERS.put(f, i, player);
                }
            }
        }
    }
    
    private void sendHeaderFooter() {
        String header = String.join("\n", this.getManager().getAdapter().getHeader(this.player));
        String footer = String.join("\n", this.getManager().getAdapter().getFooter(this.player));
        if (this.footer.equals(header) && this.header.equals(header)) {
            return;
        }
        this.header = header;
        this.footer = footer;
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        packet.header = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}");
        packet.footer = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}");
        this.sendPacket(packet);
    }
    
    @Override
    public void update() {
        this.sendHeaderFooter();
        Tablist tablist = this.getManager().getAdapter().getInfo(this.player);
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                TablistEntry entry = tablist.getEntries(f, i);
                EntityPlayer player = TablistPacketV1_16_R3.FAKE_PLAYERS.get(f, i);
                if (player.ping != entry.getPing()) {
                    player.ping = entry.getPing();
                    this.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, player));
                }
                this.handleTeams(player.getBukkitEntity(), entry.getText(), this.calcSlot(f, i));
            }
        }
    }
    
    public TablistPacketV1_16_R3(TablistManager manager, Player player) {
        super(manager, player);
        this.header = "";
        this.footer = "";
        this.maxColumns = ((Utils.getProtocolVersion(player) >= 47) ? 4 : 3);
        this.loadFakes();
        this.init();
    }
    
    public void init() {
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                EntityPlayer player = TablistPacketV1_16_R3.FAKE_PLAYERS.get(f, i);
                this.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player));
            }
        }
    }
    
    static {
        FAKE_PLAYERS = HashBasedTable.create();
        TablistPacketV1_16_R3.LOADED = false;
    }
    
    private void sendPacket(Packet<?> packet) {
        PlayerConnection connection = ((CraftPlayer)this.player).getHandle().playerConnection;
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }
}
