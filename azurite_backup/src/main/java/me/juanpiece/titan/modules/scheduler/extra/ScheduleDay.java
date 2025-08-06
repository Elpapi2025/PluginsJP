package me.juanpiece.titan.modules.scheduler.extra;

import lombok.Getter;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum ScheduleDay {

    SUNDAY("Sunday"),
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    NONE("None");

    private final String name;

    ScheduleDay(String name) {
        this.name = name;
    }
}