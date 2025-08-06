package me.juanpiece.titan.modules.tablist;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.SneakyThrows;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.board.extra.AnimatedString;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.tablist.adapter.TitanTablist;
import me.juanpiece.titan.modules.tablist.extra.TablistSkin;
import me.juanpiece.titan.modules.tablist.listener.TablistListener;
import me.juanpiece.titan.modules.tablist.packet.TablistPacket;
import me.juanpiece.titan.modules.tablist.task.TablistTask;
import me.juanpiece.titan.utils.Logger;
import me.juanpiece.titan.utils.NameThreadFactory;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class TablistManager extends Manager {

    private final Map<UUID, Tablist> tablists;
    private final Map<String, TablistSkin> skins;
    private final Table<Integer, Integer, TablistSkin> defaultSkins;
    private final ScheduledExecutorService executor;

    private TablistAdapter adapter;
    private AnimatedString title;

    public TablistManager(HCF instance) {
        super(instance);

        this.tablists = new ConcurrentHashMap<>();
        this.skins = new ConcurrentHashMap<>();
        this.defaultSkins = HashBasedTable.create();
        this.adapter = new TitanTablist(this);
        this.title = new AnimatedString(this,
                getTablistConfig().getStringList("TITLE_CONFIG.CHANGES"),
                getTablistConfig().getInt("TITLE_CONFIG.CHANGER_TICKS"));

        this.load();

        this.executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("Titan - BoardThread"));
        this.executor.scheduleAtFixedRate(new TablistTask(this), 0L, 200L, TimeUnit.MILLISECONDS);

        new TablistListener(this);
    }

    private void load() {
        if (Bukkit.getServer().getMaxPlayers() <= 80) {
            Logger.print(
                    Logger.LINE_CONSOLE,
                    "- &dTitan HCF",
                    "- &cTablist will not work as intended.",
                    "- &cPlease increase slots to 80+",
                    Logger.LINE_CONSOLE
            );
        }

        for (String key : getTablistConfig().getConfigurationSection("SKINS").getKeys(false)) {
            String path = "SKINS." + key + ".";
            skins.put(key, new TablistSkin(getTablistConfig().getString(path + "VALUE"), getTablistConfig().getString(path + "SIGNATURE")));
        }

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 4; col++) {
                String skinName = (col == 0 ?
                        Config.TAB_SKIN_CACHE_LEFT.get(row) : col == 1 ?
                        Config.TAB_SKIN_CACHE_MIDDLE.get(row) : col == 2 ?
                        Config.TAB_SKIN_CACHE_RIGHT.get(row) :
                        Config.TAB_SKIN_CACHE_FAR_RIGHT.get(row));

                TablistSkin skin = skins.get(skinName);
                defaultSkins.put(col, row, skin);
            }
        }
    }

    @Override
    public void disable() {
        executor.shutdown();
    }

    @Override
    public void reload() {
        this.adapter = new TitanTablist(this);
        this.title = new AnimatedString(this,
                getTablistConfig().getStringList("TITLE_CONFIG.CHANGES"),
                getTablistConfig().getInt("TITLE_CONFIG.CHANGER_TICKS"));

        for (String key : getTablistConfig().getConfigurationSection("SKINS").getKeys(false)) {
            String path = "SKINS." + key + ".";
            skins.put(key, new TablistSkin(getTablistConfig().getString(path + "VALUE"), getTablistConfig().getString(path + "SIGNATURE")));
        }
    }

    @SneakyThrows
    public TablistPacket createPacket(Player player) {
        String path = "me.juanpiece.titan.modules.tablist.packet.type.TablistPacketV" + Utils.getNMSVer();
        return (TablistPacket) Class.forName(path).getConstructor(TablistManager.class, Player.class).newInstance(this, player);
    }
}