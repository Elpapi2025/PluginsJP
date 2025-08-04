package me.titan.core.modules.kits.commands;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.kits.commands.args.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("KIT_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("kits");
    }
    
    public KitCommand(CommandManager manager) {
        super(manager, "kit");
        this.setPermissible("titan.kit");
        this.handleArguments(Arrays.asList(new KitCreateArg(manager), new KitDeleteArg(manager), new KitSetItemsArg(manager), new KitApplyArg(manager), new KitSetCooldownArg(manager), new KitSetNameArg(manager), new KitListArg(manager)));
    }
}
