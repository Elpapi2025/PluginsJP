package me.keano.azurite.modules.framework;

import lombok.Getter;
import me.keano.azurite.HCF;
import me.keano.azurite.modules.framework.extra.Configs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class Module<T extends Manager> extends Configs implements Listener {

    private final HCF instance;
    private final T manager;

    public Module(T manager) {
        this.instance = manager.getInstance();
        this.manager = manager;
        this.checkListener();
    }

    private void checkListener() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                manager.registerListener(this);
                break; // Break the loop, we already know it's a listener now.
            }
        }
    }
}