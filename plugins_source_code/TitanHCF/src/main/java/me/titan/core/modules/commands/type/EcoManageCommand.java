package me.titan.core.modules.commands.type;

import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.framework.commands.Command;
import me.titan.core.modules.framework.commands.extra.TabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class EcoManageCommand extends Command {
    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("ECOMANAGE_COMMAND.USAGE");
    }
    
    @Override
    public List<String> aliases() {
        return Arrays.asList("ecomanager", "balmanager");
    }
    
    public EcoManageCommand(CommandManager llllllllllllllllIlIlIIlIIIIllllI) {
        super(llllllllllllllllIlIlIIlIIIIllllI, "ecomanage");
        this.setPermissible("titan.ecomanage");
        this.completions.add(new TabCompletion(Arrays.asList("set", "add", "plus", "remove", "take"), 0));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 3) {
            this.sendUsage(sender);
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        Integer bal = this.getInt(args[2]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[1]));
            return;
        }
        if (bal == null || bal <= 0) {
            this.sendMessage(sender, Config.NOT_VALID_NUMBER.replaceAll("%number%", args[2]));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "take":
            case "remove": {
                this.getInstance().getBalanceManager().takeBalance(target, bal);
                this.sendMessage(sender, this.getLanguageConfig().getString("ECOMANAGE_COMMAND.REMOVED_BAL").replaceAll("%amount%", String.valueOf(bal)).replaceAll("%target%", target.getName()));
                return;
            }
            case "plus":
            case "add": {
                this.getInstance().getBalanceManager().giveBalance(target, bal);
                this.sendMessage(sender, this.getLanguageConfig().getString("ECOMANAGE_COMMAND.ADDED_BAL").replaceAll("%amount%", String.valueOf(bal)).replaceAll("%target%", target.getName()));
                return;
            }
            case "set": {
                this.getInstance().getBalanceManager().setBalance(target, bal);
                this.sendMessage(sender, this.getLanguageConfig().getString("ECOMANAGE_COMMAND.SET_BAL").replaceAll("%amount%", String.valueOf(bal)).replaceAll("%target%", target.getName()));
                return;
            }
        }
        this.sendUsage(sender);
    }
}
