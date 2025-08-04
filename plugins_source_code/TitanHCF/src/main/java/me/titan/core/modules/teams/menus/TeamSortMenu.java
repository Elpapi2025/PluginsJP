package me.titan.core.modules.teams.menus;

import me.titan.core.modules.framework.menu.Menu;
import me.titan.core.modules.framework.menu.MenuManager;
import me.titan.core.modules.framework.menu.button.Button;
import me.titan.core.modules.users.User;
import me.titan.core.modules.users.settings.TeamListSetting;
import me.titan.core.utils.ItemBuilder;
import me.titan.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSortMenu extends Menu {
    private final List<TeamListSetting> settings;
    
    public TeamListSetting getSetting(TeamListSetting setting) {
        int i = this.settings.indexOf(setting);
        if (i == this.settings.size() - 1) {
            return this.settings.get(0);
        }
        return this.settings.get(i + 1);
    }
    
    public TeamSortMenu(MenuManager manager, Player player) {
        super(manager, player, manager.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SORT.TITLE"), manager.getLanguageConfig().getInt("TEAM_COMMAND.TEAM_SORT.SIZE"), false);
        this.settings = Arrays.asList(TeamListSetting.values());
    }
    
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        TeamListSetting setting = user.getTeamListSetting();
        buttons.put(this.getLanguageConfig().getInt("TEAM_COMMAND.TEAM_SORT.SLOT"), new Button() {
            @Override
            public ItemStack getItemStack() {
                ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(TeamSortMenu.this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SORT.MATERIAL"))).setName(TeamSortMenu.this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SORT.NAME"));
                for (TeamListSetting listSetting : TeamSortMenu.this.settings) {
                    builder.addLoreLine((setting == listSetting) ? TeamSortMenu.this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_SORT.POINTER") + TeamSortMenu.this.getLanguageConfig().getString(listSetting.getConfigPath()) : TeamSortMenu.this.getLanguageConfig().getString(listSetting.getConfigPath()));
                }
                return builder.toItemStack();
            }
            
            @Override
            public void onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                user.setTeamListSetting(TeamSortMenu.this.getSetting(setting));
                TeamSortMenu.this.update();
            }
        });
        return buttons;
    }
}
