package me.juanpiece.titan.modules.users;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.deathban.Deathban;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.users.extra.StoredInventory;
import me.juanpiece.titan.modules.users.settings.ActionBar;
import me.juanpiece.titan.modules.users.settings.TeamChatSetting;
import me.juanpiece.titan.modules.users.settings.TeamListSetting;
import me.juanpiece.titan.utils.Serializer;
import me.juanpiece.titan.utils.Utils;
import me.juanpiece.titan.utils.configs.StorageJson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class User extends Module<UserManager> {

    private static final DecimalFormat KDR_FORMAT = new DecimalFormat("0.00");

    private UUID uniqueID;
    private UUID replied; // for message command
    private String name;
    private String killtag;
    private Deathban deathban;

    private TeamListSetting teamListSetting;
    private TeamChatSetting teamChatSetting;
    private StorageJson storageJson;
    private ActionBar actionBar;

    private List<UUID> ignoring;
    private List<StoredInventory> inventories;
    private List<String> lastKills;
    private List<String> lastDeaths;

    private int balance;
    private int kills;
    private int deaths;
    private int diamonds;
    private int lives;
    private int killstreak;
    private int highestKillstreak;

    private int falltrapTokens;
    private int baseTokens;

    private long playtime;
    private long lastLogin;
    private long dailyCooldown;

    private boolean privateMessages;
    private boolean privateMessagesSound;
    private boolean reclaimed;
    private boolean redeemed;

    private boolean scoreboard;
    private boolean scoreboardClaim;
    private boolean publicChat;
    private boolean cobblePickup;
    private boolean foundDiamondAlerts;
    private boolean claimsShown;

    // For deserialization
    public User(UserManager manager, Map<String, Object> map) {
        super(manager);

        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.name = (String) map.get("name");
        this.replied = null;

        this.teamListSetting = TeamListSetting.valueOf((String) map.get("listSetting"));
        this.teamChatSetting = TeamChatSetting.valueOf((String) map.get("chatSetting"));
        this.storageJson = null;

        this.ignoring = Utils.createList(map.get("ignoring"), String.class)
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        this.inventories = Utils.createList(map.get("inventories"), String.class).stream()
                .map(StoredInventory::fromString)
                .collect(Collectors.toList());

        this.lastKills = Utils.createList(map.get("lastKills"), String.class);
        this.lastDeaths = Utils.createList(map.get("lastDeaths"), String.class);

        this.balance = Integer.parseInt((String) map.get("balance"));
        this.kills = Integer.parseInt((String) map.get("kills"));
        this.deaths = Integer.parseInt((String) map.get("deaths"));
        this.diamonds = Integer.parseInt((String) map.get("diamonds"));
        this.lives = Integer.parseInt((String) map.get("lives"));
        this.killstreak = Integer.parseInt((String) map.get("killstreak"));
        this.highestKillstreak = Integer.parseInt((String) map.get("highestKillstreak"));
        this.reclaimed = Boolean.parseBoolean((String) map.get("reclaimed"));
        this.redeemed = Boolean.parseBoolean((String) map.get("redeemed"));

        this.falltrapTokens = Integer.parseInt((String) map.get("falltrapTokens"));
        this.baseTokens = Integer.parseInt((String) map.get("baseTokens"));

        this.playtime = Long.parseLong((String) map.get("playtime"));
        this.lastLogin = 0L;
        this.dailyCooldown = Long.parseLong((String) map.get("lastDaily"));

        this.scoreboardClaim = Boolean.parseBoolean((String) map.get("scoreboardClaim"));
        this.scoreboard = Boolean.parseBoolean((String) map.get("scoreboard"));
        this.publicChat = Boolean.parseBoolean((String) map.get("publicChat"));
        this.cobblePickup = Boolean.parseBoolean((String) map.get("cobblePickup"));
        this.foundDiamondAlerts = Boolean.parseBoolean((String) map.get("foundDiamondAlerts"));

        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.claimsShown = false;

        if (map.containsKey("deathban")) {
            this.deathban = Serializer.deserializeDeathban(getInstance().getDeathbanManager(), (String) map.get("deathban"));
        }

        if (map.containsKey("killtag")) {
            this.killtag = (String) map.get("killtag");
        }

        manager.getUsers().put(uniqueID, this);
        manager.getUuidCache().put(name, uniqueID);
    }

    public User(UserManager manager, UUID uniqueID, String name) {
        super(manager);

        this.uniqueID = uniqueID;
        this.name = name;
        this.replied = null;
        this.deathban = null;
        this.killtag = null;

        this.teamListSetting = TeamListSetting.ONLINE_HIGH;
        this.teamChatSetting = TeamChatSetting.PUBLIC;
        this.storageJson = null;

        this.ignoring = new ArrayList<>();
        this.inventories = new ArrayList<>();
        this.lastKills = new ArrayList<>();
        this.lastDeaths = new ArrayList<>();

        this.balance = 0;
        this.kills = 0;
        this.deaths = 0;
        this.diamonds = 0;
        this.lives = 0;
        this.killstreak = 0;
        this.highestKillstreak = 0;

        this.falltrapTokens = 0;
        this.baseTokens = 0;

        this.playtime = 0L;
        this.lastLogin = 0L;
        this.dailyCooldown = 0L;

        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.reclaimed = false;
        this.redeemed = false;

        this.scoreboardClaim = Config.DEFAULT_CLAIM_SCOREBOARD;
        this.scoreboard = true;
        this.publicChat = true;
        this.cobblePickup = true;
        this.foundDiamondAlerts = true;
        this.claimsShown = false;

        manager.getUsers().put(uniqueID, this);
        manager.getUuidCache().put(name, uniqueID);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>(); // keep order

        map.put("name", name);
        map.put("uniqueID", uniqueID.toString());
        map.put("listSetting", teamListSetting.toString());
        map.put("chatSetting", teamChatSetting.toString());
        map.put("ignoring", ignoring.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("lastKills", lastKills);
        map.put("lastDeaths", lastDeaths);
        map.put("balance", String.valueOf(balance));
        map.put("kills", String.valueOf(kills));
        map.put("deaths", String.valueOf(deaths));
        map.put("diamonds", String.valueOf(diamonds));
        map.put("lives", String.valueOf(lives));
        map.put("killstreak", String.valueOf(killstreak));
        map.put("highestKillstreak", String.valueOf(highestKillstreak));
        map.put("falltrapTokens", String.valueOf(falltrapTokens));
        map.put("baseTokens", String.valueOf(baseTokens));
        map.put("playtime", String.valueOf(playtime));
        map.put("lastDaily", String.valueOf(dailyCooldown));
        map.put("reclaimed", String.valueOf(reclaimed));
        map.put("redeemed", String.valueOf(redeemed));
        map.put("scoreboardClaim", String.valueOf(scoreboardClaim));
        map.put("scoreboard", String.valueOf(scoreboard));
        map.put("publicChat", String.valueOf(publicChat));
        map.put("cobblePickup", String.valueOf(cobblePickup));
        map.put("foundDiamondAlerts", String.valueOf(foundDiamondAlerts));
        map.put("inventories", new ArrayList<>(inventories).stream().map(StoredInventory::serialize).collect(Collectors.toList()));

        if (deathban != null) map.put("deathban", Serializer.serializeDeathban(deathban));
        if (killtag != null) map.put("killtag", killtag);

        return map;
    }

    public double getKDR() {
        double kdr = (double) kills / (double) deaths;
        return (Double.isNaN(kdr) ? 0.0D : kdr);
    }

    public String getName() {
        return (name == null ? "Null User" : name);
    }

    public String getKDRString() {
        return KDR_FORMAT.format(getKDR());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }

    public void updatePlaytime() {
        this.setPlaytime(playtime + (lastLogin > 0L ? System.currentTimeMillis() - lastLogin : 0L));
        this.setLastLogin(System.currentTimeMillis());
    }

    public void save() {
        getInstance().getStorageManager().getStorage().saveUser(this, true);
    }

    public void delete() {
        getInstance().getStorageManager().getStorage().deleteUser(this);
    }
}