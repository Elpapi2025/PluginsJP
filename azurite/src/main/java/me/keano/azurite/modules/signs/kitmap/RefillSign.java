package me.keano.azurite.modules.signs.kitmap;

import lombok.Getter;
import lombok.Setter;
import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.framework.menu.Menu;
import me.keano.azurite.modules.framework.menu.MenuManager;
import me.keano.azurite.modules.framework.menu.button.Button;
import me.keano.azurite.modules.signs.CustomSign;
import me.keano.azurite.modules.signs.CustomSignManager;
import me.keano.azurite.utils.extra.Cooldown;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class RefillSign extends CustomSign {

    private Cooldown cooldown;

    public RefillSign(CustomSignManager manager) {
        super(
                manager,
                manager.getConfig().getStringList("SIGNS_CONFIG.REFILL_SIGN.LINES")
        );
        this.cooldown = new Cooldown(manager);
    }

    @Override
    public void onClick(Player player, Sign sign) {
        if (cooldown.hasCooldown(player)) {
            player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.REFILL_SIGN.COOLDOWN")
                    .replace("%time%", cooldown.getRemaining(player))
            );
            return;
        }

        new RefillMenu(getInstance().getMenuManager(), player).open();
        cooldown.applyCooldown(player, getConfig().getInt("SIGNS_CONFIG.REFILL_SIGN.COOLDOWN"));
    }

    private static class RefillMenu extends Menu {

        public RefillMenu(MenuManager manager, Player player) {
            super(
                    manager,
                    player,
                    manager.getConfig().getString("SIGNS_CONFIG.REFILL_SIGN.MENU_TITLE"),
                    manager.getConfig().getInt("SIGNS_CONFIG.REFILL_SIGN.MENU_SIZE"),
                    false
            );
            this.setAllowInteract(true);
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();

            for (int i = 1; i <= Config.REFILL_SIGN.length; i++) {
                int copy = i;
                buttons.put(i, new Button() {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        // Empty
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return Config.REFILL_SIGN[copy - 1].clone();
                    }
                });
            }

            return buttons;
        }
    }
}