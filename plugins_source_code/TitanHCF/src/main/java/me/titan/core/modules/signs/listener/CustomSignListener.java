package me.titan.core.modules.signs.listener;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.signs.CustomSign;
import me.titan.core.modules.signs.CustomSignManager;
import me.titan.core.modules.signs.economy.EconomyBuySign;
import me.titan.core.modules.signs.economy.EconomySellSign;
import me.titan.core.modules.signs.economy.EconomySign;
import me.titan.core.modules.signs.kits.KitSign;
import me.titan.core.utils.CC;
import me.titan.core.utils.Utils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class CustomSignListener extends HCFModule<CustomSignManager> {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        CustomSign sign = this.getManager().getSign(event.getLines());
        if (sign != null && player.hasPermission("titan.customsigns")) {
            List<String> lines = sign.getLines();
            if (sign instanceof EconomySign) {
                return;
            }
            for (int i = 0; i < lines.size(); ++i) {
                if (sign instanceof KitSign) {
                    KitSign kitSign = (KitSign)sign;
                    if (kitSign.getKitTypeIndex() == i) {
                        continue;
                    }
                }
                event.setLine(i, lines.get(i));
            }
        }
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!event.getClickedBlock().getType().name().contains("SIGN")) {
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign)event.getClickedBlock().getState();
        CustomSign customSign = this.getManager().getSign(sign.getLines());
        if (customSign == null) {
            return;
        }
        if (customSign instanceof KitSign) {
            KitSign kitSign = (KitSign)customSign;
            if (sign.getLine(kitSign.getKitIndex()).equals(kitSign.getLines().get(kitSign.getKitIndex()))) {
                customSign.onClick(player, sign);
                return;
            }
        }
        if (customSign instanceof EconomySellSign) {
            EconomySellSign sellSIgn = (EconomySellSign)customSign;
            if (!sign.getLine(0).equals(sellSIgn.getLines().get(0))) {
                return;
            }
            String material = sign.getLine(sellSIgn.getMaterialIndex());
            String amount = sign.getLine(sellSIgn.getAmountIndex());
            String price = sign.getLine(sellSIgn.getPriceIndex());
            if (sellSIgn.getItemStack(material) == null) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT"));
                return;
            }
            if (Utils.isntNumber(amount)) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT"));
                return;
            }
            if (Utils.isntNumber(price.replaceAll("\\$", ""))) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE"));
                return;
            }
            customSign.onClick(player, sign);
        }
        else {
            if (!(customSign instanceof EconomyBuySign)) {
                if (Arrays.equals(sign.getLines(), customSign.getLines().toArray(new String[0]))) {
                    customSign.onClick(player, sign);
                }
                return;
            }
            EconomyBuySign buySign = (EconomyBuySign)customSign;
            if (!sign.getLine(0).equals(buySign.getLines().get(0))) {
                return;
            }
            String material = sign.getLine(buySign.getMaterialIndex());
            String amount = sign.getLine(buySign.getAmountIndex());
            String price = sign.getLine(buySign.getPriceIndex());
            if (buySign.getItemStack(material) == null) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT"));
                return;
            }
            if (Utils.isntNumber(amount)) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT"));
                return;
            }
            if (Utils.isntNumber(price.replaceAll("\\$", ""))) {
                player.sendMessage(this.getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE"));
                return;
            }
            customSign.onClick(player, sign);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignTranslate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("titan.customsigns")) {
            for (int i = 0; i < event.getLines().length; ++i) {
                event.setLine(i, CC.t(event.getLine(i)));
            }
        }
    }
    
    public CustomSignListener(CustomSignManager manager) {
        super(manager);
    }
}
