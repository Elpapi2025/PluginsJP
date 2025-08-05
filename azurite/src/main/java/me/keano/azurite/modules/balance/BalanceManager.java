package me.keano.azurite.modules.balance;

import me.keano.azurite.HCF;
import me.keano.azurite.modules.balance.type.VaultBalance;
import me.keano.azurite.modules.framework.Manager;
import me.keano.azurite.modules.users.User;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.util.UUID;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BalanceManager extends Manager {

    public BalanceManager(HCF instance) {
        super(instance);

        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");

        if (vault != null && vault.isEnabled()) {
            Bukkit.getServicesManager().register(Economy.class, new VaultBalance(this), getInstance(), ServicePriority.Normal);
        }
    }

    public void takeBalance(OfflinePlayer player, int amount) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() - amount, 0));
        user.save();
    }

    public void giveBalance(OfflinePlayer player, int amount) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(user.getBalance() + amount, 0));
        user.save();
    }

    public void giveBalance(UUID uuid, int amount) {
        User user = getInstance().getUserManager().getByUUID(uuid);
        user.setBalance(Math.max(user.getBalance() + amount, 0));
        user.save();
    }

    public void setBalance(OfflinePlayer player, int amount) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setBalance(Math.max(amount, 0));
        user.save();
    }

    public boolean hasBalance(OfflinePlayer player, int amount) {
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        return user.getBalance() >= amount;
    }

    public int getBalance(UUID player) {
        User user = getInstance().getUserManager().getByUUID(player);
        return user.getBalance();
    }
}