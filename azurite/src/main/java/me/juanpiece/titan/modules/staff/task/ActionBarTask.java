package me.juanpiece.titan.modules.staff.task;

import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.staff.StaffManager;
import me.juanpiece.titan.modules.users.User;
import me.juanpiece.titan.modules.users.settings.ActionBar;
import me.juanpiece.titan.modules.versions.Version;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ActionBarTask extends BukkitRunnable {

    private final StaffManager manager;
    private final Player player;
    private final Version version;

    public ActionBarTask(StaffManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.version = manager.getInstance().getVersionManager().getVersion();
        this.runTaskTimerAsynchronously(manager.getInstance(), 0L, 2L); // async cos we're using NMS
    }

    public void destroy() {
        this.cancel();
        version.sendActionBar(player, ""); // empty

        User user = manager.getInstance().getUserManager().getByUUID(player.getUniqueId());

        if (user.getActionBar() == ActionBar.STAFF_MODE) {
            user.setActionBar(null);
        }
    }

    @Override
    public void run() {
        User user = manager.getInstance().getUserManager().getByUUID(player.getUniqueId());
        ActionBar actionBar = user.getActionBar();

        if (actionBar == null) user.setActionBar(ActionBar.STAFF_MODE);

        if (actionBar == ActionBar.STAFF_MODE) {
            version.sendActionBar(player, manager.getInstance().getPlaceholderHook().replace(player, manager.getConfig().getString("STAFF_MODE.ACTION_BAR_STRING"))
                    .replace("%vanished%", manager.isVanished(player) ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER)
                    .replace("%staffbuild%", manager.isStaffBuild(player) ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER)
                    .replace("%hidestaff%", manager.isHideStaff(player) ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER)
                    .replace("%tps%", version.getTPSColored())
            );
        }
    }
}