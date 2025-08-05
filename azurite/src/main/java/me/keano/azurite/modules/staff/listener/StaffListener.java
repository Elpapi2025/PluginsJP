package me.keano.azurite.modules.staff.listener;

import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.staff.StaffManager;
import me.keano.azurite.modules.staff.extra.StaffItem;
import me.keano.azurite.modules.staff.extra.StaffItemAction;
import me.keano.azurite.modules.staff.menu.InspectionMenu;
import me.keano.azurite.modules.staff.menu.SilentViewMenu;
import me.keano.azurite.utils.Tasks;
import me.keano.azurite.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class StaffListener extends Module<StaffManager> {

    private final Cooldown interactCooldown;
    private final List<String> disabledFrozenCommands;

    public StaffListener(StaffManager manager) {
        super(manager);
        this.interactCooldown = new Cooldown(manager);
        this.disabledFrozenCommands = getConfig().getStringList("STAFF_MODE.DISABLED_COMMANDS_FROZEN");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            for (String disabledFrozenCommand : disabledFrozenCommands) {
                if (!e.getMessage().contains(disabledFrozenCommand)) continue;
                e.setCancelled(true);
                player.sendMessage(getLanguageConfig().getString("STAFF_MODE.NOT_ALLOWED_COMMAND"));
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTab(PlayerChatTabCompleteEvent e) {
        for (UUID uuid : getManager().getStaffMembers().keySet()) {
            Player staff = Bukkit.getPlayer(uuid);

            if (staff == null) continue;

            e.getTabCompletions().remove(staff.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageFrozen(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (getManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            e.setTo(e.getFrom());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropFrozen(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupFrozen(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickFrozen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuitFrozen(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (getManager().isFrozen(player)) {
            getManager().unfreezePlayer(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!e.getAction().name().contains("RIGHT")) return;

        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (getManager().isStaffEnabled(player) && block != null && block.getType().name().contains("SIGN")) {
            e.setCancelled(true);
        }

        if (interactCooldown.hasCooldown(player)) return;

        if (getManager().isStaffEnabled(player)) {
            interactCooldown.applyCooldownTicks(player, 100); // 0.1s
            handleClick(player);

            if (!getManager().isStaffBuild(player)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;

        Player damager = (Player) e.getDamager();

        if (getManager().isStaffEnabled(damager) || getManager().isVanished(damager)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInspect(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) return;

        Player player = e.getPlayer();
        Player clicked = (Player) e.getRightClicked();

        if (getManager().isStaffEnabled(player)) {
            ItemStack hand = getManager().getItemInHand(player);

            if (hand == null) return;

            StaffItem staffItem = getManager().getItem(hand);

            if (staffItem == null) return;
            if (staffItem.getAction() == null) return;
            if (interactCooldown.hasCooldown(player)) return;

            interactCooldown.applyCooldownTicks(player, 100); // 0.1s

            if (staffItem.getAction() == StaffItemAction.INSPECTION) {
                new InspectionMenu(getInstance().getMenuManager(), player, clicked).open();

            } else if (staffItem.getAction() == StaffItemAction.FREEZE) {
                player.chat("/freeze " + clicked.getName());

            } else if (!staffItem.getCommand().isEmpty() && staffItem.getAction() == StaffItemAction.INTERACT_PLAYER) {
                player.chat(staffItem.getCommand()
                        .replace("%player%", clicked.getName())
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageStaff(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (getManager().isStaffEnabled(player) || getManager().isVanished(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // Deny clicking of blocks (Deny GMC Abuse)
    public void onInventory(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();

        if (!getManager().isStaffBuild(player) && getManager().isStaffEnabled(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (!getManager().isStaffBuild(player) && getManager().isStaffEnabled(player)) {
            e.setCancelled(true);
            player.sendMessage(getLanguageConfig().getString("STAFF_MODE.DENY_BREAK"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (!getManager().isStaffBuild(player) && getManager().isStaffEnabled(player)) {
            e.setCancelled(true);
            player.sendMessage(getLanguageConfig().getString("STAFF_MODE.DENY_PLACE"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (getManager().isStaffBuild(player)) return;

        if (getManager().isStaffEnabled(player) || getManager().isVanished(player)) {
            e.setCancelled(true);
            player.sendMessage(getLanguageConfig().getString("STAFF_MODE.DENY_DROP"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (getManager().isStaffEnabled(player) || getManager().isVanished(player)) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // They somehow died while in staff
        if (getManager().isStaffEnabled(player) || getManager().isVanished(player)) {
            e.getDrops().clear();
            e.setDroppedExp(0);
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("azurite.staff") && getConfig().getBoolean("STAFF_MODE.STAFF_MODE_ON_JOIN")) {
            Tasks.execute(getManager(), () -> player.chat("/staff"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (getManager().isStaffEnabled(player)) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (getManager().isStaffEnabled(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInspect(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        Block clicked = e.getClickedBlock();

        if (!getManager().isStaffEnabled(player)) return;
        if (!getManager().isVanished(player)) return;
        if (getManager().isStaffBuild(player)) return;

        if (clicked.getState() instanceof Chest) {
            Chest chest = (Chest) clicked.getState();
            new SilentViewMenu(getInstance().getMenuManager(), player, chest.getInventory()).open();
            e.setCancelled(true);
            return;
        }

        if (clicked.getState() instanceof Hopper) {
            Hopper hopper = (Hopper) clicked.getState();
            new SilentViewMenu(getInstance().getMenuManager(), player, hopper.getInventory()).open();
            e.setCancelled(true);
            return;
        }

        if (clicked.getState() instanceof BrewingStand) {
            BrewingStand brewingStand = (BrewingStand) clicked.getState();
            new SilentViewMenu(getInstance().getMenuManager(), player, brewingStand.getInventory()).open();
            e.setCancelled(true);
            return;
        }

        if (clicked.getState() instanceof Furnace) {
            Furnace furnace = (Furnace) clicked.getState();
            new SilentViewMenu(getInstance().getMenuManager(), player, furnace.getInventory()).open();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlate(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() != Action.PHYSICAL) return;

        Player player = e.getPlayer();

        if (getManager().isStaffEnabled(player) || getManager().isVanished(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();

        if (getManager().isStaffEnabled(player)) {
            player.chat("/staff");
        }
    }

    @EventHandler
    public void onJoinVanish(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("azurite.vanish")) return;

        for (UUID uuid : getManager().getVanished()) {
            Player vanished = Bukkit.getPlayer(uuid);

            if (vanished != null) {
                player.hidePlayer(vanished);
            }
        }
    }

    private void handleClick(Player player) {
        ItemStack hand = getManager().getItemInHand(player);

        if (hand != null) {
            StaffItem staffItem = getManager().getItem(hand);

            // Not a staff item.
            if (staffItem == null) return;

            // Handle replacing of hand
            if (staffItem.getReplacement() != null) {
                for (StaffItem item : getManager().getStaffItems().values()) {
                    if (!item.getName().equals(staffItem.getReplacement())) continue;
                    getManager().setItemInHand(player, item.getItem());
                }
            }

            if (!staffItem.getCommand().isEmpty()) {
                player.chat(staffItem.getCommand());
            }

            if (staffItem.getAction() != null) {
                switch (staffItem.getAction()) {
                    case VANISH_OFF:
                        getManager().disableVanish(player);
                        break;

                    case VANISH_ON:
                        getManager().enableVanish(player);
                        break;
                }
            }
        }
    }
}