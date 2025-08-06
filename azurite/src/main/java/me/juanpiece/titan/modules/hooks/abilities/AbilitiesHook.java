package me.juanpiece.titan.modules.hooks.abilities;

import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.hooks.abilities.type.PandaAbilities;
import me.juanpiece.titan.modules.hooks.abilities.type.SladeAbilities;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AbilitiesHook extends Manager implements Abilities {

    private final List<Abilities> hooked;

    public AbilitiesHook(HCF instance) {
        super(instance);
        this.hooked = new ArrayList<>();
        this.load();
    }

    private void load() {
        if (Utils.verifyPlugin("PandaAbility", getInstance())) {
            hooked.add(new PandaAbilities(this));
        }

        if (Utils.verifyPlugin("Slade", getInstance())) {
            hooked.add(new SladeAbilities(this));
        }
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> list = new ArrayList<>();

        for (Abilities abilities : hooked) {
            list.addAll(abilities.getScoreboardLines(player));
        }

        return list;
    }

    @Override
    public String getRemainingGlobal(Player player) {
        for (Abilities abilities : hooked) {
            if (abilities.hasGlobalCooldown(player)) {
                return abilities.getRemainingGlobal(player);
            }
        }

        return null;
    }

    @Override
    public boolean hasGlobalCooldown(Player player) {
        for (Abilities abilities : hooked) {
            if (abilities.hasGlobalCooldown(player)) {
                return true;
            }
        }

        return false;
    }
}