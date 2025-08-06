package me.juanpiece.titan.modules.scheduler.command;

import me.juanpiece.titan.modules.commands.CommandManager;
import me.juanpiece.titan.modules.framework.commands.Command;
import me.juanpiece.titan.modules.scheduler.Schedule;
import me.juanpiece.titan.utils.Formatter;
import me.juanpiece.titan.utils.extra.Pair;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SchedulesCommand extends Command {

    public SchedulesCommand(CommandManager manager) {
        super(
                manager,
                "schedule"
        );
        this.setAsync(true); // Might be performance heavy?
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "koths",
                "schedules"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Calculate everything first!
        Calendar calendar = Calendar.getInstance(getInstance().getScheduleManager().getTimeZone());
        Pair<String, Long> nextSchedule = getInstance().getScheduleManager().getNextSchedule();
        List<String> normal = new ArrayList<>();
        List<String> daily = new ArrayList<>();
        List<String> hourly = new ArrayList<>();

        for (List<Schedule> schedules : getInstance().getScheduleManager().getNormal().values()) {
            for (Schedule schedule : schedules) {
                if (schedule.getName().equalsIgnoreCase("NONE")) continue;

                normal.add(getLanguageConfig().getString("SCHEDULES_COMMAND.NORMAL_FORMAT")
                        .replace("%name%", schedule.getName())
                        .replace("%time%", schedule.getTime())
                        .replace("%day%", schedule.getDay().getName())
                );
            }
        }

        for (List<Schedule> schedules : getInstance().getScheduleManager().getDaily().values()) {
            for (Schedule schedule : schedules) {
                if (schedule.getName().equalsIgnoreCase("NONE")) continue;

                daily.add(getLanguageConfig().getString("SCHEDULES_COMMAND.DAILY_FORMAT")
                        .replace("%name%", schedule.getName())
                        .replace("%time%", schedule.getTime())
                        .replace("%day%", schedule.getDay().getName())
                );
            }
        }

        for (List<Schedule> schedules : getInstance().getScheduleManager().getHourly().values()) {
            for (Schedule schedule : schedules) {
                if (schedule.getName().equalsIgnoreCase("NONE")) continue;

                hourly.add(getLanguageConfig().getString("SCHEDULES_COMMAND.HOURLY_FORMAT")
                        .replace("%name%", schedule.getName())
                        .replace("%time%", schedule.getTime())
                );
            }
        }

        // Now send everything.
        for (String s : getLanguageConfig().getStringList("SCHEDULES_COMMAND.SCHEDULES")) {
            if (s.equalsIgnoreCase("%normal%")) {
                for (String schedule : normal) sendMessage(sender, schedule);
                continue;
            }

            if (s.equalsIgnoreCase("%daily%")) {
                for (String daySchedule : daily) sendMessage(sender, daySchedule);
                continue;
            }

            if (s.equalsIgnoreCase("%hourly%")) {
                for (String hourSchedule : hourly) sendMessage(sender, hourSchedule);
                continue;
            }

            sendMessage(sender, s
                    .replace("%time%", calcTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
                    .replace("%scheduled%", nextSchedule.getKey())
                    .replace("%scheduledtime%", Formatter.formatSchedule(nextSchedule.getValue()))
            );
        }
    }

    private String calcTime(int hour, int minute) {
        return hour + ":" + (String.valueOf(minute).length() < 2 ? "0" + minute : minute) + (hour > 12 ? "PM" : "AM");
    }
}