package me.titan.core.modules.events.koth.command.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.koth.Koth;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.utils.Formatter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KothSetRemArg extends Argument {
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETREM.USAGE");
    }
    
    public KothSetRemArg(CommandManager manager) {
        super(manager, Arrays.asList("setremaining", "setrem"));
        this.setPermissible("titan.koth.setremaining");
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
        Long reaming = Formatter.parse(args[1]);
        if (koth == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_NOT_FOUND").replaceAll("%koth%", args[0]));
            return;
        }
        if (reaming == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        if (!koth.isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETREM.NOT_ACTIVE"));
            return;
        }
        koth.setRemaining(System.currentTimeMillis() + reaming);
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_SETREM.UPDATED_REM").replaceAll("%koth%", koth.getName()).replaceAll("%rem%", Formatter.formatMMSS(reaming)));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return this.getInstance().getKothManager().getKoths().values().stream().filter(Koth::isActive).map(Koth::getName).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
