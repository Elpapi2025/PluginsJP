package me.juanpiece.titan.modules.framework.menu;

import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.framework.menu.listener.MenuListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class MenuManager extends Manager {

    private final Map<UUID, Menu> menus;

    public MenuManager(HCF instance) {
        super(instance);
        this.menus = new HashMap<>();
        new MenuListener(this);
    }
}