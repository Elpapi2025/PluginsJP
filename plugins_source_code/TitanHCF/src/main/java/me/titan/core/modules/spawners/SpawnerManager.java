package me.titan.core.modules.spawners;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.spawners.listener.SpawnerListener;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SpawnerManager extends Manager {
    private final Map<EntityType, Spawner> spawners;
    
    public Spawner getByName(String name) {
        if (name.equalsIgnoreCase("skele")) {
            return this.spawners.get(EntityType.SKELETON);
        }
        try {
            return this.spawners.get(EntityType.valueOf(name.toUpperCase()));
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
    
    public Spawner getByItem(ItemStack stack) {
        for (Spawner spawner : this.spawners.values()) {
            if (!spawner.getItemStack().isSimilar(stack)) {
                continue;
            }
            return spawner;
        }
        return null;
    }
    
    private void load() {
        for (String s : this.getConfig().getStringList("SPAWNERS_CONFIG.TYPES")) {
            String[] types = s.split(", ");
            this.spawners.put(EntityType.valueOf(types[0]), new Spawner(this, EntityType.valueOf(types[0]), types[1]));
        }
    }
    
    public SpawnerManager(HCF plugin) {
        super(plugin);
        this.spawners = new HashMap<>();
        this.load();
        new SpawnerListener(this);
    }
}