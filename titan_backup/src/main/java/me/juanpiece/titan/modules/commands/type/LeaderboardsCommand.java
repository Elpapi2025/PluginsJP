package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.framework.menu.Menu;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.framework.menu.button.Button;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class LeaderboardsCommand extends Command {

    public LeaderboardsCommand(CommandManager manager) {
        super(
                manager,
                "leaderboards"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "leaderboard"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        Player player = (Player) sender;
        new LeaderboardsMenu(getInstance().getMenuManager(), player).open();
    }

    private static class LeaderboardsMenu extends Menu {

        public LeaderboardsMenu(MenuManager manager, Player player) {
            super(
                    manager,
                    player,
                    manager.getLanguageConfig().getString("LEADERBOARDS_COMMAND.TITLE"),
                    manager.getLanguageConfig().getInt("LEADERBOARDS_COMMAND.SIZE"),
                    false
            );
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();
            List<PlayerTeam> topTeams = getInstance().getTeamManager().getTeamSorting().getTeamTop();
            List<User> topKills = getInstance().getUserManager().getTopKills();
            List<User> topDeaths = getInstance().getUserManager().getTopDeaths();
            List<User> topKDR = getInstance().getUserManager().getTopKDR();
            List<User> topKillStreaks = getInstance().getUserManager().getTopKillStreaks();

            for (String key : getLanguageConfig().getConfigurationSection("LEADERBOARDS_COMMAND.ITEMS").getKeys(false)) {
                String path = "LEADERBOARDS_COMMAND.ITEMS." + key + ".";

                buttons.put(getLanguageConfig().getInt(path + "SLOT"), new Button() {
                    @Override
                    public void onClick(InventoryClickEvent e) {
                        e.setCancelled(true);
                    }

                    @Override
                    public ItemStack getItemStack() {
                        ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(getLanguageConfig().getString(path + "MATERIAL")));
                        builder.setName(getLanguageConfig().getString(path + "NAME"));
                        List<String> lore = getLanguageConfig().getStringList(path + "LORE");

                        lore.replaceAll(s -> {
                            // Top Teams
                            for (int i = 0; i < topTeams.size(); i++) {
                                if (i == 10) break; // Limit the loop
                                PlayerTeam pt = topTeams.get(i);
                                s = s.replace("%team_top" + (i + 1) + "%", getLanguageConfig()
                                        .getString(path + "FORMAT")
                                        .replace("%team%", pt.getName())
                                        .replace("%points%", String.valueOf(pt.getPoints())));
                            }

                            // Top Kills
                            for (int i = 0; i < topKills.size(); i++) {
                                if (i == 10) break; // Limit the loop
                                User user = topKills.get(i);
                                s = s.replace("%kills_top" + (i + 1) + "%", getLanguageConfig()
                                        .getString(path + "FORMAT")
                                        .replace("%player%", user.getName())
                                        .replace("%kills%", String.valueOf(user.getKills()))
                                );
                            }

                            // Top Deaths
                            for (int i = 0; i < topDeaths.size(); i++) {
                                if (i == 10) break; // Limit the loop
                                User user = topDeaths.get(i);
                                s = s.replace("%deaths_top" + (i + 1) + "%", getLanguageConfig()
                                        .getString(path + "FORMAT")
                                        .replace("%player%", user.getName())
                                        .replace("%deaths%", String.valueOf(user.getDeaths())));
                            }

                            // Top KDR
                            for (int i = 0; i < topKDR.size(); i++) {
                                if (i == 10) break; // Limit the loop
                                User user = topKDR.get(i);
                                s = s.replace("%kdr_top" + (i + 1) + "%", getLanguageConfig()
                                        .getString(path + "FORMAT")
                                        .replace("%player%", user.getName())
                                        .replace("%kdr%", user.getKDRString()));
                            }

                            // Top Killstreaks
                            for (int i = 0; i < topKillStreaks.size(); i++) {
                                if (i == 10) break; // Limit the loop
                                User user = topKillStreaks.get(i);
                                s = s.replace("%killstreaks_top" + (i + 1) + "%", getLanguageConfig()
                                        .getString(path + "FORMAT")
                                        .replace("%player%", user.getName())
                                        .replace("%killstreak%", String.valueOf(user.getKillstreak())));
                            }

                            // Any extra
                            if (s.contains("%deaths_top") || s.contains("%kills_top") || s.contains("%team_top") ||
                                    s.contains("%killstreaks_top") || s.contains("%kdr_top")) {
                                s = getLanguageConfig().getString("LEADERBOARDS_COMMAND.NONE_MESSAGE");
                            }

                            return s;
                        });

                        builder.setLore(lore);
                        return builder.toItemStack();
                    }
                });
            }

            return buttons;
        }
    }
}