package me.titan.core.modules.events.koth.command.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.events.koth.Koth;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class KothCreateArg extends Argument {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 4) {
            this.sendUsage(sender);
            return;
        }
        String name = args[0];
        String color = CC.t(args[1]);
        Integer minutes = this.getInt(args[2]);
        Integer pointRewards = this.getInt(args[3]);
        if (minutes == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        if (pointRewards == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[3]));
            return;
        }
        if (this.getInstance().getKothManager().getKoth(name) != null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CREATE.ALREADY_EXISTS"));
            return;
        }
        Koth koth = new Koth(this.getInstance().getKothManager(), name, color, pointRewards, minutes);
        koth.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CREATE.CREATED").replaceAll("%koth%", koth.getName()));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KOTH_COMMAND.KOTH_CREATE.USAGE");
    }
    
    public KothCreateArg(CommandManager manage) {
        super(manage, Collections.singletonList("create"));
        this.setPermissible("titan.koth.create");
    }
}
