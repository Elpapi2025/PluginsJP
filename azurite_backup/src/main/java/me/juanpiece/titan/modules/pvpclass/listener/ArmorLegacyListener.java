package me.juanpiece.titan.modules.pvpclass.listener;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.pvpclass.PvPClass;
import me.juanpiece.titan.modules.pvpclass.PvPClassManager;
import me.juanpiece.titan.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ArmorLegacyListener extends Module<PvPClassManager> {

    public ArmorLegacyListener(PvPClassManager manager) {
        super(manager);
    }

    @EventHandler
    public void onExpire(PotionEffectExpireEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        PotionEffect restore = getManager().getRestores().remove(player.getUniqueId(), e.getEffect().getType());
        PvPClass active = getManager().getActiveClass(player);

        if (active != null) {
            Tasks.execute(getManager(), () -> {
                if (active.hasArmor(player)) active.addEffects(player);
            });
        }

        if (restore != null) {
            Tasks.execute(getManager(), () -> {
                // Attempted fix for resistance bug (SagePvP)
                if (active != null && active.getEffects().stream().anyMatch(effect -> effect.getType().equals(restore.getType()))) {
                    if (!active.hasArmor(player)) return;
                }

                player.addPotionEffect(restore);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquipFix(EquipmentSetEvent e) {
        Player player = (Player) e.getHumanEntity();
        ItemStack previous = e.getPreviousItem();

        if (previous == null) return;

        // best way I can think of, no reason to remove armor unless you are trying to bug it out.
        getManager().getRestores().rowKeySet().remove(player.getUniqueId());
    }

    @EventHandler
    public void onEquip(EquipmentSetEvent e) {
        // There's a bug where this event will get called when u switch worlds
        // It will make the class unequip/equip again, this will fix that.
        if (e.getNewItem() != null && e.getPreviousItem() != null &&
                e.getNewItem().getType() == e.getPreviousItem().getType()) return;

        getManager().checkArmor((Player) e.getHumanEntity());
    }
}