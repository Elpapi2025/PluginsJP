package me.titan.core.modules.staff;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.staff.extra.StaffItem;
import me.titan.core.modules.staff.extra.StaffItemAction;
import me.titan.core.modules.staff.listener.StaffListener;
import me.titan.core.utils.ItemBuilder;
import me.titan.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class StaffManager extends Manager {
    private final Set<UUID> frozen;
    private final Set<UUID> staffBuild;
    private final Set<UUID> vanished;
    private final Map<UUID, Staff> staffMembers;
    private final Map<ItemStack, StaffItem> staffItems;
    
    public boolean isStaffBuild(Player player) {
        return this.staffBuild.contains(player.getUniqueId());
    }
    
    @Override
    public void disable() {
        Iterator<UUID> members = this.staffMembers.keySet().iterator();
        while (members.hasNext()) {
            UUID member = members.next();
            Staff staff = this.staffMembers.get(member);
            Player player = Bukkit.getPlayer(member);
            PlayerInventory inventory = player.getInventory();
            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }
            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());
            player.updateInventory();
            player.setGameMode(staff.getGameMode());
            members.remove();
        }
    }
    
    public boolean isStaffEnabled(Player player) {
        return this.staffMembers.containsKey(player.getUniqueId());
    }
    
    public void enableVanish(Player player) {
        this.vanished.add(player.getUniqueId());
        player.spigot().setCollidesWithEntities(false);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("titan.vanish")) {
                continue;
            }
            online.hidePlayer(player);
        }
    }
    
    public void freezePlayer(Player player) {
        player.setWalkSpeed(0.0f);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
        this.frozen.add(player.getUniqueId());
    }
    
    public void disableVanish(Player player) {
        this.vanished.remove(player.getUniqueId());
        player.spigot().setCollidesWithEntities(true);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
        }
    }
    
    private void load() {
        for (String s : this.getConfig().getConfigurationSection("STAFF_MODE.STAFF_ITEMS").getKeys(false)) {
            String path = "STAFF_MODE.STAFF_ITEMS." + s + ".";
            String action = this.getConfig().getString(path + "ACTION");
            String replace = this.getConfig().getString(path + "REPLACE");
            ItemStack stack = new ItemBuilder(ItemUtils.getMat(this.getConfig().getString(path + "MATERIAL"))).setName(this.getConfig().getString(path + "NAME")).setLore(this.getConfig().getStringList(path + "LORE")).data(this, (short)this.getConfig().getInt(path + "DATA")).toItemStack();
            this.staffItems.put(stack, new StaffItem(action.isEmpty() ? null : StaffItemAction.valueOf(action), replace.isEmpty() ? null : replace, stack, this.getConfig().getInt(path + "SLOT")));
        }
    }
    
    public boolean isVanished(Player player) {
        return this.vanished.contains(player.getUniqueId());
    }
    
    public void unfreezePlayer(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFoodLevel(20);
        player.setSprinting(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.removePotionEffect(PotionEffectType.JUMP);
        this.frozen.remove(player.getUniqueId());
    }
    
    public StaffManager(HCF plugin) {
        super(plugin);
        this.staffMembers = new HashMap<>();
        this.staffItems = new HashMap<>();
        this.vanished = new HashSet<>();
        this.frozen = new HashSet<>();
        this.staffBuild = new HashSet<>();
        new StaffListener(this);
        this.load();
    }
    
    public void enableStaff(Player player) {
        PlayerInventory inventory = player.getInventory();
        Staff staff = new Staff(inventory, player.getGameMode());
        inventory.clear();
        inventory.setArmorContents(null);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
            staff.getEffects().add(effect);
        }
        for (StaffItem item : this.staffItems.values()) {
            if (item.getAction() == StaffItemAction.VANISH_ON) {
                continue;
            }
            player.getInventory().setItem(item.getSlot() - 1, item.getItem());
        }
        player.updateInventory();
        player.setGameMode(GameMode.CREATIVE);
        this.enableVanish(player);
        this.staffMembers.put(player.getUniqueId(), staff);
        this.getInstance().getNametagManager().update();
    }
    
    public boolean isFrozen(Player player) {
        return this.frozen.contains(player.getUniqueId());
    }
    
    public void disableStaff(Player player) {
        Staff staff = this.staffMembers.get(player.getUniqueId());
        if (staff != null) {
            PlayerInventory inventory = player.getInventory();
            inventory.setContents(staff.getContents());
            inventory.setArmorContents(staff.getArmorContents());
            for (PotionEffect effect : staff.getEffects()) {
                player.addPotionEffect(effect);
            }
            player.updateInventory();
            player.setGameMode(staff.getGameMode());
            this.disableVanish(player);
            this.staffMembers.remove(player.getUniqueId());
            this.staffBuild.remove(player.getUniqueId());
            this.getInstance().getNametagManager().update();
        }
    }
}