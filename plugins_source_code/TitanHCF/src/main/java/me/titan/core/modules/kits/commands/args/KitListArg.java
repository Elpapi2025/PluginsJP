package me.titan.core.modules.kits.commands.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class KitListArg extends Argument {
    @Override
    public String usage() {
        return null;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        for (String s : this.getLanguageConfig().getStringList("KIT_COMMAND.KIT_LIST.KITS_LIST")) {
            if (!s.equalsIgnoreCase("%kits%")) {
                this.sendMessage(sender, s);
                return;
            }
            for (Kit kit : this.getInstance().getKitManager().getKits().values()) {
                this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_LIST.KITS_FORMAT").replaceAll("%kit%", kit.getName()).replaceAll("%seconds%", String.valueOf(kit.getSeconds())));
            }
        }
    }
    
    public KitListArg(CommandManager manager) {
        super(manager, Collections.singletonList("list"));
        this.setPermissible("titan.kit.list");
    }
}
