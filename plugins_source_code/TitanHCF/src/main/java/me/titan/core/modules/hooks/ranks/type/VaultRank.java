package me.titan.core.modules.hooks.ranks.type;

import me.titan.core.modules.hooks.ranks.*;
import net.milkbowl.vault.chat.*;
import org.bukkit.entity.*;
import org.bukkit.*;

public class VaultRank implements Rank {
    private final Chat chat;
    
    @Override
    public String getRankSuffix(Player player) {
        return this.chat != null ? this.chat.getPlayerSuffix(player) : "";
    }
    
    public VaultRank() {
        this.chat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class) != null ? (Chat)Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider() : null;
    }
    
    @Override
    public String getRankColor(Player player) {
        return "";
    }
    
    @Override
    public String getRankName(Player player) {
        return this.chat != null ? this.chat.getPrimaryGroup(player) : "";
    }
    
    @Override
    public String getRankPrefix(Player player) {
        return this.chat != null ? this.chat.getPlayerPrefix(player) : "";
    }
}
