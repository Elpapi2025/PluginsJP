package me.titan.core.modules.framework.menu.listener;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.framework.menu.Menu;
import me.titan.core.modules.framework.menu.MenuManager;
import me.titan.core.modules.framework.menu.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MenuListener extends HCFModule<MenuManager> {
    public MenuListener(MenuManager manager) {
        super(manager);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Menu menu = this.getManager().getMenus().remove(player.getUniqueId());
        if (menu != null) {
            menu.destroy();
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Menu menu = this.getManager().getMenus().get(player.getUniqueId());
        if (menu != null) {
            menu.destroy();
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        Menu menu = this.getManager().getMenus().get(player.getUniqueId());
        if (menu != null) {
            event.setCancelled(true);
            if (event.getClickedInventory() != player.getInventory()) {
                Button button = menu.getButtons().get(event.getSlot() + 1);
                if (button != null) {
                    button.onClick(event);
                }
            }
        }
    }
}
