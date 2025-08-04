package me.titan.core.modules.spawners;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.utils.ItemBuilder;
import me.titan.core.utils.ItemUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class Spawner extends HCFModule<SpawnerManager> {
    private ItemStack itemStack;
    private String name;
    private EntityType type;
    
    public Spawner(SpawnerManager manager, EntityType type, String name) {
        super(manager);
        this.type = type;
        this.name = name;
        this.itemStack = this.getSpawnerItem();
    }
    
    private ItemStack getSpawnerItem() {
        ItemBuilder builder = new ItemBuilder(ItemUtils.getMat("MOB_SPAWNER")).setName(this.getConfig().getString("SPAWNERS_CONFIG.ITEM.NAME"));
        for (String s : this.getConfig().getStringList("SPAWNERS_CONFIG.ITEM.LORE")) {
            builder.addLoreLine(s.replaceAll("%spawner%", this.name));
        }
        return builder.toItemStack();
    }
}
