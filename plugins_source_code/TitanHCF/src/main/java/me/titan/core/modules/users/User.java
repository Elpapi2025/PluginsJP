package me.titan.core.modules.users;

import lombok.Getter;
import lombok.Setter;
import me.titan.core.modules.deathban.Deathban;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.users.settings.TeamChatSetting;
import me.titan.core.modules.users.settings.TeamListSetting;
import me.titan.core.utils.Serializer;
import me.titan.core.utils.Utils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class User extends HCFModule<UserManager> {
    private boolean scoreboardClaim;
    private boolean privateMessagesSound;
    private UUID uniqueID;
    private int diamonds;
    private boolean reclaimed;
    private boolean scoreboard;
    private int lives;
    private boolean privateMessages;
    private int kills;
    private static DecimalFormat KDR_FORMAT;
    private TeamListSetting teamListSetting;
    private int deaths;
    private int highestKillstreak;
    private boolean claimsShown;
    private List<UUID> ignoring;
    private boolean foundDiamondAlerts;
    private int killstreak;
    private Deathban deathban;
    private boolean cobblePickup;
    private boolean publicChat;
    private int balance;
    private TeamChatSetting teamChatSetting;
    private UUID replied;
    
    public double getKDR() {
        double dtr = this.kills / (double)this.deaths;
        return Double.isNaN(dtr) ? 0.0 : dtr;
    }

    public String getKDRString() {
        return KDR_FORMAT.format(this.getKDR());
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("listSetting", this.teamListSetting.toString());
        map.put("chatSetting", this.teamChatSetting.toString());
        map.put("ignoring", this.ignoring.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("balance", this.balance + "");
        map.put("kills", this.kills + "");
        map.put("deaths", this.deaths + "");
        map.put("diamonds", this.diamonds + "");
        map.put("lives", this.lives + "");
        map.put("killstreak", this.killstreak + "");
        map.put("highestKillstreak", this.highestKillstreak + "");
        map.put("reclaimed", this.reclaimed + "");
        map.put("scoreboardClaim", this.scoreboardClaim + "");
        map.put("scoreboard", this.scoreboard + "");
        map.put("publicChat", this.publicChat + "");
        map.put("cobblePickup", this.cobblePickup + "");
        map.put("foundDiamondAlerts", this.foundDiamondAlerts + "");
        if (this.deathban != null) {
            map.put("deathban", Serializer.serializeDeathban(this.deathban));
        }
        return map;
    }
    
    static {
        KDR_FORMAT = new DecimalFormat("0.00");
    }
    
    public User(UserManager manager, UUID uuid) {
        super(manager);
        this.uniqueID = uuid;
        this.replied = null;
        this.deathban = null;
        this.teamListSetting = TeamListSetting.ONLINE_LOW;
        this.teamChatSetting = TeamChatSetting.PUBLIC;
        this.ignoring = new ArrayList<>();
        this.balance = manager.getConfig().getInt("STARTING_BALANCE");
        this.kills = 0;
        this.deaths = 0;
        this.diamonds = 0;
        this.lives = 0;
        this.killstreak = 0;
        this.highestKillstreak = 0;
        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.reclaimed = false;
        this.scoreboardClaim = this.getConfig().getBoolean("DEFAULT_CLAIM_SCOREBOARD");
        this.scoreboard = true;
        this.publicChat = true;
        this.cobblePickup = true;
        this.foundDiamondAlerts = true;
        this.claimsShown = false;
        manager.getUsers().put(uuid, this);
    }
    
    public void save() {
        this.getInstance().getStorageManager().getStorage().saveUser(this, true);
    }
    
    public User(UserManager manager, Map<String, Object> map) {
        super(manager);
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.replied = null;
        this.teamListSetting = TeamListSetting.valueOf((String) map.get("listSetting"));
        this.teamChatSetting = TeamChatSetting.valueOf((String) map.get("chatSetting"));
        this.ignoring = Utils.createList(map.get("ignoring"), String.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.balance = Integer.parseInt((String) map.get("balance"));
        this.kills = Integer.parseInt((String) map.get("kills"));
        this.deaths = Integer.parseInt((String) map.get("deaths"));
        this.diamonds = Integer.parseInt((String) map.get("diamonds"));
        this.lives = Integer.parseInt((String) map.get("lives"));
        this.killstreak = Integer.parseInt((String) map.get("killstreak"));
        this.highestKillstreak = Integer.parseInt((String) map.get("highestKillstreak"));
        this.reclaimed = Boolean.parseBoolean((String) map.get("reclaimed"));
        this.scoreboardClaim = Boolean.parseBoolean((String) map.get("scoreboardClaim"));
        this.scoreboard = Boolean.parseBoolean((String) map.get("scoreboard"));
        this.publicChat = Boolean.parseBoolean((String) map.get("publicChat"));
        this.cobblePickup = Boolean.parseBoolean((String) map.get("cobblePickup"));
        this.foundDiamondAlerts = Boolean.parseBoolean((String) map.get("foundDiamondAlerts"));
        this.privateMessages = true;
        this.privateMessagesSound = true;
        this.claimsShown = false;
        if (map.containsKey("deathban")) {
            this.deathban = Serializer.deserializeDeathban(this.getInstance().getDeathbanManager(), (String) map.get("deathban"));
        }
        manager.getUsers().put(this.uniqueID, this);
    }
}