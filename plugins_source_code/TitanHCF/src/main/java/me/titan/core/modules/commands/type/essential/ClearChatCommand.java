package me.titan.core.modules.commands.type.essential;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ClearChatCommand extends Command {
    private final String clearString;
    
    @Override
    public List<String> aliases() {
        return Collections.singletonList("cc");
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("CLEARCHAT_COMMAND.USAGE");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        Bukkit.broadcastMessage(this.clearString);
    }
    
    public ClearChatCommand(CommandManager sender) {
        super(sender, "clearchat");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 300; ++i) {
            builder.append(CC.t("&7 &d &f &3 &b &9 &6 \n"));
        }
        this.clearString = String.valueOf(builder);
        this.setPermissible("titan.clearchat");
    }
}
