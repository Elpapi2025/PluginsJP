package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import me.titan.core.modules.storage.Storage;
import me.titan.core.utils.CC;
import me.titan.core.utils.configs.ConfigYML;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TitanCommand extends Command {
    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }
    
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("COMMAND_USAGE.TITAN");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, CC.LINE);
            this.sendMessage(sender, "&eThis server is running &dTitanHCF&e.");
            this.sendMessage(sender, "&eMade by &dJuanPiece &efor titan.cc");
            this.sendMessage(sender, CC.LINE);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                long time = System.currentTimeMillis();
                for (ConfigYML config : this.getInstance().getConfigs()) {
                    config.reload();
                    config.reloadCache();
                }
                this.sendMessage(sender, "&dTitan &eha sido recargado en &a" + (System.currentTimeMillis() - time) + "ms&e.");
                this.sendMessage(sender, "&c¡TEN EN CUENTA QUE ESTO PODRÍA NO RECARGAR ALGUNAS COSAS! - SE REQUIERE UN REINICIO.");
                return;
            }
            case "version": {
                this.sendMessage(sender, "&dTitan &eis currently on version &a" + this.getInstance().getDescription().getVersion() + "&e.");
                return;
            }
            case "forcesave": {
                long time = System.currentTimeMillis();
                Storage storage = this.getInstance().getStorageManager().getStorage();
                storage.saveTimers();
                storage.saveTeams();
                storage.saveUsers();
                this.sendMessage(sender, "&dTitan &eha sido guardado en &a" + (System.currentTimeMillis() - time) + "ms&e.");
                return;
            }
        }
        this.sendUsage(sender);
    }
    
    public TitanCommand(CommandManager manager) {
        super(manager, "titan");
        this.completions.add(new TabCompletion(Arrays.asList("reload", "version", "forcesave"), 0));
        this.setPermissible("titan.reload");
    }
}
