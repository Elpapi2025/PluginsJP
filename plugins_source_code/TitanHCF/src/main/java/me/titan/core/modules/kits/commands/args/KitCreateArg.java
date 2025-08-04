package me.titan.core.modules.kits.commands.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class KitCreateArg extends Argument {
    public KitCreateArg(CommandManager manager) {
        super(manager, Collections.singletonList("create"));
        this.setPermissible("titan.kit.create");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        String kitName = args[0];
        if (this.getInstance().getKitManager().getKit(kitName) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.ALREADY_EXISTS").replaceAll("%kit%", kitName));
            return;
        }
        Kit kit = new Kit(this.getInstance().getKitManager(), kitName);
        kit.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.CREATED").replaceAll("%kit%", kitName));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_CREATE.USAGE");
    }
}
