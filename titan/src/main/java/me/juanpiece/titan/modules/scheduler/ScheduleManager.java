package me.juanpiece.titan.modules.scheduler;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.scheduler.extra.ScheduleDay;
import me.juanpiece.titan.utils.Tasks;
import me.juanpiece.titan.utils.extra.Pair;

import java.util.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("all")
public class ScheduleManager extends Manager {

    private final Table<Integer, Long, List<Schedule>> normal; // Day, Time -> Schedules
    private final Map<Long, List<Schedule>> daily; // Time -> Schedules
    private final Map<Integer, List<Schedule>> hourly; // Hour -> Schedules
    private final Map<Long, List<Schedule>> kothSchedules;

    // These can be cached
    private final ScheduleDay[] days;
    private final Calendar calendar;

    private TimeZone timeZone;
    private Pair<List<Schedule>, Long> nextSchedule;
    private int oldMin;

    public ScheduleManager(HCF instance) {
        super(instance);

        this.normal = Tables.newCustomTable(new LinkedHashMap<>(), LinkedHashMap::new);
        this.daily = new LinkedHashMap<>();
        this.hourly = new LinkedHashMap<>();
        this.kothSchedules = new LinkedHashMap<>();
        this.days = ScheduleDay.values(); // Cache all of them, we use the ordinal to get the name of the day.
        this.timeZone = TimeZone.getTimeZone(getSchedulesConfig().getString("TIME_ZONE")); // We can store timezone
        this.calendar = Calendar.getInstance(timeZone);

        this.load();
        Tasks.executeScheduled(this, 20, this::tick);
    }

    @Override
    public void reload() {
        normal.clear();
        daily.clear();
        hourly.clear();
        kothSchedules.clear();
        this.timeZone = TimeZone.getTimeZone(getSchedulesConfig().getString("TIME_ZONE")); // We can store timezone
        this.load();
    }

    public Pair<String, Long> getNextSchedule() {
        if (nextSchedule == null || nextSchedule.getKey() == null) {
            return new Pair<>("None", 0L);
        }

        String[] names = nextSchedule.getKey()
                .stream()
                .map(Schedule::getName)
                .toArray(String[]::new);

        return new Pair<>(String.join("§7, ", names), nextSchedule.getValue());
    }

    private void load() {
        for (String s : getSchedulesConfig().getStringList("SCHEDULES.HOURLY")) {
            String[] split = s.split(", ");
            Schedule schedule = new Schedule(this, split[0], split[1], Arrays.asList(split[2].split(";")));
            hourly.putIfAbsent(schedule.getMinute(), new ArrayList<>());
            hourly.get(schedule.getMinute()).add(schedule);
        }

        for (String s : getSchedulesConfig().getStringList("SCHEDULES.DAILY")) {
            String[] split = s.split(", ");
            Schedule schedule = new Schedule(this, split[0], split[1], ScheduleDay.NONE, Arrays.asList(split[2].split(";")));
            long time = toLong(schedule.getHour(), schedule.getMinute());
            daily.putIfAbsent(time, new ArrayList<>());
            daily.get(time).add(schedule);
        }

        for (String s : getSchedulesConfig().getStringList("SCHEDULES.NORMAL")) {
            String[] split = s.split(", ");
            Schedule schedule = new Schedule(this, split[0], split[1], ScheduleDay.valueOf(split[2]), Arrays.asList(split[3].split(";")));
            long time = toLong(schedule.getHour(), schedule.getMinute());
            int day = schedule.getDay().ordinal();
            if (normal.get(day, time) == null) normal.put(day, time, new ArrayList<>());
            normal.get(day, time).add(schedule);
        }

        for (String s : getSchedulesConfig().getStringList("KOTH_SCHEDULES")) {
            String[] split = s.split(", ");
            boolean daily = split[2].equalsIgnoreCase("DAILY");

            if (daily) {
                for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    Schedule schedule = new Schedule(this, split[0], split[1], i, Arrays.asList(split[3].split(";")));
                    kothSchedules.putIfAbsent(schedule.getDayTime(), new ArrayList<>());
                    kothSchedules.get(schedule.getDayTime()).add(schedule);
                }
                continue;
            }

            Schedule schedule = new Schedule(this, split[0], split[1], Integer.parseInt(split[2]), Arrays.asList(split[3].split(";")));
            kothSchedules.putIfAbsent(schedule.getDayTime(), new ArrayList<>());
            kothSchedules.get(schedule.getDayTime()).add(schedule);
        }
    }

    private void tick() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int min = calendar.get(Calendar.MINUTE);
        this.nextSchedule = checkNextSchedule(calendar.getTimeInMillis());

        if (oldMin == min) return;

        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // These are needed otherwise the time might be slight off for koth schedules
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        this.oldMin = min;
        long time = toLong(hour, min);

        List<Schedule> normalSchedule = normal.get(day, time);
        List<Schedule> dailySchedule = daily.get(time);
        List<Schedule> hourlySchedule = hourly.get(min);
        List<Schedule> kothSchedule = kothSchedules.get(calendar.getTimeInMillis());

        if (normalSchedule != null) normalSchedule.forEach(Schedule::execute);
        if (dailySchedule != null) dailySchedule.forEach(Schedule::execute);
        if (hourlySchedule != null) hourlySchedule.forEach(Schedule::execute);
        if (kothSchedule != null) kothSchedule.forEach(Schedule::execute);
    }

    private Pair<List<Schedule>, Long> checkNextSchedule(long currentTime) {
        long closest = 0L;

        for (Map.Entry<Long, List<Schedule>> entry : kothSchedules.entrySet()) {
            List<Schedule> schedules = entry.getValue();
            long time = entry.getKey();

            // Don't check if the time is already passed
            if (time < currentTime) continue;

            // Init first
            if (closest == 0L) {
                closest = time;
                continue;
            }

            // if time is less than the current closest we know its next
            if ((time - currentTime) < (closest - currentTime)) {
                closest = time;
            }
        }

        return new Pair<>(kothSchedules.get(closest), closest - currentTime);
    }

    private long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }
}