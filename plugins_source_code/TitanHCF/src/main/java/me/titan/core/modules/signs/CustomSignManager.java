package me.titan.core.modules.signs;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.signs.economy.EconomyBuySign;
import me.titan.core.modules.signs.economy.EconomySellSign;
import me.titan.core.modules.signs.elevators.ElevatorDownSign;
import me.titan.core.modules.signs.elevators.ElevatorUpSign;
import me.titan.core.modules.signs.kits.KitSign;
import me.titan.core.modules.signs.listener.CustomSignListener;
import me.titan.core.modules.signs.subclaim.SubclaimSign;

@Getter
public class CustomSignManager extends Manager {
    private EconomySellSign sellSign;
    private ElevatorUpSign upSign;
    private EconomyBuySign buySign;
    private ElevatorDownSign downSign;
    private KitSign kitSign;
    private SubclaimSign subclaimSign;
    
    public CustomSign getSign(String[] lines) {
        if (lines[this.kitSign.getKitIndex()].toLowerCase().contains("kit")) {
            return this.kitSign;
        }
        if (lines[0].toLowerCase().contains("buy")) {
            return this.buySign;
        }
        if (lines[0].toLowerCase().contains("sell")) {
            return this.sellSign;
        }
        if (lines[this.upSign.getElevatorIndex()].toLowerCase().contains("elevator")) {
            if (lines[this.upSign.getUpIndex()].toLowerCase().contains("up")) {
                return this.upSign;
            }
            if (lines[this.downSign.getDownIndex()].toLowerCase().contains("down")) {
                return this.downSign;
            }
        }
        return null;
    }
    
    public CustomSignManager(HCF plugin) {
        super(plugin);
        new CustomSignListener(this);
        if (this.getConfig().getBoolean("SIGNS_CONFIG.UP_SIGN.ENABLED")) {
            this.upSign = new ElevatorUpSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.DOWN_SIGN.ENABLED")) {
            this.downSign = new ElevatorDownSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.KIT_SIGN.ENABLED")) {
            this.kitSign = new KitSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.BUY_SIGN.ENABLED")) {
            this.buySign = new EconomyBuySign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.SELL_SIGN.ENABLED")) {
            this.sellSign = new EconomySellSign(this);
        }
        if (this.getConfig().getBoolean("SIGNS_CONFIG.SUBCLAIM_SIGN.ENABLED")) {
            this.subclaimSign = new SubclaimSign(this);
        }
    }
}