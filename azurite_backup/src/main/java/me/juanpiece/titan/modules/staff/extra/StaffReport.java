package me.juanpiece.titan.modules.staff.extra;

import lombok.Getter;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.staff.StaffManager;

import java.util.UUID;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class StaffReport extends Module<StaffManager> {

    private final UUID player;
    private final UUID reported;
    private final String reason;

    public StaffReport(StaffManager manager, UUID player, UUID reported, String reason) {
        super(manager);
        this.player = player;
        this.reported = reported;
        this.reason = reason;
    }
}