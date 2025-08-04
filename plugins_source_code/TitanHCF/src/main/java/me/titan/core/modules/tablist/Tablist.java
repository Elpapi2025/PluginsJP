package me.titan.core.modules.tablist;

import lombok.Getter;
import me.titan.core.modules.framework.*;
import me.titan.core.modules.tablist.extra.*;
import me.titan.core.modules.tablist.packet.*;
import org.bukkit.entity.*;
import com.google.common.collect.*;

@Getter
public class Tablist extends HCFModule<TablistManager> {
    private final Table<Integer, Integer, TablistEntry> entries;
    private final TablistPacket packet;
    private static final TablistEntry EMPTY_ENTRY;
    
    public void update() {
        this.entries.clear();
        this.packet.update();
    }
    
    public TablistEntry getEntries(int x, int y) {
        TablistEntry entry = this.entries.get(x, y);
        if (entry == null) {
            this.entries.put(x, y, Tablist.EMPTY_ENTRY);
            return Tablist.EMPTY_ENTRY;
        }
        return entry;
    }
    
    public void add(int x, int y, String name) {
        this.entries.put(x, y, new TablistEntry(name, -1));
    }
    
    public TablistPacket getPacket() {
        return this.packet;
    }
    
    public Tablist(TablistManager manager, Player player) {
        super(manager);
        this.entries = HashBasedTable.create();
        this.packet = manager.createPacket(player);
    }
    
    static {
        EMPTY_ENTRY = new TablistEntry("", -1);
    }
    
    public void add(int x, int y, String name, int ping) {
        this.entries.put(x, y, new TablistEntry(name, ping));
    }
}
