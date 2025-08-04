package me.titan.core.modules.signs;

import lombok.Getter;
import me.titan.core.modules.framework.HCFModule;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class CustomSign extends HCFModule<CustomSignManager> {
    protected List<String> lines;
    
    public void setLines(List<String> lines) {
        this.lines = lines;
    }
    
    public abstract void onClick(Player player, Sign sign);
    
    public Integer getIndex(String input) {
        return this.lines.indexOf(this.lines.stream().filter(s -> s.toLowerCase().contains(input)).findFirst().orElse(null));
    }
    
    
    public CustomSign(CustomSignManager manager, List<String> lines) {
        super(manager);
        this.lines = lines;
    }
}
