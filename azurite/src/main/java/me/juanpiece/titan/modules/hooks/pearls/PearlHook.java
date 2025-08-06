package me.juanpiece.titan.modules.hooks.pearls;

import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.hooks.pearls.type.NonePearlHook;
import me.juanpiece.titan.modules.hooks.pearls.type.VortexPearlHook;
import me.juanpiece.titan.utils.Utils;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class PearlHook extends Manager implements Pearl {

    private Pearl pearl;

    public PearlHook(HCF instance) {
        super(instance);
        this.load();
        this.loadHook();
    }

    private void load() {
        if (Utils.verifyPlugin("VortexPearls", getInstance())) {
            pearl = new VortexPearlHook(this);

        } else {
            pearl = new NonePearlHook(this);
        }
    }

    @Override
    public void loadHook() {
        pearl.loadHook();
    }
}