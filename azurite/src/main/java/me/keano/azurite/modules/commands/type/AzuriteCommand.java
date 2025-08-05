package me.keano.azurite.modules.commands.type;

import me.keano.azurite.modules.commands.CommandManager;
import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.framework.Manager;
import me.keano.azurite.modules.framework.commands.Command;
import me.keano.azurite.modules.framework.commands.extra.TabCompletion;
import me.keano.azurite.modules.storage.Storage;
import me.keano.azurite.modules.teams.Team;
import me.keano.azurite.modules.teams.TeamManager;
import me.keano.azurite.modules.teams.enums.TeamType;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.modules.users.User;
import me.keano.azurite.utils.CC;
import me.keano.azurite.utils.configs.ConfigYML;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteCommand extends Command {

    public AzuriteCommand(CommandManager manager) {
        super(
                manager,
                "azurite"
        );
        this.completions.add(new TabCompletion(Arrays.asList("reload", "deleteteams", "deleteusers", "version", "forcesave"), 0));
        this.setPermissible("azurite.reload");
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
                "&eThis server is running &dAzuriteHCF&e.",
                "&eUse &d/azurite reload &eto reload configs.",
                "&eUse &d/azurite version &eto check your current ver.",
                "&eUse &d/azurite forcesave &eto save data.",
                "&eUse &d/azurite deleteteams &eto delete teams.",
                "&eUse &d/azurite deleteusers &eto delete users.",
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

                sendMessage(sender, "&dAzurite &ehas been reloaded in &a" + (System.currentTimeMillis() - now) + "ms&e.");
                sendMessage(sender, "&cPLEASE NOTE THIS MIGHT NOT RELOAD SOME THINGS! - A RESTART IS REQUIRED.");
                return;

            case "version":
                sendMessage(sender, "&dAzurite &eis currently on version &a" + getInstance().getDescription().getVersion() + "&e.");
                return;

            case "forcesave":
                long now1 = System.currentTimeMillis();
                Storage storage = getInstance().getStorageManager().getStorage();

                storage.saveTimers();
                storage.saveTeams();
                storage.saveUsers();

                sendMessage(sender, "&dAzurite &ehas been saved in &a" + (System.currentTimeMillis() - now1) + "ms&e.");
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

        if (sender.hasPermission("azurite.reload")) return;

        // /azurite
        if (e.getMessage().equals("/azurite:" + name) || e.getMessage().equals("/" + name)) {
            sendMessage(sender, CC.LINE);
            sendMessage(sender, "&eThis server is running &dAzuriteHCF&e.");
            sendMessage(sender, "&eMade by &dKeqno_ &efor azurite.cc");
            sendMessage(sender, "&ehttps://www.mc-market.org/resources/24593/");
            sendMessage(sender, CC.LINE);
            return;
        }

        // Aliases
        for (String alias : aliases()) {
            if (e.getMessage().equals("/azurite:" + alias) || e.getMessage().equals("/" + alias)) {
                sendMessage(sender, CC.LINE);
                sendMessage(sender, "&eThis server is running &dAzuriteHCF&e.");
                sendMessage(sender, "&eMade by &dKeqno_ &efor azurite.cc");
                sendMessage(sender, "&ehttps://www.mc-market.org/resources/24593/");
                sendMessage(sender, CC.LINE);
                break;
            }
        }
    }
}