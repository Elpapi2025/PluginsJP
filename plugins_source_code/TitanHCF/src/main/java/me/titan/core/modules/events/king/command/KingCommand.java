package me.titan.core.modules.events.king.command;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.king.command.args.KingEndArg;
import me.titan.core.modules.events.king.command.args.KingStartArg;
import me.titan.core.modules.framework.commands.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KingCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KING_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("ktk");
    }
    
    public KingCommand(CommandManager manager) {
        super(manager, "king");
        this.setPermissible("titan.king");
        this.handleArguments(Arrays.asList(new KingEndArg(manager), new KingStartArg(manager)));
    }
}
