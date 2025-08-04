package me.titan.core.modules.tablist;

import lombok.Getter;
import lombok.SneakyThrows;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.tablist.adapter.TitanTablist;
import me.titan.core.modules.tablist.extra.TablistSkin;
import me.titan.core.modules.tablist.listener.TablistListener;
import me.titan.core.modules.tablist.packet.TablistPacket;
import me.titan.core.modules.tablist.thread.TablistThread;
import me.titan.core.utils.Logger;
import me.titan.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TablistManager extends Manager {
    private final Map<UUID, Tablist> tablists;
    private final TablistAdapter adapter;
    private final Map<String, TablistSkin> skins;
    
    public TablistManager(HCF plugin) {
        super(plugin);
        this.tablists = new ConcurrentHashMap<>();
        this.skins = new ConcurrentHashMap<>();
        this.adapter = new TitanTablist(this);
        this.load();
        new TablistListener(this);
        new TablistThread(this);
    }
    
    private void load() {
        if (Bukkit.getServer().getMaxPlayers() <= 80) {
            Logger.print(Logger.LINE_CONSOLE, "- &dTitan HCF", "- &cTablist will not work as intended.", "- &cPlease increase slots to 80+", Logger.LINE_CONSOLE);
        }
        for (String s : this.getTablistConfig().getConfigurationSection("SKINS").getKeys(false)) {
            String path = "SKINS." + s + ".";
            this.skins.put(s, new TablistSkin(this.getTablistConfig().getString(path + "VALUE"), this.getTablistConfig().getString(path + "SIGNATURE")));
        }
    }

    @SneakyThrows
    public TablistPacket createPacket(Player player) {
        String skin = "me.titan.core.modules.tablist.packet.type.TablistPacketV" + Utils.getNMSVer();
        return (TablistPacket)Class.forName(skin).getConstructor(TablistManager.class, Player.class).newInstance(this, player);
    }
}