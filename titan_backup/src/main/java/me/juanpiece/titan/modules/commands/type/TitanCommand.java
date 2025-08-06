package me.juanpiece.titan.modules.commands.type;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.framework.commands.extra.TabCompletion;
import me.juanpiece.titan.modules.storage.Storage;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.enums.TeamType;
import me.juanpiece.titan.modules.teams.type.PlayerTeam;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.utils.CC;
import me.juanpiece.titan.utils.configs.ConfigYML;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TitanCommand extends Command {

    public TitanCommand(CommandManager manager) {
        super(
                manager,
                "titan"
        );
        this.completions.add(new TabCompletion(Arrays.asList("reload", "deleteteams", "deleteusers", "version", "forcesave"), 0));
        this.setPermissible("titan.reload");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "hcf",
                "hcfcore"
        );
    }

    @Override
    public List<String> usage() {
        return Arrays.asList(
                CC.LINE,
                "&fThis server is running &cTitanHCF&f.",
                "&fUse &c/titan reload &fto reload configs.",
                "&fUse &c/titan version &fto check your current ver.",
                "&fUse &c/titan forcesave &fto save data.",
                "&fUse &c/titan deleteteams &fto delete teams.",
                "&fUse &c/titan deleteusers &fto delete users.",
                CC.LINE
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permissible)) {
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                long now = System.currentTimeMillis();

                for (ConfigYML config : getInstance().getConfigs()) {
                    config.reload(); // reload
                    config.reloadCache(); // then re-cache all the objects in the cache.
                }

                for (Manager manage : getInstance().getManagers()) {
                    manage.reload();
                }

                Config.load(getInstance().getConfigsObject(), true);

                sendMessage(sender, "&cTitan &fhas been reloaded in &a" + (System.currentTimeMillis() - now) + "ms&f.");
                sendMessage(sender, "&cPLEASE NOTE THIS MIGHT NOT RELOAD SOME THINGS! - A RESTART IS REQUIRED.");
                return;

            case "version":
                sendMessage(sender, "&cTitan &fis currently on version &a" + getInstance().getDescription().getVersion() + "&f.");
                return;

            case "forcesave":
                long now1 = System.currentTimeMillis();
                Storage storage = getInstance().getStorageManager().getStorage();

                storage.saveTimers();
                storage.saveTeams();
                storage.saveUsers();

                sendMessage(sender, "&cTitan &fhas been saved in &a" + (System.currentTimeMillis() - now1) + "ms&f.");
                return;

            case "deleteteams":
                TeamManager teamManager = getInstance().getTeamManager();
                int teams = 0;

                for (Team team : getInstance().getTeamManager().getTeams().values()) {
                    if (team.getType() != TeamType.PLAYER) continue;
                    PlayerTeam playerTeam = (PlayerTeam) team;
                    playerTeam.disband(false);
                }

                // Do not modify the maps while looping otherwise CME
                teamManager.getTeams().clear();
                teamManager.getPlayerTeams().clear();
                teamManager.getSystemTeams().clear();
                teamManager.getStringTeams().clear();
                teamManager.getClaimManager().getClaims().clear();

                sendMessage(sender, "&eYou have deleted &d" + teams + " &eteams.");
                return;

            case "deleteusers":
                int users = 0;

                for (User user : getInstance().getUserManager().getUsers().values()) {
                    user.delete();
                    users++;
                }

                // Fix
                getInstance().getUserManager().getUsers().clear();
                getInstance().getUserManager().getUuidCache().clear();

                // Create a user for all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = new User(getInstance().getUserManager(), player.getUniqueId(), player.getName());
                    user.save();
                }

                sendMessage(sender, "&eYou have deleted &d" + users + " &eusers.");
                return;
        }

        sendUsage(sender);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProcess(PlayerCommandPreprocessEvent e) {
        Player sender = e.getPlayer();

        if (sender.hasPermission("titan.reload")) return;

        // /titan
        if (e.getMessage().equals("/titan:" + name) || e.getMessage().equals("/" + name)) {
            sendMessage(sender, CC.LINE);
            sendMessage(sender, "&fThis server is running &cTitanHCF&f.");
            sendMessage(sender, "&fMade by &cJuanpiece &ffor juanpiece.cc");
            sendMessage(sender, "&fhttps://www.mc-market.org/resources/24593/");
            sendMessage(sender, CC.LINE);
            return;
        }

        // Aliases
        for (String alias : aliases()) {
            if (e.getMessage().equals("/titan:" + alias) || e.getMessage().equals("/" + alias)) {
                sendMessage(sender, CC.LINE);
                sendMessage(sender, "&fThis server is running &cTitanHCF&f.");
                sendMessage(sender, "&fMade by &cJuanpiece &ffor juanpiece.cc");
                sendMessage(sender, "&fhttps://www.mc-market.org/resources/24593/");
                sendMessage(sender, CC.LINE);
                break;
            }
        }
    }
}