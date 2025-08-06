package me.juanpiece.titan.modules.signs.kitmap;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.signs.CustomSign;
import me.juanpiece.titan.modules.signs.CustomSignManager;
import me.juanpiece.titan.utils.extra.Cooldown;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class QuickRefillSign extends CustomSign {

    private Cooldown cooldown;

    public QuickRefillSign(CustomSignManager manager) {
        super(
                manager,
                manager.getConfig().getStringList("SIGNS_CONFIG.QUICK_REFILL_SIGN.LINES")
        );
        this.cooldown = new Cooldown(manager);
    }

    @Override
    public void onClick(Player player, Sign sign) {
        if (cooldown.hasCooldown(player)) {
            player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.QUICK_REFILL_SIGN.COOLDOWN")
                    .replace("%time%", cooldown.getRemaining(player))
            );
            return;
        }

        new QuickRefillMenu(getInstance().getMenuManager(), player).open();
        cooldown.applyCooldown(player, getConfig().getInt("SIGNS_CONFIG.QUICK_REFILL_SIGN.COOLDOWN"));
    }

    private static class QuickRefillMenu extends Menu {

        public QuickRefillMenu(MenuManager manager, Player player) {
            super(
                    manager,
                    player,
                    manager.getConfig().getString("SIGNS_CONFIG.QUICK_REFILL_SIGN.MENU_TITLE"),
                    manager.getConfig().getInt("SIGNS_CONFIG.QUICK_REFILL_SIGN.MENU_SIZE"),
                    false
            );
            this.setAllowInteract(true);
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();

            for (int i = 1; i <= Config.QUICK_REFILL_SIGN.length; i++) {
                int copy = i;
                buttons.put(i, new Button() {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        // Empty
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return Config.QUICK_REFILL_SIGN[copy - 1].clone();
                    }
                });
            }

            return buttons;
        }
    }
}