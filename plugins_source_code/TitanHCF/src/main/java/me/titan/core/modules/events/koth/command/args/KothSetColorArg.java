package me.titan.core.modules.events.koth.command.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.koth.Koth;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KothSetColorArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETCOLOR.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        Koth koth = this.getInstance().getKothManager().getKoth(args[0]);
        String color = CC.t(args[1]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        koth.setColor(color);
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETCOLOR.UPDATED_COLOR").replaceAll("%koth%", koth.getName()).replaceAll("%color%", color));
    }
    
    public KothSetColorArg(CommandManager manager) {
        super(manager, Collections.singletonList("setcolor"));
        this.setPermissible("titan.koth.setcolor");
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
