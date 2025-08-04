package me.titan.core.modules.framework.menu;

import lombok.Getter;
import me.titan.core.modules.framework.*;
import me.titan.core.*;
import java.util.*;
import me.titan.core.modules.framework.menu.listener.*;

@Getter
public class MenuManager extends Manager {
    private final Map<UUID, Menu> menus;
    
    public MenuManager(HCF plugin) {
        super(plugin);
        this.menus = new HashMap<>();
        new MenuListener(this);
    }
    
}
