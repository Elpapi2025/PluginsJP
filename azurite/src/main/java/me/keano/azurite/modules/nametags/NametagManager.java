package me.keano.azurite.modules.nametags;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.SneakyThrows;
import me.keano.azurite.HCF;
import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.framework.Manager;
import me.keano.azurite.modules.nametags.adapter.AzuriteNametags;
import me.keano.azurite.modules.nametags.listener.NametagListener;
import me.keano.azurite.modules.nametags.packet.NametagPacket;
import me.keano.azurite.modules.nametags.task.NametagTask;
import me.keano.azurite.modules.teams.extra.TeamPosition;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.utils.CC;
import me.keano.azurite.utils.NameThreadFactory;
import me.keano.azurite.utils.Tasks;
import me.keano.azurite.utils.Utils;
import me.keano.azurite.utils.extra.FastReplaceString;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class NametagManager extends Manager {

    private final Map<UUID, Nametag> nametags;
    private final NametagAdapter adapter;
    private final ScheduledExecutorService executor;

    public NametagManager(HCF instance) {
        super(instance);

        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new AzuriteNametags(this);
        this.executor = Executors.newScheduledThreadPool(1, new NameThreadFactory("Azurite - NametagThread"));
        this.executor.scheduleAtFixedRate(new NametagTask(this), 0L, 300L, TimeUnit.MILLISECONDS);

        new NametagListener(this);
    }

    @Override
    public void disable() {
        for (Nametag nametag : nametags.values()) {
            nametag.delete();
        }
        executor.shutdown();
    }

    public void handleUpdate(Player viewer, Player target) {
        if (viewer == null || target == null) return; // Possibly?
        String prefix = getAdapter().getAndUpdate(viewer, target);
        updateLunarTags(viewer, target, prefix);
    }

    public void updateLunarTags(Player viewer, Player target, String prefix) {
        if (getInstance().getClientHook().getClients().isEmpty()) return;
        if (!nametags.containsKey(viewer.getUniqueId())) return;

        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(target.getUniqueId());
        String pos = getInstance().getUserManager().getPrefix(target);
        String name = prefix + target.getName();
        String killTag = (pos != null ? pos : "");
        String teamTag = getTeamTag(pt, viewer);
        boolean vanished = getInstance().getStaffManager().isVanished(target);

        if (getInstance().getStaffManager().isStaffEnabled(target)) {
            List<String> format = new ArrayList<>();

            for (String s : Config.NAMETAG_MOD_MODE) {
                format.add(s
                        .replace("%name%", name)
                        .replace("%killtag%", killTag)
                        .replace("%teamtag%", teamTag)
                        .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(target)))
                        .replace("%rank-name%", CC.t(getInstance().getRankHook().getRankName(target)))
                        .replace("%vanishsymbol%", (vanished ? Config.VANISHED_SYMBOL : ""))
                );
            }

            handleLunar(target, viewer, format);
            return;
        }

        if (pt != null) {
            List<String> format = new ArrayList<>();

            for (String s : Config.NAMETAG_IN_TEAM) {
                format.add(s
                        .replace("%name%", name)
                        .replace("%killtag%", killTag)
                        .replace("%teamtag%", teamTag)
                        .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(target)))
                        .replace("%rank-name%", CC.t(getInstance().getRankHook().getRankName(target)))
                        .replace("%vanishsymbol%", (vanished ? Config.VANISHED_SYMBOL : ""))
                );
            }

            handleLunar(target, viewer, format);
            return;
        }

        List<String> format = new ArrayList<>();

        for (String s : Config.NAMETAG_NO_TEAM) {
            format.add(s
                    .replace("%name%", name)
                    .replace("%killtag%", killTag)
                    .replace("%teamtag%", teamTag)
                    .replace("%rank-color%", CC.t(getInstance().getRankHook().getRankColor(target)))
                    .replace("%rank-name%", CC.t(getInstance().getRankHook().getRankName(target)))
                    .replace("%vanishsymbol%", (vanished ? Config.VANISHED_SYMBOL : ""))
            );
        }

        handleLunar(target, viewer, format);
    }

    private void handleLunar(Player target, Player viewer, List<String> format) {
        // you can't send the packet asynchronously in modern versions
        if (Utils.isModernVer()) {
            Tasks.execute(this, () -> LunarClientAPI.getInstance().overrideNametag(target, format, viewer));
            return;
        }

        LunarClientAPI.getInstance().overrideNametag(target, format, viewer);
    }

    private String getTeamTag(PlayerTeam pt, Player viewer) {
        if (pt == null) {
            return "";
        }

        TeamPosition pos = pt.getTeamPosition();

        if (pos != null) {
            return new FastReplaceString(Config.NAMETAGS_TEAM_TOP)
                    .replaceAll("%name%", (Config.NAMETAGS_TEAM_TOP.contains("%pos-color%") ? pt.getName() : pt.getDisplayName(viewer)))
                    .replaceAll("%dtr%", pt.getDtrString())
                    .replaceAll("%dtr-color%", pt.getDtrColor())
                    .replaceAll("%dtr-symbol%", pt.getDtrSymbol())
                    .replaceAll("%pos%", pos.getPrefix())
                    .replaceAll("%pos-color%", pos.getColor())
                    .endResult();

        } else {
            return new FastReplaceString(Config.NAMETAGS_NORMAL)
                    .replaceAll("%name%", pt.getDisplayName(viewer))
                    .replaceAll("%dtr%", pt.getDtrString())
                    .replaceAll("%dtr-color%", pt.getDtrColor())
                    .replaceAll("%dtr-symbol%", pt.getDtrSymbol())
                    .endResult();
        }
    }

    @SneakyThrows
    public NametagPacket createPacket(Player player) {
        String path = "me.keano.azurite.modules.nametags.packet.type.NametagPacketV" + Utils.getNMSVer();
        return (NametagPacket) Class.forName(path).getConstructor(NametagManager.class, Player.class).newInstance(this, player);
    }
}