package me.titan.core.modules.framework;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.extra.Configs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

@Getter
public abstract class HCFModule<T extends Manager> extends Configs implements Listener {
    protected T manager;
    protected HCF instance;
    
    private void checkListener() {
        Method[] methods = this.getClass().getMethods();
        for(Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                this.manager.registerListener(this);
                break;
            }
        }
    }
    
    public HCFModule(T manager) {
        this.instance = manager.getInstance();
        this.manager = manager;
        this.checkListener();
    }
}
