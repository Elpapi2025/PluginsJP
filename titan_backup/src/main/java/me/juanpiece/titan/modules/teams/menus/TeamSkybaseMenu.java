package me.juanpiece.titan.modules.teams.menus;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.teams.claims.Claim;
import me.juanpiece.titan.modules.teams.task.SkybaseTask;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TeamSkybaseMenu extends Menu {

    private final List<SkybaseData> data;
    private final Claim claim;

    public TeamSkybaseMenu(MenuManager manager, Player player, Claim claim) {
        super(
                manager,
                player,
                manager.getTeamConfig().getString("SKYBASE_CONFIG.SKYBASE_MENU.TITLE"),
                manager.getTeamConfig().getInt("SKYBASE_CONFIG.SKYBASE_MENU.SIZE"),
                false
        );
        this.data = new ArrayList<>();
        this.claim = claim;
        this.load();
    }

    private void load() {
        for (String s : getTeamConfig().getStringList("SKYBASE_CONFIG.SKYBASE_MENU.SKYBASE_WALLS")) {
            String[] split = s.split(", ");

            if (split[0].equalsIgnoreCase("NONE")) {
                data.add(new SkybaseData(
                        new ItemBuilder(Material.QUARTZ).setName(split[2]).toItemStack(),
                        null
                ));
                continue;
            }

            Material material = ItemUtils.getMat(split[0]);
            int number = Integer.parseInt(split[1]);
            data.add(new SkybaseData(
                    new ItemBuilder(material).data(getManager(), (short) number).setName(split[2]).toItemStack(),
                    new ItemBuilder(material).data(getManager(), (short) number).toItemStack()
            ));
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 1; i <= data.size(); i++) {
            SkybaseData trap = data.get(i - 1);
            buttons.put(i, new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
                    e.setCancelled(true);

                    if (user.getSkybaseTokens() <= 0) {
                        player.closeInventory();
                        player.sendMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_SKYBASE.NOT_ENOUGH_TOKENS"));
                        return;
                    }

                    player.sendMessage(getLanguageConfig().getString("TEAM_COMMAND.TEAM_SKYBASE.STARTED_PROCESS"));
                    player.closeInventory();
                    user.setSkybaseTokens(user.getSkybaseTokens() - 1);
                    user.save();
                    PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
                    pt.getSkybaseTasks().add(new SkybaseTask(getManager().getInstance().getTeamManager(), player.getUniqueId(), claim, trap.getActualItem()));
                    pt.save();
                }

                @Override
                public ItemStack getItemStack() {
                    return trap.getMenuItem();
                }
            });
        }

        return buttons;
    }

    @Getter
    private static class SkybaseData {

        private final ItemStack menuItem;
        private final ItemStack actualItem;

        public SkybaseData(ItemStack menuItem, ItemStack actualItem) {
            this.menuItem = menuItem;
            this.actualItem = actualItem;
        }
    }
}
