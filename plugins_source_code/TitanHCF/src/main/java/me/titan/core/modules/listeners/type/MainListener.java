package me.titan.core.modules.listeners.type;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.listeners.ListenerManager;
import me.titan.core.utils.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainListener extends HCFModule<ListenerManager> {
    private final ItemStack bookItem;
    private final List<ItemStack> joinItems;
    
    private ItemStack loadBook() {
        ItemStack stack = new ItemBuilder(Material.WRITTEN_BOOK).toItemStack();
        BookMeta meta = (BookMeta)stack.getItemMeta();
        meta.setTitle(this.getConfig().getString("JOIN_ITEMS.BOOK_ITEM.TITLE"));
        meta.setPages(this.getConfig().getStringList("JOIN_ITEMS.BOOK_ITEM.PAGES"));
        meta.setAuthor(this.getConfig().getString("JOIN_ITEMS.BOOK_ITEM.AUTHOR"));
        stack.setItemMeta(meta);
        this.joinItems.add(stack);
        return stack;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);
        if (!player.hasPlayedBefore() && this.getConfig().getBoolean("JOIN_ITEMS.ENABLED")) {
            for (ItemStack stack : this.joinItems) {
                player.getInventory().addItem(stack);
            }
        }
        if (this.getConfig().getBoolean("TEAM_INFO_JOIN")) {
            player.chat("/t info");
        }
    }
    
    private void load() {
        for (String s : this.getConfig().getStringList("JOIN_ITEMS.NORMAL_ITEMS")) {
            String[] items = s.split(", ");
            ItemBuilder builder = new ItemBuilder(Material.valueOf(items[0]), Integer.parseInt(items[1]));
            if (!items[2].equals("NONE")) {
                String[] enchantments = items[2].split(":");
                Enchantment enchantment = Enchantment.getByName(enchantments[0]);
                builder.addEnchant(enchantment, Integer.parseInt(enchantments[1]));
            }
            this.joinItems.add(builder.toItemStack());
        }
        new BukkitRunnable() {
            public void run() {
                List<String> lines = MainListener.this.getConfig().getStringList("ONLINE_DONOR.MESSAGE");
                List<String> toSend = new ArrayList<>();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.hasPermission("titan.donor")) {
                        continue;
                    }
                    if (online.isOp()) {
                        continue;
                    }
                    toSend.add(online.getName());
                }
                String text = toSend.isEmpty() ? "None" : StringUtils.join(toSend, ", ");
                lines.replaceAll(s -> s.replaceAll("%members%", text));
                for (String s : lines) {
                    Bukkit.broadcastMessage(s);
                }
            }
        }.runTaskTimerAsynchronously(this.getInstance(), 400L, 20L * this.getConfig().getInt("ONLINE_DONOR.INTERVAL"));
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
    
    public MainListener(ListenerManager manager) {
        super(manager);
        this.joinItems = new ArrayList<>();
        this.bookItem = this.loadBook();
        this.load();
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (item.getItemStack().isSimilar(this.bookItem)) {
            item.remove();
        }
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EnderDragon || entity instanceof Wither) {
            event.setCancelled(true);
        }
    }
}
