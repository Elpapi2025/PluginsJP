package me.titan.core.modules.framework.commands;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.commands.CommandManager;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
public abstract class Argument extends HCFModule<CommandManager> {
    protected String permissible;
    protected List<String> names;
    protected boolean async;
    
    
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
    
    public void execute(CommandSender sender, String[] arg) {
        this.sendMessage(sender, "woah command argument! - JuanPiece..");
    }
    
    public void sendMessage(CommandSender sender, String... args) {
        for(String s : args) {
            sender.sendMessage(CC.t(s));
        }
    }
    
    public Double getDouble(String number) {
        try {
            return Double.parseDouble(number);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }
    
    public Argument(CommandManager manager, List<String> names) {
        super(manager);
        this.names = names;
        this.async = false;
        this.permissible = null;
    }
    
    public Integer getInt(String number) {
        try {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }
    
    public void sendUsage(CommandSender sender) {
        sender.sendMessage(CC.t(this.usage()));
    }

    public abstract String usage();
}