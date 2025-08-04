package me.titan.core.modules.tablist.thread;

import me.titan.core.modules.tablist.Tablist;
import me.titan.core.modules.tablist.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistThread extends Thread {
    private final TablistManager manager;
    
    @Override
    public void run() {
        while (true) {
            try {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Tablist tablist = this.manager.getTablists().get(online.getUniqueId());
                    if (tablist != null) {
                        tablist.update();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(200L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public TablistThread(TablistManager manager) {
        super("Titan - TablistThread");
        this.manager = manager;
        this.start();
    }
}
