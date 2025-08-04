package me.titan.core.modules.listeners;

import me.titan.core.HCF;
import me.titan.core.modules.framework.Manager;
import me.titan.core.modules.listeners.type.*;
import me.titan.core.modules.listeners.type.team.PlayerTeamListener;
import me.titan.core.modules.listeners.type.team.TeamListener;

public class ListenerManager extends Manager {
    public ListenerManager(HCF plugin) {
        super(plugin);
        new MainListener(this);
        new ChatListener(this);
        new DeathListener(this);
        new FixListener(this);
        new BorderListener(this);
        new GlitchListener(this);
        new WorldListener(this);
        new CobbleListener(this);
        new DiamondListener(this);
        new PortalListener(this);
        new SmeltListener(this);
        new DropListener(this);
        new StackListener(this);
        new LimiterListener(this);
        new DurabilityListener(this);
        new StrengthListener(this);
        new EndListener(this);
        new TeamListener(this.getInstance().getTeamManager());
        new PlayerTeamListener(this.getInstance().getTeamManager());
    }
}
