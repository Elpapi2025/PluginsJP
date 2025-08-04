package me.titan.core.modules.events.koth.command.args;

import me.titan.core.modules.framework.commands.*;
import org.bukkit.command.*;
import me.titan.core.modules.framework.*;
import me.titan.core.modules.events.koth.*;
import java.util.function.*;
import java.util.stream.*;
import me.titan.core.modules.commands.*;
import java.util.*;

public class KothEndArg extends Argument {
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
        Koth koth = this.getInstance().getKothManager().getKoth(args[0]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        if (!koth.isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_END.NOT_ACTIVE"));
            return;
        }
        koth.end();
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_END.STOPPED").replaceAll("%koth%", koth.getName()));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().filter(Koth::isActive).map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_END.USAGE");
    }
    
    public KothEndArg(CommandManager manager) {
        super(manager, Arrays.asList("end", "stop"));
        this.setPermissible("titan.koth.end");
    }
}
