package me.titan.core.modules.scheduler;

import lombok.Getter;
import me.titan.core.modules.scheduler.extra.ScheduleDay;
import org.bukkit.Bukkit;

import java.util.List;

@Getter
public class Schedule {
    private final String name;
    private final int minute;
    private final ScheduleDay day;
    private final List<String> commands;
    private final String time;
    private final int hour;
    
    public Schedule(String name, String time, List<String> commands) {
        this.name = name;
        this.time = time;
        this.commands = commands;
        this.hour = 0;
        this.day = ScheduleDay.NONE;
        this.minute = Integer.parseInt(time);
    }
    
    public void execute() {
        for (String s : this.commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }
    
    
    public Schedule(String name, String time, ScheduleDay day, List<String> commands) {
        this.name = name;
        this.time = time;
        this.commands = commands;
        this.day = day;
        this.hour = (time.contains(":") ? Integer.parseInt(time.split(":")[0]) : 0);
        this.minute = (time.contains(":") ? Integer.parseInt(time.split(":")[1]) : 0);
    }
    
}