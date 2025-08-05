package me.keano.azurite.modules.board.adapter;

import me.keano.azurite.modules.ability.extra.GlobalCooldown;
import me.keano.azurite.modules.board.BoardAdapter;
import me.keano.azurite.modules.board.BoardManager;
import me.keano.azurite.modules.deathban.Deathban;
import me.keano.azurite.modules.events.conquest.Conquest;
import me.keano.azurite.modules.events.conquest.extra.Capzone;
import me.keano.azurite.modules.events.conquest.extra.ConquestType;
import me.keano.azurite.modules.events.koth.Koth;
import me.keano.azurite.modules.events.sotw.SOTWManager;
import me.keano.azurite.modules.framework.Config;
import me.keano.azurite.modules.framework.Module;
import me.keano.azurite.modules.pvpclass.PvPClass;
import me.keano.azurite.modules.pvpclass.cooldown.CustomCooldown;
import me.keano.azurite.modules.pvpclass.type.bard.BardClass;
import me.keano.azurite.modules.pvpclass.type.ghost.GhostClass;
import me.keano.azurite.modules.pvpclass.type.ghost.GhostData;
import me.keano.azurite.modules.pvpclass.type.mage.MageClass;
import me.keano.azurite.modules.pvpclass.type.miner.MinerClass;
import me.keano.azurite.modules.staff.StaffManager;
import me.keano.azurite.modules.teams.Team;
import me.keano.azurite.modules.teams.enums.TeamType;
import me.keano.azurite.modules.teams.type.PlayerTeam;
import me.keano.azurite.modules.timers.listeners.playertimers.AbilityTimer;
import me.keano.azurite.modules.timers.listeners.playertimers.AppleTimer;
import me.keano.azurite.modules.timers.type.CustomTimer;
import me.keano.azurite.modules.timers.type.PlayerTimer;
import me.keano.azurite.modules.users.User;
import me.keano.azurite.utils.Formatter;
import me.keano.azurite.utils.Utils;
import me.keano.azurite.utils.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2023. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteBoard extends Module<BoardManager> implements BoardAdapter {

    private final SOTWManager sotwManager;
    private final StaffManager staffManager;
    private final boolean linesEnabled;
    private final boolean lastLineEnabled;

    private final List<String> kingLines;
    private final List<String> focusLines;
    private final List<String> focusSystemLines;
    private final List<String> conquestLines;
    private final List<String> kitsLines;
    private final List<String> noModMode;
    private final List<String> modMode;
    private final List<String> footerLines;
    private final List<String> claim;
    private final List<String> kothLines;

    private final String line;
    private final String antiClean;
    private final String sotwOff;
    private final String sotw;
    private final String appleLimit;
    private final String className;
    private final String eotw;
    private final String purge;
    private final String bardEnergy;
    private final String mageEnergy;
    private final String minerInvis;
    private final String minerDiamonds;
    private final String ghostMode;
    private final String globalAbilities;
    private final String customTimerFormat;
    private final String deathbanInfo;
    private final String deathbanLives;

    private final boolean footerEnabled;
    private final boolean focusEnabled;
    private final boolean kingEnabled;
    private final boolean staffEnabled;
    private final boolean kitsInfoOnlySpawn;
    private final boolean conquestOnlyTeam;
    private final boolean claimEnabled;
    private final boolean kothEnabled;
    private final boolean onlyPositiveKillstreak;

    public AzuriteBoard(BoardManager manager) {
        super(manager);

        this.sotwManager = getInstance().getSotwManager();
        this.staffManager = getInstance().getStaffManager();
        this.linesEnabled = getScoreboardConfig().getBoolean("SCOREBOARD_INFO.LINES_ENABLED");
        this.lastLineEnabled = getScoreboardConfig().getBoolean("SCOREBOARD_INFO.LAST_LINE_ENABLED");

        this.kingLines = getScoreboardConfig().getStringList("KILL_THE_KING.LINES");
        this.focusLines = getScoreboardConfig().getStringList("TEAM_FOCUS.LINES_PLAYER");
        this.focusSystemLines = getScoreboardConfig().getStringList("TEAM_FOCUS.LINES_SYSTEM");
        this.conquestLines = getScoreboardConfig().getStringList("CONQUEST.LINES");
        this.kitsLines = getScoreboardConfig().getStringList("KITS_INFO");
        this.noModMode = getScoreboardConfig().getStringList("STAFF_MODE.VANISH_NO_MODMODE");
        this.modMode = getScoreboardConfig().getStringList("STAFF_MODE.MOD_MODE");
        this.footerLines = getScoreboardConfig().getStringList("FOOTER.LINES");
        this.claim = getScoreboardConfig().getStringList("CLAIM.LINES");
        this.kothLines = getScoreboardConfig().getStringList("KOTH.LINES");

        this.line = getScoreboardConfig().getString("SCOREBOARD_INFO.LINES");
        this.antiClean = getString("PLAYER_TIMERS.ANTI_CLEAN");
        this.sotwOff = getString("PLAYER_TIMERS.SOTW_OFF");
        this.sotw = getString("PLAYER_TIMERS.SOTW");
        this.appleLimit = getString("PLAYER_TIMERS.APPLE_LIMIT");
        this.className = getString("PLAYER_TIMERS.ACTIVE_CLASS");
        this.eotw = getString("PLAYER_TIMERS.PRE_EOTW");
        this.purge = getString("PLAYER_TIMERS.PURGE");
        this.bardEnergy = getString("BARD_CLASS.BARD_ENERGY");
        this.mageEnergy = getString("MAGE_CLASS.MAGE_ENERGY");
        this.minerInvis = getString("MINER_CLASS.INVIS");
        this.minerDiamonds = getString("MINER_CLASS.DIAMONDS");
        this.ghostMode = getString("GHOST_CLASS.MODE");
        this.globalAbilities = getString("PLAYER_TIMERS.GLOBAL_ABILITIES");
        this.customTimerFormat = getString("CUSTOM_TIMERS.FORMAT");
        this.deathbanInfo = getString("DEATHBAN_INFO.TIME");
        this.deathbanLives = getString("DEATHBAN_INFO.LIVES");

        this.footerEnabled = getScoreboardConfig().getBoolean("FOOTER.ENABLED");
        this.focusEnabled = getScoreboardConfig().getBoolean("TEAM_FOCUS.ENABLED");
        this.kingEnabled = getScoreboardConfig().getBoolean("KILL_THE_KING.ENABLED");
        this.staffEnabled = getScoreboardConfig().getBoolean("STAFF_MODE.ENABLED");
        this.kitsInfoOnlySpawn = getConfig().getBoolean("KITS_INFO_ONLY_SPAWN");
        this.conquestOnlyTeam = getConfig().getBoolean("CONQUEST.SCOREBOARD_ONLY_CONQUEST_CLAIM");
        this.claimEnabled = getScoreboardConfig().getBoolean("CLAIM.ENABLED");
        this.kothEnabled = getScoreboardConfig().getBoolean("KOTH.ENABLED");
        this.onlyPositiveKillstreak = getScoreboardConfig().getBoolean("SCOREBOARD_INFO.SHOW_KILLSTREAK_ONLY_POSITIVE");
    }

    @Override
    public String getTitle(Player player) {
        return Config.SCOREBOARD_TITLE;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        PlayerTeam pt = getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        User user = getInstance().getUserManager().getByUUID(player.getUniqueId());
        GlobalCooldown globalCooldown = getInstance().getAbilityManager().getGlobalCooldown();
        Conquest conquest = getInstance().getConquestManager().getConquest();
        Deathban deathban = user.getDeathban();
        Team atPlayer = null;

        boolean mod = false;
        boolean vanish = false;
        boolean focus = false;
        int focusSize = 0;
        int footer = (footerEnabled ? footerLines.size() : 0);
        int numberOfLines = (lastLineEnabled ? 2 : 1);

        if (deathban != null) {
            if (linesEnabled) lines.add(line);

            lines.add(deathbanInfo + Formatter.getRemaining(deathban.getTime(), false));
            lines.add(deathbanLives + user.getLives());

            if (footerEnabled) {
                for (String s : footerLines)
                    lines.add(s
                            .replace("%footer%", getManager().getFooter().getCurrent())
                    );
            }

            if (linesEnabled) lines.add(line);
            return getInstance().getPlaceholderHook().replace(player, lines);
        }

        if (!user.isScoreboard()) return null;

        if (staffEnabled) {
            boolean vanished = staffManager.isVanished(player);
            boolean staffActive = staffManager.isStaffEnabled(player);

            if (vanished && !staffActive) {
                vanish = true;

                for (String s : noModMode)
                    lines.add(s
                            .replace("%vanished%", Config.STAFF_TRUE_PLACEHOLDER)
                    );

            } else if (staffActive) {
                mod = true;

                for (String s : modMode)
                    lines.add(s
                            .replace("%vanished%", (vanished ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER))
                            .replace("%staffbuild%", staffManager.isStaffBuild(player) ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER)
                            .replace("%hidestaff%", staffManager.isHideStaff(player) ? Config.STAFF_TRUE_PLACEHOLDER : Config.STAFF_FALSE_PLACEHOLDER)
                            .replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                            .replace("%staff%", String.valueOf(staffManager.getStaffMembers().size()))
                            .replace("%tps%", getInstance().getVersionManager().getVersion().getTPSColored())
                    );
            }
        }

        if (claimEnabled && user.isScoreboardClaim()) {
            atPlayer = getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());

            for (String s : claim)
                lines.add(s
                        .replace("%claim%", atPlayer.getDisplayName(player))
                );
        }

        if (getInstance().isKits()) {
            boolean add = false;

            if (kitsInfoOnlySpawn) {
                if (atPlayer == null) {
                    atPlayer = getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
                }

                if (atPlayer.getType() == TeamType.SAFEZONE) {
                    add = true;
                }

            } else {
                add = true;
            }

            if (add) {
                for (String s : kitsLines) {
                    if (onlyPositiveKillstreak && s.contains("%killstreak%") && user.getKillstreak() <= 0) {
                        continue;
                    }

                    lines.add(s
                            .replace("%kills%", String.valueOf(user.getKills()))
                            .replace("%deaths%", String.valueOf(user.getDeaths()))
                            .replace("%killstreak%", String.valueOf(user.getKillstreak()))
                            .replace("%balance%", String.valueOf(user.getBalance()))
                            .replace("%kdr%", user.getKDRString())
                    );
                }
            }
        }

        if (kingEnabled && getInstance().getKingManager().isActive()) {
            Player king = getInstance().getKingManager().getKing();
            double currentHealth = king.getHealth() / 2;

            for (String s : kingLines)
                lines.add(s
                        .replace("%king%", king.getName())
                        .replace("%loc%", Utils.formatLocation(king.getLocation()))
                        .replace("%reward%", getInstance().getKingManager().getReward())
                        .replace("%health%", Formatter.formatHealth(currentHealth))
                );
        }

        if (kothEnabled) {
            for (Koth activeKoth : getInstance().getKothManager().getKoths().values()) {
                if (!activeKoth.isActive()) continue;

                Cuboid capzone = activeKoth.getCaptureZone();

                for (String s : kothLines)
                    lines.add(s
                            .replace("%color%", activeKoth.getColor())
                            .replace("%koth%", activeKoth.getName())
                            .replace("%rem%", Formatter.getRemaining(activeKoth.getRemaining(), false))
                            .replace("%loc%", (capzone == null ? "None" : Utils.formatLocation(capzone.getCenter())))
                    );
            }
        }

        if (pt != null && antiClean != null) {
            PlayerTeam antiCleanTeam = pt.getAntiCleanTeam();
            long rem = pt.getAntiCleanRemaining();

            if (antiCleanTeam != null && rem > 0L) {
                lines.add(antiClean + Formatter.getRemaining(rem, false));
            }
        }

        if (sotwManager.isActive()) {
            if (sotwManager.getEnabled().contains(player.getUniqueId())) {
                if (sotwOff != null) lines.add(sotwOff + sotwManager.getRemainingString());

            } else {
                if (sotw != null) lines.add(sotw + sotwManager.getRemainingString());
            }
        }

        AppleTimer appleTimer = getInstance().getTimerManager().getAppleTimer();
        World.Environment environment = player.getWorld().getEnvironment();

        if (appleTimer.isLimited(environment)) {
            if (appleLimit != null)
                lines.add(appleLimit
                        .replace("%amount%", String.valueOf(appleTimer.getLimit(player)))
                        .replace("%max-amount%", String.valueOf(appleTimer.getMaxLimit(environment)))
                );
        }

        for (CustomTimer timer : getInstance().getTimerManager().getCustomTimers().values()) {
            if (timer.getName().equals("EOTW")) {
                if (eotw != null) lines.add(eotw + timer.getRemainingString());
                continue;
            }

            if (timer.getName().equals("Purge")) {
                if (purge != null) lines.add(purge + timer.getRemainingString());
                continue;
            }

            lines.add(customTimerFormat
                    .replace("%displayName%", timer.getDisplayName()) + timer.getRemainingString()
            );
        }

        PvPClass activeClass = getInstance().getClassManager().getActiveClasses().get(player.getUniqueId());

        if (activeClass != null) {
            if (className != null) lines.add(className + activeClass.getName());

            if (activeClass instanceof BardClass) {
                BardClass bardClass = (BardClass) activeClass;

                if (bardEnergy != null) {
                    lines.add(bardEnergy + Formatter.formatBardEnergy(bardClass.getEnergyCooldown(player).getEnergy()));
                }

            } else if (activeClass instanceof MageClass) {
                MageClass mageClass = (MageClass) activeClass;

                if (mageEnergy != null) {
                    lines.add(mageEnergy + Formatter.formatBardEnergy(mageClass.getEnergyCooldown(player).getEnergy()));
                }

            } else if (activeClass instanceof MinerClass) {
                MinerClass minerClass = (MinerClass) activeClass;

                if (minerInvis != null) {
                    lines.add(minerInvis + (minerClass.getInvisible().contains(player.getUniqueId()) ? "true" : "false"));
                }

                if (minerDiamonds != null) {
                    lines.add(minerDiamonds + user.getDiamonds());
                }

            } else if (activeClass instanceof GhostClass) {
                GhostClass ghostClass = (GhostClass) activeClass;
                GhostData data = ghostClass.getData().get(player.getUniqueId());

                if (ghostMode != null) {
                    lines.add(ghostMode + data.getMode());
                }
            }

            // basically all your speeds, jump
            for (CustomCooldown customCooldown : activeClass.getCustomCooldowns()) {
                String name = customCooldown.getDisplayName();

                if (name == null) continue;
                if (!customCooldown.hasCooldown(player)) continue;

                lines.add(name + customCooldown.getRemainingOld(player));
            }
        }

        if (globalCooldown.hasTimer(player)) {
            if (globalAbilities != null) lines.add(globalAbilities + globalCooldown.getRemainingString(player));
        }

        for (PlayerTimer timer : getInstance().getTimerManager().getPlayerTimers().values()) {
            if (timer instanceof GlobalCooldown) continue;

            String name = timer.getScoreboard();

            if (!timer.hasTimer(player)) continue;
            if (name == null) continue;

            // These need to be replaced, so handle differently
            if (timer instanceof AbilityTimer) {
                AbilityTimer abilityTimer = (AbilityTimer) timer;
                lines.add(name.replace("%ability%", abilityTimer.getAbility().getDisplayName()) + timer.getRemainingStringBoard(player));
                continue;
            }

            lines.add(name + timer.getRemainingStringBoard(player));
        }

        lines.addAll(getInstance().getAbilitiesHook().getScoreboardLines(player));

        if (conquest.isActive()) {
            if (atPlayer == null) {
                atPlayer = getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
            }

            if (!conquestOnlyTeam || atPlayer.getType() == TeamType.CONQUEST) {
                Map<ConquestType, Capzone> conquests = conquest.getCapzones();

                for (String conquestLine : conquestLines)
                    lines.add(conquestLine
                            .replace("%points-1%", conquest.getPoints(1))
                            .replace("%points-2%", conquest.getPoints(2))
                            .replace("%points-3%", conquest.getPoints(3))
                            .replace("%red%", conquests.get(ConquestType.RED).getRemainingString())
                            .replace("%yellow%", conquests.get(ConquestType.YELLOW).getRemainingString())
                            .replace("%green%", conquests.get(ConquestType.GREEN).getRemainingString())
                            .replace("%blue%", conquests.get(ConquestType.BLUE).getRemainingString())
                    );
            }
        }

        if (focusEnabled && pt != null && pt.getFocus() != null) {
            Team focusedTeam = pt.getFocusedTeam();

            if (focusedTeam != null) {
                focus = true;

                if (focusedTeam.getType() == TeamType.PLAYER) {
                    focusSize = focusLines.size();
                    PlayerTeam focusPT = (PlayerTeam) focusedTeam;

                    for (String focusLine : focusLines)
                        lines.add(focusLine
                                .replace("%team%", focusPT.getName())
                                .replace("%hq%", focusPT.getHQFormatted())
                                .replace("%online%", String.valueOf(focusPT.getOnlinePlayersSize(false)))
                                .replace("%max-online%", String.valueOf(focusPT.getPlayers().size()))
                                .replace("%dtr-color%", focusPT.getDtrColor())
                                .replace("%dtr%", focusPT.getDtrString())
                                .replace("%dtr-symbol%", focusPT.getDtrSymbol())
                        );

                } else {
                    focusSize = focusSystemLines.size();

                    for (String s : focusSystemLines)
                        lines.add(s
                                .replace("%team%", focusedTeam.getName())
                                .replace("%hq%", focusedTeam.getHQFormatted())
                        );
                }
            }
        }

        if (footerEnabled) {
            for (String s : footerLines)
                lines.add(s
                        .replace("%footer%", getManager().getFooter().getCurrent())
                );
        }

        if (lines.isEmpty()) {
            return null;
        }

        if (linesEnabled) {
            List<String> clone = new ArrayList<>();

            if (!lines.get(0).equals(line)) {
                clone.add(line);
            }

            clone.addAll(lines);

            if (!lines.get(lines.size() - 1).equals(line) && lastLineEnabled) {
                clone.add(line);
            }

            lines = clone;

            if (lines.size() == numberOfLines + footer) return null;
        }

        if (!linesEnabled && footerEnabled && lines.size() == footerLines.size()) {
            return null;
        }

        // Below handles all the double lines which shouldn't be there.

        int totalSize = numberOfLines + footer;
        boolean lastLineModMode = modMode.get(modMode.size() - 1).equals(line);
        boolean lastLineNoModMode = noModMode.get(noModMode.size() - 1).equals(line);

        if (mod && lines.size() == totalSize + modMode.size() && lastLineModMode) {
            lines.remove(modMode.size());
        }

        if (vanish && lines.size() == totalSize + noModMode.size() && lastLineNoModMode) {
            lines.remove(noModMode.size());
        }

        if (mod && lastLineModMode && focus && lines.size() == totalSize + modMode.size() + focusSize) {
            String modModeLine = lines.get(modMode.size() + 1);

            if (modModeLine.equals(line) || modModeLine.isEmpty()) {
                lines.remove(modMode.size() + 1);
            }
        }

        if (vanish && lastLineNoModMode && focus && lines.size() == totalSize + noModMode.size() + focusSize) {
            String noModModeLine = lines.get(noModMode.size() + 1);

            if (noModModeLine.equals(line) || noModModeLine.isEmpty()) {
                lines.remove(noModMode.size() + 1);
            }
        }

        return getInstance().getPlaceholderHook().replace(player, lines);
    }

    public String getString(String path) {
        String string = getScoreboardConfig().getString(path);
        return (string.isEmpty() ? null : string);
    }
}