package me.titan.core.modules.board.adapter;

import me.titan.core.modules.board.BoardAdapter;
import me.titan.core.modules.board.BoardManager;
import me.titan.core.modules.deathban.Deathban;
import me.titan.core.modules.events.koth.Koth;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.pvpclass.PvPClass;
import me.titan.core.modules.pvpclass.cooldown.CustomCooldown;
import me.titan.core.modules.pvpclass.type.bard.BardClass;
import me.titan.core.modules.pvpclass.type.mage.MageClass;
import me.titan.core.modules.pvpclass.type.miner.MinerClass;
import me.titan.core.modules.staff.StaffManager;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.type.PlayerTeam;
import me.titan.core.modules.timers.listeners.playertimers.AbilityTimer;
import me.titan.core.modules.timers.listeners.servertimers.SOTWTimer;
import me.titan.core.modules.timers.type.CustomTimer;
import me.titan.core.modules.timers.type.PlayerTimer;
import me.titan.core.modules.users.User;
import me.titan.core.utils.Formatter;
import me.titan.core.utils.Utils;
import me.titan.core.utils.extra.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TitanBoard extends HCFModule<BoardManager> implements BoardAdapter {
    private final List<String> noModMode;
    private final boolean staffEnabled;
    private final List<String> modMode;
    private final boolean focusEnabled;
    private final StaffManager staffManager;
    private final List<String> kingLines;
    private final boolean kingEnabled;
    private final SOTWTimer sotwTimer;
    private final List<String> focusLines;
    
    public TitanBoard(BoardManager manager) {
        super(manager);
        this.sotwTimer = this.getInstance().getTimerManager().getSotwTimer();
        this.staffManager = this.getInstance().getStaffManager();
        this.kingLines = this.getScoreboardConfig().getStringList("KILL_THE_KING.LINES");
        this.focusLines = this.getScoreboardConfig().getStringList("TEAM_FOCUS.LINES");
        this.noModMode = this.getScoreboardConfig().getStringList("STAFF_MODE.VANISH_NO_MODMODE");
        this.modMode = this.getScoreboardConfig().getStringList("STAFF_MODE.MOD_MODE");
        this.focusEnabled = this.getScoreboardConfig().getBoolean("TEAM_FOCUS.ENABLED");
        this.kingEnabled = this.getScoreboardConfig().getBoolean("KILL_THE_KING.ENABLED");
        this.staffEnabled = this.getScoreboardConfig().getBoolean("STAFF_MODE.ENABLED");
    }
    
    public String getString(String string) {
        String s = this.getScoreboardConfig().getString(string);
        return s.equals("") ? null : s;
    }
    
    @Override
    public String getTitle(Player player) {
        return this.getString("SCOREBOARD_INFO.TITLE");
    }
    
    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        String lines = this.getScoreboardConfig().getString("SCOREBOARD_INFO.LINES");
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        Cooldown cooldown = this.getInstance().getAbilityManager().getGlobalCooldown();
        boolean isVanished = this.staffManager.isVanished(player);
        boolean isStaff = this.staffManager.isStaffEnabled(player);
        boolean b1 = false;
        boolean b2 = false;
        if (user.getDeathban() != null) {
            Deathban deathban = user.getDeathban();
            toReturn.add(lines);
            toReturn.add(this.getString("DEATHBAN_INFO.TIME") + Formatter.formatMMSS(deathban.getTime()));
            toReturn.add(this.getString("DEATHBAN_INFO.LIVES") + user.getLives());
            toReturn.add(lines);
            return toReturn;
        }
        if (!user.isScoreboard()) {
            return null;
        }
        toReturn.add(lines);
        if (this.staffEnabled) {
            if (!isStaff && isVanished) {
                List<String> list = new ArrayList<>(this.noModMode);
                list.replaceAll(s -> s.replaceAll("%vanished%", String.valueOf(true)));
                toReturn.addAll(list);
            }
            else if (isStaff) {
                List<String> list = new ArrayList<>(this.modMode);
                list.replaceAll(s -> s.replaceAll("%vanished%", String.valueOf(isVanished)).replaceAll("%players%", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("%staff%", String.valueOf(this.staffManager.getStaffMembers().size())).replaceAll("%tps%", this.getInstance().getVersionManager().getVersion().getTPSColored()));
                toReturn.addAll(list);
            }
        }
        if (this.getInstance().isKits()) {
            String kills = this.getString("KITS_INFO.KILLS");
            String deaths = this.getString("KITS_INFO.DEATHS");
            String balance = this.getString("KITS_INFO.BALANCE");
            if (kills != null) {
                toReturn.add(kills + " " + user.getKills());
            }
            if (deaths != null) {
                toReturn.add(deaths + " " + user.getDeaths());
            }
            if (balance != null) {
                toReturn.add(balance + " " + user.getBalance());
            }
        }
        if (this.sotwTimer.isActive()) {
            if (this.sotwTimer.getEnabled().contains(player.getUniqueId())) {
                String sotw = this.getString("PLAYER_TIMERS.SOTW_OFF");
                if (sotw != null) {
                    toReturn.add(sotw + " " + this.sotwTimer.getRemainingString());
                }
            }
            else {
                String sotw = this.getString("PLAYER_TIMERS.SOTW");
                if (sotw != null) {
                    toReturn.add(sotw + " " + this.sotwTimer.getRemainingString());
                }
            }
        }
        if (this.kingEnabled && this.getInstance().getKingManager().isActive()) {
            Player king = this.getInstance().getKingManager().getKing();
            double health = king.getHealth() / 2.0;
            List<String> list = new ArrayList<>(this.kingLines);
            list.replaceAll(s -> s.replaceAll("%king%", player.getName()).replaceAll("%loc%", Utils.formatLocation(player.getLocation())).replaceAll("%reward%", this.getInstance().getKingManager().getReward()).replaceAll("%health%", Formatter.formatHealth(health)));
            b1 = true;
            toReturn.addAll(list);
        }
        for (Koth koth : this.getInstance().getKothManager().getKoths().values()) {
            if (!koth.isActive()) {
                continue;
            }
            String kothS = this.getString("PLAYER_TIMERS.KOTH");
            if (kothS == null) {
                continue;
            }
            toReturn.add(kothS.replaceAll("%color%", koth.getColor()).replaceAll("%koth%", koth.getName()) + " " + Formatter.formatMMSS(koth.getRemaining()));
        }
        for (CustomTimer timer : this.getInstance().getTimerManager().getCustomTimers().values()) {
            toReturn.add(this.getString("CUSTOM_TIMERS.FORMAT").replaceAll("%displayName%", timer.getDisplayName()) + " " + timer.getRemainingString());
        }
        if (user.isScoreboardClaim()) {
            String claim = this.getString("PLAYER_TIMERS.CLAIM");
            if (claim != null) {
                Team cTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
                toReturn.add(claim + " " + cTeam.getDisplayName(player));
            }
        }
        PvPClass pvpClass = this.getInstance().getClassManager().getActiveClasses().get(player.getUniqueId());
        if (pvpClass != null) {
            String activeClass = this.getString("PLAYER_TIMERS.ACTIVE_CLASS");
            if (activeClass != null) {
                toReturn.add(activeClass + " " + pvpClass.getName());
            }
            if (pvpClass instanceof BardClass) {
                BardClass bardClass = (BardClass)pvpClass;
                String bardEnergy = this.getString("BARD_CLASS.BARD_ENERGY");
                if (bardEnergy != null) {
                    toReturn.add(bardEnergy + " " + Formatter.formatBardEnergy(bardClass.getEnergyCooldown(player).getEnergy()));
                }
            }
            else if (pvpClass instanceof MageClass) {
                MageClass mageClass = (MageClass)pvpClass;
                String mageEnergy = this.getString("MAGE_CLASS.MAGE_ENERGY");
                if (mageEnergy != null) {
                    toReturn.add(mageEnergy + " " + Formatter.formatBardEnergy(mageClass.getEnergyCooldown(player).getEnergy()));
                }
            }
            else if (pvpClass instanceof MinerClass) {
                MinerClass minerClass = (MinerClass)pvpClass;
                String minerInvs = this.getString("MINER_CLASS.INVIS");
                String minerDiamonds = this.getString("MINER_CLASS.DIAMONDS");
                if (minerInvs != null) {
                    toReturn.add(minerInvs + " " + (minerClass.getInvisible().contains(player.getUniqueId()) ? "true" : "false"));
                }
                if (minerDiamonds != null) {
                    toReturn.add(minerDiamonds + " " + user.getDiamonds());
                }
            }
            for (CustomCooldown customCooldown : pvpClass.getCustomCooldowns()) {
                String cooldownName = customCooldown.getDisplayName();
                if (cooldownName == null) {
                    continue;
                }
                if (!customCooldown.hasCooldown(player)) {
                    continue;
                }
                toReturn.add(cooldownName + customCooldown.getRemaining(player));
            }
        }
        if (cooldown.hasCooldown(player)) {
            String abilities = this.getString("PLAYER_TIMERS.GLOBAL_ABILITIES");
            if (abilities != null) {
                toReturn.add(abilities + " " + cooldown.getRemaining(player));
            }
        }
        for (PlayerTimer timer : this.getInstance().getTimerManager().getPlayerTimers().values()) {
            String timerText = this.getString(timer.getScoreboardPath());
            if (!timer.hasTimer(player)) {
                continue;
            }
            if (timerText == null) {
                continue;
            }
            if (timer instanceof AbilityTimer) {
                AbilityTimer abilityTimer = (AbilityTimer)timer;
                toReturn.add(timerText.replaceAll("%ability%", abilityTimer.getAbility().getDisplayName()) + " " + timer.getRemainingString(player));
            }
            else {
                toReturn.add(timerText + " " + timer.getRemainingString(player));
            }
        }
        if (this.focusEnabled && team != null && team.getFocus() != null) {
            PlayerTeam focused = team.getFocusedTeam();
            List<String> list = new ArrayList<String>(this.focusLines);
            b2 = true;
            list.replaceAll(s -> s.replaceAll("%team%", focused.getName()).replaceAll("%hq%", focused.getHQFormatted()).replaceAll("%online%", String.valueOf(focused.getOnlinePlayers().size())).replaceAll("%dtr-color%", focused.getDtrColor()).replaceAll("%dtr%", focused.getDtrString()).replaceAll("%dtr-symbol%", focused.getDtrSymbol()));
            toReturn.addAll(list);
        }
        toReturn.add(lines);
        if (b1 && toReturn.size() == this.kingLines.size() + 2 && this.kingLines.contains(lines)) {
            toReturn.remove(toReturn.size() - 1);
        }
        if (b2 && toReturn.size() == this.focusLines.size() + 2 && this.focusLines.contains(lines)) {
            toReturn.remove(0);
        }
        if (isStaff && toReturn.size() == this.modMode.size() + 2 && this.modMode.contains(lines)) {
            toReturn.remove(toReturn.size() - 1);
        }
        if (!isStaff && isVanished && toReturn.size() == this.noModMode.size() + 2 && this.noModMode.contains(lines)) {
            toReturn.remove(toReturn.size() - 1);
        }
        if (b2 && b1 && toReturn.size() == this.focusLines.size() + this.kingLines.size() + 2 && this.kingLines.contains(lines)) {
            toReturn.remove(this.kingLines.size());
        }
        if (toReturn.size() == 2) {
            return null;
        }
        return toReturn;
    }
}
