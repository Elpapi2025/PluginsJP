package me.juanpiece.titan.modules.reclaims;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Reclaim {

    private final String name;
    private final String permission;
    private final List<String> commands;
    private final int priority;

    public Reclaim(String name, List<String> commands, int priority) {
        this.name = name;
        this.permission = "titan.reclaim." + name.toLowerCase();
        this.commands = commands;
        this.priority = priority;
    }
}