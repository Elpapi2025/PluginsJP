package me.juanpiece.titan.modules.framework.extra;

import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.utils.ItemUtils;
import me.juanpiece.titan.utils.Utils;
import me.juanpiece.titan.utils.configs.ConfigJson;
import me.juanpiece.titan.utils.configs.ConfigYML;

import java.io.File;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class Configs {

    private static ConfigYML CONFIG;
    private static ConfigYML SCOREBOARD_CONFIG;
    private static ConfigYML LANGUAGE_CONFIG;
    private static ConfigYML TEAM_CONFIG;
    private static ConfigYML LUNAR_CONFIG;
    private static ConfigYML TABLIST_CONFIG;
    private static ConfigYML CLASSES_CONFIG;
    private static ConfigYML RECLAIMS_CONFIG;
    private static ConfigYML LIMITERS_CONFIG;
    private static ConfigYML ABILITIES_CONFIG;
    private static ConfigYML ITEMS_CONFIG;
    private static ConfigYML SCHEDULES_CONFIG;
    private static ConfigYML KILLSTREAK_CONFIG;
    private static ConfigYML TIPS_CONFIG;
    private static ConfigYML MISC_CONFIG;

    private static ConfigJson EVENTS_DATA;
    private static ConfigJson KITS_DATA;

    public void load(HCF instance) {
        CONFIG = new ConfigYML(instance, "config");
        SCOREBOARD_CONFIG = new ConfigYML(instance, "scoreboard");
        LANGUAGE_CONFIG = new ConfigYML(instance, "language");
        TEAM_CONFIG = new ConfigYML(instance, "teams");
        LUNAR_CONFIG = new ConfigYML(instance, "lunar");
        TABLIST_CONFIG = new ConfigYML(instance, "tablist");
        CLASSES_CONFIG = new ConfigYML(instance, "classes");
        RECLAIMS_CONFIG = new ConfigYML(instance, "reclaims");
        LIMITERS_CONFIG = new ConfigYML(instance, "limiters");
        ABILITIES_CONFIG = new ConfigYML(instance, "abilities");
        SCHEDULES_CONFIG = new ConfigYML(instance, "schedules");
        KILLSTREAK_CONFIG = new ConfigYML(instance, "killstreaks");
        TIPS_CONFIG = new ConfigYML(instance, "tips");
        MISC_CONFIG = new ConfigYML(instance, "data" + File.separator + "misc");

        EVENTS_DATA = new ConfigJson(instance, "data" + File.separator + "events.json");
        KITS_DATA = new ConfigJson(instance, "data" + File.separator + "kits.json");

        ITEMS_CONFIG = (Utils.isModernVer() ?
                new ConfigYML(instance, "items" + File.separator + "itemsModern") :
                new ConfigYML(instance, "items" + File.separator + "items"));

        new ItemUtils(this);
        Config.load(this, false);
    }

    public ConfigYML getConfig() {
        return CONFIG;
    }

    public ConfigYML getTeamConfig() {
        return TEAM_CONFIG;
    }

    public ConfigYML getLanguageConfig() {
        return LANGUAGE_CONFIG;
    }

    public ConfigYML getScoreboardConfig() {
        return SCOREBOARD_CONFIG;
    }

    public ConfigYML getTablistConfig() {
        return TABLIST_CONFIG;
    }

    public ConfigYML getClassesConfig() {
        return CLASSES_CONFIG;
    }

    public ConfigYML getReclaimsConfig() {
        return RECLAIMS_CONFIG;
    }

    public ConfigYML getLimitersConfig() {
        return LIMITERS_CONFIG;
    }

    public ConfigYML getAbilitiesConfig() {
        return ABILITIES_CONFIG;
    }

    public ConfigYML getItemsConfig() {
        return ITEMS_CONFIG;
    }

    public ConfigYML getLunarConfig() {
        return LUNAR_CONFIG;
    }

    public ConfigYML getSchedulesConfig() {
        return SCHEDULES_CONFIG;
    }

    public ConfigYML getKillstreakConfig() {
        return KILLSTREAK_CONFIG;
    }

    public ConfigYML getTipsConfig() {
        return TIPS_CONFIG;
    }

    public ConfigYML getMiscConfig() {
        return MISC_CONFIG;
    }

    public ConfigJson getEventsData() {
        return EVENTS_DATA;
    }

    public ConfigJson getKitsData() {
        return KITS_DATA;
    }

}