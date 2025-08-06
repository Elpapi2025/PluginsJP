package me.juanpiece.titan.modules.hooks.pearls.type;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.hooks.pearls.Pearl;
import me.juanpiece.titan.modules.hooks.pearls.PearlHook;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NonePearlHook extends Module<PearlHook> implements Pearl {

    public NonePearlHook(PearlHook manager) {
        super(manager);
    }

    @Override
    public void loadHook() {
        // Empty
    }
}