package me.titan.core.modules.hooks.ranks;

import me.titan.core.modules.framework.*;
import me.titan.core.utils.*;
import me.titan.core.modules.hooks.ranks.type.*;
import me.titan.core.*;
import org.bukkit.entity.*;

public class RankManager extends Manager implements Rank {
    private Rank rank = null;
    
    private void load() {
        if (Utils.verifyPlugin("AquaCore", this.getInstance())) {
            this.rank = new AquaCoreRank();
        }
        else if (Utils.verifyPlugin("Zoot", this.getInstance())) {
            this.rank = new ZootRank();
        }
        else if (Utils.verifyPlugin("Zoom", this.getInstance())) {
            this.rank = new ZoomRank();
        }
        else if (Utils.verifyPlugin("Mizu", this.getInstance())) {
            this.rank = new MizuRank();
        }
        else if (Utils.verifyPlugin("Atom", this.getInstance())) {
            this.rank = new AtomRank();
        }
        else if (Utils.verifyPlugin("Basic", this.getInstance())) {
            this.rank = new CoreRank();
        }
        else if (Utils.verifyPlugin("ZPermissions", this.getInstance())) {
            this.rank = new ZPermissionRank();
        }
        else if (this.getInstance().getServer().getPluginManager().getPlugin("Vault") != null && this.getInstance().getServer().getPluginManager().getPlugin("Vault").isEnabled()) {
            this.rank = new VaultRank();
        }
        else {
            this.rank = new NoneRank();
        }
    }
    
    public RankManager(HCF plugin) {
        super(plugin);
        this.load();
    }
    
    @Override
    public String getRankColor(Player player) {
        if (this.rank == null) this.load();
        return this.rank.getRankColor(player);
    }
    
    @Override
    public String getRankName(Player player) {
        if (this.rank == null) this.load();
        return this.rank.getRankName(player);
    }
    
    @Override
    public String getRankSuffix(Player player) {
        if (this.rank == null) this.load();
        return this.rank.getRankSuffix(player);
    }
    
    @Override
    public String getRankPrefix(Player player) {
        if (this.rank == null) this.load();
        return this.rank.getRankPrefix(player);
    }
}
