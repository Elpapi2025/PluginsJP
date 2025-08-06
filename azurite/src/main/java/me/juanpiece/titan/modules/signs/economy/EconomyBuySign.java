package me.juanpiece.titan.modules.signs.economy;

import lombok.Getter;
import me.juanpiece.titan.modules.balance.BalanceManager;
import me.juanpiece.titan.modules.signs.CustomSignManager;
import me.juanpiece.titan.utils.ItemUtils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class EconomyBuySign extends EconomySign {

    private final int materialIndex, amountIndex, priceIndex;

    public EconomyBuySign(CustomSignManager manager) {
        super(
                manager,
                manager.getConfig().getStringList("SIGNS_CONFIG.BUY_SIGN.LINES")
        );
        this.materialIndex = getIndex("%material%");
        this.amountIndex = getIndex("%amount%");
        this.priceIndex = getIndex("%price%");
    }

    @Override
    public void onClick(Player player, Sign sign) {
        ItemStack itemStack = getItemStack(sign.getLine(materialIndex));
        BalanceManager balanceManager = getInstance().getBalanceManager();
        int price = Integer.parseInt(sign.getLine(priceIndex).replace("$", ""));
        int amount = Integer.parseInt(sign.getLine(amountIndex));

        if (!balanceManager.hasBalance(player, price)) {
            String[] clone = sign.getLines().clone();
            clone[priceIndex] = getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.INSUFFICIENT_MONEY");
            sendSignChange(player, sign, clone);
            return;
        }

        String[] clone = sign.getLines().clone();

        clone[materialIndex + 1] = getLanguageConfig().getString("CUSTOM_SIGNS.ECONOMY_SIGNS.PURCHASED");
        balanceManager.takeBalance(player, price);
        itemStack.setAmount(amount);

        sendSignChange(player, sign, clone);
        ItemUtils.giveItem(player, itemStack, player.getLocation());
    }
}