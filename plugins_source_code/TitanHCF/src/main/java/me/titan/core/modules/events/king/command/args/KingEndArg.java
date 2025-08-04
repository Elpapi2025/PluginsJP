package me.titan.core.modules.events.king.command.args;

import me.titan.core.modules.framework.commands.*;
import me.titan.core.modules.commands.*;
import java.util.*;
import org.bukkit.command.*;

public class KingEndArg extends Argument {
    @Override
    public String usage() {
        return null;
    }
    
    public KingEndArg(CommandManager manager) {
        super(manager, Arrays.asList("stop", "end"));
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!this.getInstance().getKingManager().isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KING_COMMAND.KING_END.NOT_ACTIVE"));
            return;
        }
        this.getInstance().getKingManager().stopKing(true);
        this.getInstance().getNametagManager().update();
    }
}
