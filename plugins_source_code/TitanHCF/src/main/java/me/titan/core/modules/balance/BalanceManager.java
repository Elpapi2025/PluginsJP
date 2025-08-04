package me.titan.core.modules.balance;

import me.titan.core.HCF;
import me.titan.core.modules.balance.type.VaultBalance;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.users.User;
import me.titan.core.utils.Utils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceManager extends Manager {
    public boolean hasBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        return user.getBalance() >= amount;
    }
    
    public BalanceManager(HCF plugin) {
        super(plugin);
        if (Utils.verifyPlugin("Vault", plugin)) {
            new VaultBalance(this);
        }
    }
    
    public void takeBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() - amount, 0));
        user.save();
    }
    
    public int getBalance(UUID uuid) {
        User user = this.getInstance().getUserManager().getByUUID(uuid);
        return user.getBalance();
    }
    
    public void giveBalance(Player player, int amount) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() + amount, 0));
        user.save();
    }
    
    public void setBalance(Player player, int balance) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(balance, 0));
        user.save();
    }
}
