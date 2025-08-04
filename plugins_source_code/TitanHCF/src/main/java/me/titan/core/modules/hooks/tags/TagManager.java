package me.titan.core.modules.hooks.tags;

import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.hooks.tags.type.*;
import me.titan.core.utils.Utils;
import org.bukkit.entity.Player;

public class TagManager extends Manager implements Tag {
    private Tag tag;
    
    @Override
    public String getTag(Player player) {
        return this.tag.getTag(player);
    }
    
    public TagManager(HCF plugin) {
        super(plugin);
        this.load();
    }
    
    private void load() {
        if (Utils.verifyPlugin("AquaCore", this.getInstance())) {
            this.tag = new AquaCoreTag();
        }
        else if (Utils.verifyPlugin("Mizu", this.getInstance())) {
            this.tag = new MizuTag();
        }
        else if (Utils.verifyPlugin("Basic", this.getInstance())) {
            this.tag = new CoreTag();
        }
        else if (Utils.verifyPlugin("DeluxeTags", this.getInstance())) {
            this.tag = new DeluxeTag();
        }
        else {
            this.tag = new NoneTag();
        }
    }
}
