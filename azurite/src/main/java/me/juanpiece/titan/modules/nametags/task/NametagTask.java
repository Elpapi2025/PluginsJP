package me.juanpiece.titan.modules.nametags.task;

import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.nametags.NametagManager;
import me.juanpiece.titan.modules.staff.Staff;
import me.juanpiece.titan.modules.versions.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("ALL")
public class NametagTask extends Module<NametagManager> implements Runnable {

    private final Version version;

    public NametagTask(NametagManager manager) {
        super(manager);
        this.version = getInstance().getVersionManager().getVersion();
    }

    @Override
    public void run() {
        try {

            for (Player viewer : Bukkit.getOnlinePlayers()) {
                // Tracked players doesn't contain the viewer
                getManager().handleUpdate(viewer, viewer);

                // When in staff tracked players are empty because you are hidden
                for (Staff staff : getManager().getInstance().getStaffManager().getStaffMembers().values()) {
                    Player staffPlayer = staff.getPlayer();
                    if (staffPlayer == viewer) continue;
                    getManager().handleUpdate(staffPlayer, viewer);
                }

                for (Player target : version.getTrackedPlayers(viewer)) {
                    if (viewer == target) continue;
                    getManager().handleUpdate(viewer, target);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}