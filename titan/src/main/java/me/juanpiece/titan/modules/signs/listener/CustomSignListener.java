package me.juanpiece.titan.modules.signs.listener;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.signs.CustomSign;
import me.juanpiece.titan.modules.signs.CustomSignManager;
import me.juanpiece.titan.modules.signs.economy.EconomyBuySign;
import me.juanpiece.titan.modules.signs.economy.EconomySellSign;
import me.juanpiece.titan.modules.signs.kits.KitSign;
import me.juanpiece.titan.utils.CC;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CustomSignListener extends Module<CustomSignManager> {

    public CustomSignListener(CustomSignManager manager) {
        super(manager);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        CustomSign cSign = getManager().getCreation(player, e.getLines());

        if (cSign != null) {
            List<String> lines = cSign.getLines();

            if (cSign instanceof KitSign) {
                for (int i = 0; i < lines.size(); i++) {
                    KitSign kitSign = (KitSign) cSign;
                    if (kitSign.getKitTypeIndex() == i) continue; // don't override the kit index..
                    e.setLine(i, lines.get(i));
                }
                return;
            }

            for (int i = 0; i < lines.size(); i++) {
                e.setLine(i, lines.get(i));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // call last
    public void onSignTranslate(SignChangeEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("titan.customsigns")) {
            for (int i = 0; i < e.getLines().length; i++) {
                e.setLine(i, CC.t(e.getLine(i)));
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        if (!e.getClickedBlock().getType().name().contains("SIGN")) return; // uses Type#name for multi ver support.

        Player player = e.getPlayer();
        Sign sign = (Sign) e.getClickedBlock().getState();
        CustomSign cSign = getManager().getSign(sign.getLines());

        if (cSign == null) return;

        e.setCancelled(true);

        if (cSign instanceof EconomySellSign) {
            EconomySellSign sellSign = (EconomySellSign) cSign;

            String material = sign.getLine(sellSign.getMaterialIndex());
            String amount = sign.getLine(sellSign.getAmountIndex());
            String price = sign.getLine(sellSign.getPriceIndex());

            if (sellSign.getItemStack(material) == null) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT"));
                return;
            }

            if (Utils.isntNumber(amount)) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT"));
                return;
            }

            if (Utils.isntNumber(price.replace("$", ""))) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE"));
                return;
            }

            cSign.onClick(player, sign);
            return;
        }

        if (cSign instanceof EconomyBuySign) {
            EconomyBuySign buySign = (EconomyBuySign) cSign;

            String material = sign.getLine(buySign.getMaterialIndex());
            String amount = sign.getLine(buySign.getAmountIndex());
            String price = sign.getLine(buySign.getPriceIndex());

            if (buySign.getItemStack(material) == null) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT"));
                return;
            }

            if (Utils.isntNumber(amount)) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT"));
                return;
            }

            if (Utils.isntNumber(price.replace("$", ""))) {
                player.sendMessage(getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE"));
                return;
            }

            cSign.onClick(player, sign);
            return;
        }

        cSign.onClick(player, sign);
    }
}