package me.juanpiece.titan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.juanpiece.titan.modules.ability.AbilityManager;
import me.juanpiece.titan.modules.balance.BalanceManager;
import me.juanpiece.titan.modules.board.BoardManager;
import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.deathban.DeathbanManager;
import me.juanpiece.titan.modules.events.conquest.ConquestManager;
import me.juanpiece.titan.modules.events.eotw.EOTWManager;
import me.juanpiece.titan.modules.events.king.KingManager;
import me.juanpiece.titan.modules.events.koth.KothManager;
import me.juanpiece.titan.modules.events.purge.PurgeManager;
import me.juanpiece.titan.modules.events.sotw.SOTWManager;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.framework.extra.Configs;
import me.juanpiece.titan.modules.framework.menu.MenuManager;
import me.juanpiece.titan.modules.hooks.abilities.AbilitiesHook;
import me.juanpiece.titan.modules.hooks.clients.ClientHook;
import me.juanpiece.titan.modules.hooks.pearls.PearlHook;
import me.juanpiece.titan.modules.hooks.placeholder.PlaceholderHook;
import me.juanpiece.titan.modules.hooks.ranks.RankHook;
import me.juanpiece.titan.modules.hooks.tags.TagHook;
import me.juanpiece.titan.modules.killstreaks.KillstreakManager;
import me.juanpiece.titan.modules.killtag.KilltagManager;
import me.juanpiece.titan.modules.kits.KitManager;
import me.juanpiece.titan.modules.listeners.ListenerManager;
import me.juanpiece.titan.modules.listeners.type.GlitchListener;
import me.juanpiece.titan.modules.loggers.LoggerManager;
import me.juanpiece.titan.modules.nametags.NametagManager;
import me.juanpiece.titan.modules.pvpclass.PvPClassManager;
import me.juanpiece.titan.modules.reclaims.ReclaimManager;
import me.juanpiece.titan.modules.scheduler.ScheduleManager;
import me.juanpiece.titan.modules.signs.CustomSignManager;
import me.juanpiece.titan.modules.spawners.SpawnerManager;
import me.juanpiece.titan.modules.staff.StaffManager;
import me.juanpiece.titan.modules.storage.StorageManager;
import me.juanpiece.titan.modules.tablist.TablistManager;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.timers.TimerManager;
import me.juanpiece.titan.modules.tips.TipManager;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.UserManager;
import me.juanpiece.titan.modules.versions.VersionManager;
import me.juanpiece.titan.modules.walls.WallManager;
import me.juanpiece.titan.modules.waypoints.WaypointManager;
import me.juanpiece.titan.utils.Logger;
import me.juanpiece.titan.utils.configs.ConfigYML;
import me.juanpiece.titan.utils.extra.Cooldown;
import me.juanpiece.titan.utils.extra.CooldownClearTask;
import me.juanpiece.titan.utils.extra.TeamCooldown;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public final class HCF extends JavaPlugin {

    @Getter private static HCF instance;

    private List<Manager> managers;
    private List<ConfigYML> configs; // used for easier reloading
    private List<Cooldown> cooldowns; // used for clearing cache/mem
    private List<TeamCooldown> teamCooldowns;

    private Configs configsObject;

    private Gson gson; // used for serialization / deserialization
    private GlitchListener glitchListener; // Used to check glitch cooldown

    // Managers
    private UserManager userManager;
    private VersionManager versionManager;
    private TimerManager timerManager;
    private BalanceManager balanceManager;
    private WaypointManager waypointManager;
    private NametagManager nametagManager;
    private TeamManager teamManager;
    private WallManager wallManager;
    private SpawnerManager spawnerManager;
    private DeathbanManager deathbanManager;
    private ReclaimManager reclaimManager;
    private KitManager kitManager;
    private RankHook rankHook;
    private TagHook tagHook;
    private PlaceholderHook placeholderHook;
    private ClientHook clientHook;
    private AbilitiesHook abilitiesHook;
    private PvPClassManager classManager;
    private LoggerManager loggerManager;
    private AbilityManager abilityManager;
    private StorageManager storageManager;
    private ScheduleManager scheduleManager;
    private StaffManager staffManager;
    private KillstreakManager killstreakManager;
    private KilltagManager killtagManager;
    private MenuManager menuManager;
    private ListenerManager listenerManager;

    // Events
    private KothManager kothManager;
    private KingManager kingManager;
    private SOTWManager sotwManager;
    private EOTWManager eotwManager;
    private PurgeManager purgeManager;
    private ConquestManager conquestManager;

    private boolean loaded = false;
    private boolean kits;
    public String load;

    @Override
    public void onEnable() {
        instance = this;
        this.managers = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.cooldowns = new ArrayList<>();
        this.teamCooldowns = new ArrayList<>();

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        (this.configsObject = new Configs()).load(this);

        this.userManager = new UserManager(this);
        this.versionManager = new VersionManager(this);
        this.timerManager = new TimerManager(this);
        this.balanceManager = new BalanceManager(this);
        this.waypointManager = new WaypointManager(this);
        this.nametagManager = new NametagManager(this);
        this.teamManager = new TeamManager(this);
        this.wallManager = new WallManager(this);
        this.spawnerManager = new SpawnerManager(this);
        this.deathbanManager = new DeathbanManager(this);
        this.reclaimManager = new ReclaimManager(this);
        this.kitManager = new KitManager(this);
        this.rankHook = new RankHook(this);
        this.tagHook = new TagHook(this);
        this.abilitiesHook = new AbilitiesHook(this);
        this.placeholderHook = new PlaceholderHook(this);
        this.clientHook = new ClientHook(this);
        this.classManager = new PvPClassManager(this);
        this.loggerManager = new LoggerManager(this);
        this.abilityManager = new AbilityManager(this);
        this.storageManager = new StorageManager(this);
        this.scheduleManager = new ScheduleManager(this);
        this.staffManager = new StaffManager(this);
        this.killstreakManager = new KillstreakManager(this);
        this.killtagManager = new KilltagManager(this);
        this.menuManager = new MenuManager(this);
        this.listenerManager = new ListenerManager(this);

        this.kothManager = new KothManager(this);
        this.kingManager = new KingManager(this);
        this.sotwManager = new SOTWManager(this);
        this.eotwManager = new EOTWManager(this);
        this.purgeManager = new PurgeManager(this);
        this.conquestManager = new ConquestManager(this);

        this.glitchListener = new GlitchListener(listenerManager);
        this.kits = getConfig().getBoolean("KITMAP_MODE");

        new PearlHook(this);
        new CommandManager(this);
        new CustomSignManager(this);
        new TipManager(this);
        new BoardManager(this);
        new CooldownClearTask(this);

        if (Config.TABLIST_ENABLED) new TablistManager(this);

        this.managers.forEach(Manager::enable);
        this.loaded = true;

        Logger.state("Enabled", managers.size(),
                teamManager.getTeams().size(), userManager.getUsers().size(),
                kitManager.getKits().size(), kothManager.getKoths().size());
    }

    @Override
    public void onDisable() {
        if (teamManager != null && userManager != null) {
            this.managers.forEach(Manager::disable);
            Logger.state("Disabled", managers.size(),
                    teamManager.getTeams().size(), userManager.getUsers().size(),
                    kitManager.getKits().size(), kothManager.getKoths().size());
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(this.userManager.getUsers().values());
    }

    public List<me.juanpiece.titan.modules.teams.Team> getTeams() {
        return new ArrayList<>(this.teamManager.getTeams().values());
    }
}