package me.titan.core.modules.timers;

import lombok.Getter;
import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.timers.listeners.playertimers.*;
import me.titan.core.modules.timers.listeners.servertimers.EOTWTimer;
import me.titan.core.modules.timers.listeners.servertimers.SOTWTimer;
import me.titan.core.modules.timers.listeners.servertimers.TeamRegenTimer;
import me.titan.core.modules.timers.type.CustomTimer;
import me.titan.core.modules.timers.type.PlayerTimer;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class TimerManager extends Manager {
    private final InvincibilityTimer invincibilityTimer;
    private final WarmupTimer warmupTimer;
    private final Map<String, CustomTimer> customTimers;
    private final LogoutTimer logoutTimer;
    private final EnderpearlTimer enderpearlTimer;
    private final Map<String, PlayerTimer> playerTimers;
    private final StuckTimer stuckTimer;
    private final PvPTimer pvpTimer;
    private final AppleTimer appleTimer;
    private final GappleTimer gappleTimer;
    private final EOTWTimer eotwTimer;
    private final SOTWTimer sotwTimer;
    private final HQTimer hqTimer;
    private final ArcherTagTimer archerTagTimer;
    private final SpawnTimer spawnTimer;
    private final DeathbanTimer deathbanTimer;
    private final TeamRegenTimer teamRegenTimer;
    private final CombatTimer combatTimer;
    
    public PlayerTimer getPlayerTimer(String name) {
        return this.playerTimers.get(name);
    }
    
    public CustomTimer getCustomTimer(String name) {
        return this.customTimers.get(name);
    }
    
    public TimerManager(HCF plugin) {
        super(plugin);
        this.customTimers = new LinkedHashMap<>();
        this.playerTimers = new LinkedHashMap<>();
        this.logoutTimer = new LogoutTimer(this);
        this.invincibilityTimer = new InvincibilityTimer(this);
        this.pvpTimer = new PvPTimer(this);
        this.warmupTimer = new WarmupTimer(this);
        this.combatTimer = new CombatTimer(this);
        this.appleTimer = new AppleTimer(this);
        this.enderpearlTimer = new EnderpearlTimer(this);
        this.gappleTimer = new GappleTimer(this);
        this.archerTagTimer = new ArcherTagTimer(this);
        this.stuckTimer = new StuckTimer(this);
        this.spawnTimer = new SpawnTimer(this);
        this.hqTimer = new HQTimer(this);
        this.sotwTimer = new SOTWTimer(this);
        this.eotwTimer = new EOTWTimer(this);
        this.deathbanTimer = new DeathbanTimer(this);
        this.teamRegenTimer = new TeamRegenTimer(this);
    }
}