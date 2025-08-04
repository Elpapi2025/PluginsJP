package me.titan.core.modules.teams.commands.systeam;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.teams.commands.systeam.args.*;

import java.util.Arrays;
import java.util.List;

public class SysTeamCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("SYSTEM_TEAM_COMMAND.USAGE");
    }
    
    public SysTeamCommand(CommandManager manager) {
        super(manager, "systemteam");
        this.setPermissible("titan.systeam");
        this.handleArguments(Arrays.asList(new SysTeamDeleteArg(manager), new SysTeamCreateArg(manager), new SysTeamClaimArg(manager), new SysTeamSetHqArg(manager), new SysTeamUnclaimArg(manager)));
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("systeam", "sysfac", "sysfaction", "st", "sf");
    }
}
