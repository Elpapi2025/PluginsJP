package me.titan.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.titan.core.modules.ability.AbilityManager;
import me.titan.core.modules.balance.BalanceManager;
import me.titan.core.modules.board.BoardManager;
import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.deathban.DeathbanManager;
import me.titan.core.modules.events.king.KingManager;
import me.titan.core.modules.events.koth.KothManager;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.framework.extra.Configs;
import me.titan.core.modules.framework.menu.MenuManager;
import me.titan.core.modules.hooks.ranks.RankManager;
import me.titan.core.modules.hooks.tags.TagManager;
import me.titan.core.modules.kits.KitManager;
import me.titan.core.modules.listeners.ListenerManager;
import me.titan.core.modules.listeners.type.GlitchListener;
import me.titan.core.modules.loggers.LoggerManager;
import me.titan.core.modules.nametags.NametagManager;
import me.titan.core.modules.pvpclass.PvPClassManager;
import me.titan.core.modules.reclaims.ReclaimManager;
import me.titan.core.modules.scheduler.ScheduleManager;
import me.titan.core.modules.signs.CustomSignManager;
import me.titan.core.modules.spawners.SpawnerManager;
import me.titan.core.modules.staff.StaffManager;
import me.titan.core.modules.storage.StorageManager;
import me.titan.core.modules.tablist.TablistManager;
import me.titan.core.modules.teams.TeamManager;
import me.titan.core.modules.timers.TimerManager;
import me.titan.core.modules.users.UserManager;
import me.titan.core.modules.versions.VersionManager;
import me.titan.core.modules.walls.WallManager;
import me.titan.core.modules.waypoints.WaypointManager;
import me.titan.core.utils.Logger;
import me.titan.core.utils.configs.ConfigYML;
import me.titan.core.utils.event.ArmorListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HCF extends JavaPlugin {

    private Gson gson;

    //Managers
    private List<Manager> managers;
    private UserManager userManager;
    private WaypointManager waypointManager;
    private ScheduleManager scheduleManager;
    private NametagManager nametagManager;
    private StorageManager storageManager;
    private TeamManager teamManager;
    private DeathbanManager deathbanManager;
    private RankManager rankManager;
    private SpawnerManager spawnerManager;
    private PvPClassManager classManager;
    private KitManager kitManager;
    private BalanceManager balanceManager;
    private LoggerManager loggerManager;
    private TimerManager timerManager;
    private ReclaimManager reclaimManager;
    private KingManager kingManager;
    private TagManager tagManager;
    private WallManager wallManager;
    private VersionManager versionManager;
    private AbilityManager abilityManager;
    private KothManager kothManager;
    private StaffManager staffManager;
    private MenuManager menuManager;

    private GlitchListener glitchListener;
    private boolean kits;
    private List<ConfigYML> configs;


    public void onEnable() {

        Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(), this);

        this.managers = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        new Configs().load(this);
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
        this.rankManager = new RankManager(this);
        this.tagManager = new TagManager(this);
        this.classManager = new PvPClassManager(this);
        this.loggerManager = new LoggerManager(this);
        this.abilityManager = new AbilityManager(this);
        this.storageManager = new StorageManager(this);
        this.scheduleManager = new ScheduleManager(this);
        this.staffManager = new StaffManager(this);
        this.menuManager = new MenuManager(this);
        this.kothManager = new KothManager(this);
        this.kingManager = new KingManager(this);
        this.glitchListener = new GlitchListener(new ListenerManager(this));
        this.kits = this.getConfig().getBoolean("KITMAP_MODE");
        new BoardManager(this);
        new TablistManager(this);
        new CommandManager(this);
        new CustomSignManager(this);
        this.managers.forEach(Manager::enable);
        this.userManager.setLoaded(true);
        Logger.state("Activado", this.managers.size(), this.teamManager.getTeams().size(), this.userManager.getUsers().size());
    }

    public void onDisable() {
        this.managers.forEach(Manager::disable);
        Logger.state("Desactivado", this.managers.size(), this.teamManager.getTeams().size(), this.userManager.getUsers().size());
    }
}
