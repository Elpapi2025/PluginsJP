package me.titan.core.modules.nametags;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.SneakyThrows;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.nametags.adapter.TitanNametags;
import me.titan.core.modules.nametags.listener.NametagListener;
import me.titan.core.modules.nametags.packet.NametagPacket;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.utils.Tasks;
import me.titan.core.utils.Utils;
import me.titan.core.utils.extra.FastReplaceString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NametagManager extends Manager {
    private final NametagAdapter adapter;
    private final Map<UUID, Nametag> nametags;
    private final ExecutorService executor;
    
    private void updateLunarTags(Player from, Player to, String update) {
        if (this.getInstance().getWaypointManager().isLunarMissing()) {
            return;
        }
        List<String> lines = new ArrayList<>();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(to.getUniqueId());
        String prefix = this.getInstance().getUserManager().getPrefix(to);
        if (team != null) {
            String teamPosition = team.getTeamPosition();
            if (teamPosition != null) {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.TEAM_TOP")).replaceAll("%pos%", teamPosition).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            }
            else {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.NORMAL")).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            }
        }
        if (this.getInstance().getVersionManager().isVer16() || Utils.isVer16(from)) {
            Tasks.execute(this, () -> LunarClientAPI.getInstance().overrideNametag(to, lines, from));
            return;
        }
        lines.add(((prefix != null) ? prefix + " " : "") + update + to.getName());
        LunarClientAPI.getInstance().overrideNametag(to, lines, from);
    }
    
    public NametagManager(HCF plugin) {
        super(plugin);
        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new TitanNametags(this);
        this.executor = Executors.newSingleThreadExecutor();
        new NametagListener(this);
    }
    
    @Override
    public void disable() {
        this.executor.shutdown();
    }
    
    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                this.executor.execute(() -> {
                    String update = this.adapter.getAndUpdate(player, target);
                    this.updateLunarTags(player, target, update);
                });
            }
        }
    }
    
    @SneakyThrows
    public NametagPacket createPacket(Player player) {
        String version = "me.titan.core.modules.nametags.packet.type.NametagPacketV" + Utils.getNMSVer();
        return (NametagPacket)Class.forName(version).getConstructor(NametagManager.class, Player.class).newInstance(this, player);
    }
    
}