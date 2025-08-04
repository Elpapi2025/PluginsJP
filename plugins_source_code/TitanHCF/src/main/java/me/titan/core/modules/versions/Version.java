package me.titan.core.modules.versions;

import me.titan.core.modules.loggers.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.command.*;

public interface Version {

    void handleLoggerDeath(Logger logger);
    
    void hideArmor(Player player);
    
    boolean isNotGapple(ItemStack stack);
    
    void playEffect(Location location, String p1, Object p2);
    
    CommandMap getCommandMap();
    
    ItemStack getItemInHand(Player player);
    
    String getTPSColored();
    
    void showArmor(Player player);
    
    int getPing(Player player);
    
    void setItemInHand(Player player, ItemStack stack);
}