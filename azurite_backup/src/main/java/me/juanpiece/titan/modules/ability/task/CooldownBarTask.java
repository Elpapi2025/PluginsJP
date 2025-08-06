package me.juanpiece.titan.modules.ability.task;

import me.juanpiece.titan.modules.ability.Ability;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.settings.ActionBar;
import me.juanpiece.titan.modules.versions.Version;
import me.juanpiece.titan.utils.CC;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CooldownBarTask extends BukkitRunnable {

    private final Player player;
    private final Ability ability;

    public CooldownBarTask(Player player, Ability ability) {
        this.player = player;
        this.ability = ability;
        this.runTaskTimerAsynchronously(ability.getInstance(), 0L, 2L);
    }

    private void destroy() {
        this.cancel();
        User user = ability.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (user.getActionBar() == ActionBar.ABILITY) user.setActionBar(null);
    }

    @Override
    public void run() {
        if (ability.getInstance().getStaffManager().isStaffEnabled(player)) return;

        if (!player.isOnline()) {
            destroy();
            return;
        }

        ItemStack hand = ability.getManager().getItemInHand(player);
        Version version = ability.getInstance().getVersionManager().getVersion();

        if (hand == null || !ability.isSimilar(hand)) {
            destroy();
            return;
        }

        if (!ability.getAbilityCooldown().hasTimer(player)) {
            destroy();
            version.sendActionBar(player, CC.t("&7"));
            return;
        }

        User user = ability.getInstance().getUserManager().getByUUID(player.getUniqueId());
        ActionBar actionBar = user.getActionBar();

        if (actionBar == null) user.setActionBar(ActionBar.ABILITY);

        if (actionBar == ActionBar.ABILITY) {
            version.sendActionBar(player, Config.ABILITY_ACTION_BAR_STRING
                    .replace("%abilityname%", ability.getDisplayName())
                    .replace("%remaining%", ability.getAbilityCooldown().getRemainingStringBoard(player))
                    .replace("%actionbar%", Utils.getProgressBar(
                            ability.getAbilityCooldown().getRemaining(player),
                            ability.getAbilityCooldown().getSeconds() * 1000L,
                            Config.ABILITY_ACTION_BAR_BARS,
                            Config.ABILITY_ACTION_BAR_SYMBOL,
                            Config.ABILITY_ACTION_BAR_YES_COLOR,
                            Config.ABILITY_ACTION_BAR_NO_COLOR
                    ))
            );
        }
    }
}