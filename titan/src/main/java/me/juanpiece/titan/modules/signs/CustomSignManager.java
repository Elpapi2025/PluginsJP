package me.juanpiece.titan.modules.signs;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.signs.economy.EconomyBuySign;
import me.juanpiece.titan.modules.signs.economy.EconomySellSign;
import me.juanpiece.titan.modules.signs.elevators.ElevatorDownSign;
import me.juanpiece.titan.modules.signs.elevators.ElevatorUpSign;
import me.juanpiece.titan.modules.signs.kitmap.QuickRefillSign;
import me.juanpiece.titan.modules.signs.kitmap.RefillSign;
import me.juanpiece.titan.modules.signs.kits.KitSign;
import me.juanpiece.titan.modules.signs.listener.CustomSignListener;
import me.juanpiece.titan.modules.signs.subclaim.SubclaimSign;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class CustomSignManager extends Manager {

    private ElevatorUpSign upSign;
    private ElevatorDownSign downSign;

    private EconomyBuySign buySign;
    private EconomySellSign sellSign;

    private KitSign kitSign;
    private SubclaimSign subclaimSign;

    private RefillSign refillSign;
    private QuickRefillSign quickRefillSign;

    public CustomSignManager(HCF instance) {
        super(instance);

        if (getConfig().getBoolean("SIGNS_CONFIG.UP_SIGN.ENABLED")) {
            this.upSign = new ElevatorUpSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.DOWN_SIGN.ENABLED")) {
            this.downSign = new ElevatorDownSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.KIT_SIGN.ENABLED")) {
            this.kitSign = new KitSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.BUY_SIGN.ENABLED")) {
            this.buySign = new EconomyBuySign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.SELL_SIGN.ENABLED")) {
            this.sellSign = new EconomySellSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.SUBCLAIM_SIGN.ENABLED")) {
            this.subclaimSign = new SubclaimSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.REFILL_SIGN.ENABLED")) {
            this.refillSign = new RefillSign(this);
        }

        if (getConfig().getBoolean("SIGNS_CONFIG.QUICK_REFILL_SIGN.ENABLED")) {
            this.quickRefillSign = new QuickRefillSign(this);
        }

        new CustomSignListener(this);
    }

    public CustomSign getCreation(Player player, String[] lines) {
        if (kitSign != null && lines[0].toLowerCase().contains("kit") && player.hasPermission("titan.customsigns")) {
            return kitSign;
        }

        if (refillSign != null && lines[0].equalsIgnoreCase("[refill]") && player.hasPermission("titan.customsigns")) {
            return refillSign;
        }

        if (quickRefillSign != null && lines[0].equalsIgnoreCase("[quickrefill]") && player.hasPermission("titan.customsigns")) {
            return quickRefillSign;
        }

        if (upSign != null && lines[0].toLowerCase().contains("elevator") && lines[1].toLowerCase().contains("up")) {
            return upSign;
        }

        if (downSign != null && lines[0].toLowerCase().contains("elevator") && lines[1].toLowerCase().contains("down")) {
            return downSign;
        }

        return null;
    }

    public CustomSign getSign(String[] lines) {
        if (buySign != null && lines[0].equals(buySign.getLines().get(0))) {
            return buySign;
        }

        if (sellSign != null && lines[0].equals(sellSign.getLines().get(0))) {
            return sellSign;
        }

        if (kitSign != null && lines[kitSign.getKitIndex()].equals(kitSign.getLines().get(kitSign.getKitIndex()))) {
            return kitSign;
        }

        if (refillSign != null && refillSign.equals(lines)) {
            return refillSign;
        }

        if (quickRefillSign != null && quickRefillSign.equals(lines)) {
            return quickRefillSign;
        }

        if (upSign != null && upSign.equals(lines)) {
            return upSign;
        }

        if (downSign != null && downSign.equals(lines)) {
            return downSign;
        }

        return null;
    }
}