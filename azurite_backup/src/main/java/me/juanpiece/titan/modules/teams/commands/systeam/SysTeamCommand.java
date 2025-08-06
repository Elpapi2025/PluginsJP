package me.juanpiece.titan.modules.teams.commands.systeam;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.teams.commands.systeam.args.*;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SysTeamCommand extends Command {

    public SysTeamCommand(CommandManager manager) {
        super(
                manager,
                "systemteam"
        );
        this.setPermissible("titan.systeam");
        this.handleArguments(Arrays.asList(
                new SysTeamDeleteArg(manager),
                new SysTeamCreateArg(manager),
                new SysTeamClaimArg(manager),
                new SysTeamSetHqArg(manager),
                new SysTeamUnclaimArg(manager),
                new SysTeamAbilityArg(manager),
                new SysTeamListArg(manager),
                new SysTeamSetColorArg(manager)
        ));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "systeam",
                "sysfac",
                "sysfaction",
                "st",
                "sf"
        );
    }

    @Override
    public List<String> usage() {
        return getLanguageConfig().getStringList("SYSTEM_TEAM_COMMAND.USAGE");
    }
}