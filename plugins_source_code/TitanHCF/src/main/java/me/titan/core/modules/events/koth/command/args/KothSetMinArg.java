package me.titan.core.modules.events.koth.command.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.koth.Koth;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KothSetMinArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETMIN.USAGE");
    }
    
    public KothSetMinArg(CommandManager manager) {
        super(manager, Arrays.asList("setminutes", "setmins"));
        this.setPermissible("titan.koth.setminutes");
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
        Integer minutes = this.getInt(args[1]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        if (minutes == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        koth.setMinutes(60000L * minutes);
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETMIN.UPDATED_MIN").replaceAll("%koth%", koth.getName()).replaceAll("%min%", String.valueOf(minutes)));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
