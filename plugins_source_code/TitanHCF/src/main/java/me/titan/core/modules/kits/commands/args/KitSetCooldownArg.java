package me.titan.core.modules.kits.commands.args;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Argument;
import me.titan.core.modules.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KitSetCooldownArg extends Argument
{
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getKitManager().getKits().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
    
    public KitSetCooldownArg(CommandManager manager) {
        super(manager, Arrays.asList("setcooldown", "setseconds"));
        this.setPermissible("titan.kit.setcooldown");
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
        String kitName = args[0];
        Integer cooldown = this.getInt(args[1]);
        Kit kit = this.getInstance().getKitManager().getKit(kitName);
        if (kit == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.NOT_FOUND").replaceAll("%kit%", kitName));
            return;
        }
        if (cooldown == null) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[1]));
            return;
        }
        kit.setSeconds(cooldown);
        kit.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETCOOLDOWN.SET_COOLDOWN").replaceAll("%kit%", kit.getName()).replaceAll("%seconds%", String.valueOf(cooldown)));
    }
    
    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETCOOLDOWN.USAGE");
    }
}
