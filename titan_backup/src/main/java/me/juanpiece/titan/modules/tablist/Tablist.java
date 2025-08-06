package me.juanpiece.titan.modules.tablist;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.tablist.extra.TablistEntry;
import me.juanpiece.titan.modules.tablist.extra.TablistSkin;
import me.juanpiece.titan.modules.tablist.packet.TablistPacket;
import me.juanpiece.titan.utils.extra.Pair;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Tablist extends Module<TablistManager> {

    public static final TablistEntry EMPTY_ENTRY = new TablistEntry("", -1);

    private final Map<Pair<Integer, Integer>, TablistEntry> entries;
    private final TablistPacket packet;

    public Tablist(TablistManager manager, Player player) {
        super(manager);
        this.entries = new ConcurrentHashMap<>();
        this.packet = manager.createPacket(player);
        manager.getTablists().put(player.getUniqueId(), this);
    }

    public void update() {
        packet.update(); // Updating will get the entries again.
    }

    public TablistEntry getEntry(int col, int row) {
        TablistEntry entry = entries.get(new Pair<>(col, row));

        if (entry == null) {
            return EMPTY_ENTRY;
        }

        return entry;
    }

    public void add(int col, int row, String text) {
        text = getInstance().getPlaceholderHook().replace(packet.getPlayer(), text);
        entries.put(new Pair<>(col, row), new TablistEntry(text, Config.TABLIST_PING));
    }

    public void add(int col, int row, String text, int ping) {
        text = getInstance().getPlaceholderHook().replace(packet.getPlayer(), text);
        entries.put(new Pair<>(col, row), new TablistEntry(text, ping));
    }

    public void add(int col, int row, TablistSkin skin, String text) {
        text = getInstance().getPlaceholderHook().replace(packet.getPlayer(), text);
        entries.put(new Pair<>(col, row), new TablistEntry(text, skin, Config.TABLIST_PING));
    }

    public void add(int col, int row, TablistSkin skin, String text, int ping) {
        text = getInstance().getPlaceholderHook().replace(packet.getPlayer(), text);
        entries.put(new Pair<>(col, row), new TablistEntry(text, skin, ping));
    }
}