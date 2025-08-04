package me.titan.core.modules.commands;

import me.titan.core.modules.framework.*;
import me.titan.core.modules.framework.commands.*;
import me.titan.core.modules.teams.commands.team.*;
import me.titan.core.modules.teams.commands.systeam.*;
import me.titan.core.modules.teams.commands.mountain.*;
import me.titan.core.modules.timers.command.timer.*;
import me.titan.core.modules.timers.command.customtimer.*;
import me.titan.core.modules.deathban.command.*;
import me.titan.core.modules.kits.commands.*;
import me.titan.core.modules.events.koth.command.*;
import me.titan.core.modules.events.king.command.*;
import me.titan.core.modules.teams.commands.citadel.*;
import me.titan.core.modules.timers.command.keyall.*;
import me.titan.core.modules.reclaims.command.*;
import me.titan.core.modules.spawners.command.*;
import me.titan.core.modules.ability.command.*;
import me.titan.core.modules.commands.type.essential.*;
import me.titan.core.modules.scheduler.command.*;
import me.titan.core.modules.staff.command.*;
import me.titan.core.*;
import me.titan.core.modules.commands.type.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.*;

public class CommandManager extends Manager {
    private final List<Command> commands;
    
    private void load() {
        this.commands.add(new TeamCommand(this));
        this.commands.add(new SysTeamCommand(this));
        this.commands.add(new MountainCommand(this));
        this.commands.add(new TimerCommand(this));
        this.commands.add(new CTimerCommand(this));
        this.commands.add(new DeathbanCommand(this));
        this.commands.add(new KitCommand(this));
        this.commands.add(new KothCommand(this));
        this.commands.add(new KingCommand(this));
        this.commands.add(new CitadelCommand(this));
        this.commands.add(new KeyAllCommand(this));
        this.commands.add(new PlaytimeCommand(this));
        this.commands.add(new CraftCommand(this));
        this.commands.add(new TopCommand(this));
        this.commands.add(new RenameCommand(this));
        this.commands.add(new RepairCommand(this));
        this.commands.add(new GappleCommand(this));
        this.commands.add(new GamemodeCommand(this));
        this.commands.add(new GMCCommand(this));
        this.commands.add(new GMSCommand(this));
        this.commands.add(new PvPCommand(this));
        this.commands.add(new BalanceCommand(this));
        this.commands.add(new EcoManageCommand(this));
        this.commands.add(new WorldCommand(this));
        this.commands.add(new TLCommand(this));
        this.commands.add(new SettingsCommand(this));
        this.commands.add(new LogoutCommand(this));
        this.commands.add(new TpCommand(this));
        this.commands.add(new TpHereCommand(this));
        this.commands.add(new TpRandomCommand(this));
        this.commands.add(new TpLocCommand(this));
        this.commands.add(new HealCommand(this));
        this.commands.add(new FeedCommand(this));
        this.commands.add(new BroadcastCommand(this));
        this.commands.add(new MessageCommand(this));
        this.commands.add(new ReplyCommand(this));
        this.commands.add(new IgnoreCommand(this));
        this.commands.add(new ClearCommand(this));
        this.commands.add(new ClearChatCommand(this));
        this.commands.add(new ToggleSoundsCommand(this));
        this.commands.add(new TogglePMCommand(this));
        this.commands.add(new LivesCommand(this));
        this.commands.add(new PayCommand(this));
        this.commands.add(new KillCommand(this));
        this.commands.add(new ReclaimCommand(this));
        this.commands.add(new ResetReclaimCommand(this));
        this.commands.add(new SpawnerCommand(this));
        this.commands.add(new AbilityCommand(this));
        this.commands.add(new AbilitiesCommand(this));
        this.commands.add(new LivesManageCommand(this));
        this.commands.add(new SOTWCommand(this));
        this.commands.add(new SetEndCommand(this));
        this.commands.add(new TpAllCommand(this));
        this.commands.add(new PingCommand(this));
        this.commands.add(new SchedulesCommand(this));
        this.commands.add(new StaffCommand(this));
        this.commands.add(new StaffBuildCommand(this));
        this.commands.add(new VanishCommand(this));
        this.commands.add(new StrengthNerfCommand(this));
        this.commands.add(new FreezeCommand(this));
        this.commands.add(new SpawnCommand(this));
        this.commands.add(new StatsCommand(this));
        this.commands.add(new LeaderboardsCommand(this));
    }
    
    public CommandManager(HCF plugin) {
        super(plugin);
        this.commands = new ArrayList<Command>();
        this.load();
        this.checkCommands();
        plugin.getVersionManager().getVersion().getCommandMap().register("?", (org.bukkit.command.Command)new TitanCommand(this).asBukkitCommand());
        plugin.getVersionManager().getVersion().getCommandMap().registerAll("titan", (List)this.commands.stream().map(Command::asBukkitCommand).collect(Collectors.toList()));
        this.commands.clear();
    }
    
    private void checkCommands() {
        List<String> disabled = this.getConfig().getStringList("DISABLED_COMMANDS.MAIN_COMMANDS");
        Iterator<Command> cmds = this.commands.iterator();
        while (cmds.hasNext()) {
            Command cmd = cmds.next();
            if (disabled.contains(cmd.getName().toLowerCase())) {
                cmds.remove();
            }
            else {
                for (String s : cmd.aliases()) {
                    if (!disabled.contains(s.toLowerCase())) {
                        continue;
                    }
                    cmds.remove();
                    break;
                }
            }
        }
    }
}
