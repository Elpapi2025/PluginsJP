package me.titan.core.modules.storage.json;

import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.storage.Storage;
import me.titan.core.modules.storage.StorageManager;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.claims.Claim;
import me.titan.core.modules.teams.enums.TeamType;
import me.titan.core.modules.teams.type.*;
import me.titan.core.modules.timers.listeners.servertimers.SOTWTimer;
import me.titan.core.modules.timers.type.CustomTimer;
import me.titan.core.modules.timers.type.PlayerTimer;
import me.titan.core.modules.users.User;
import me.titan.core.utils.Tasks;
import me.titan.core.utils.configs.ConfigJson;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class JsonStorage extends HCFModule<StorageManager> implements Storage {
    private final ConfigJson teamsJson;
    private final ConfigJson usersJson;
    private final ConfigJson timersJson;
    
    @Override
    public void close() {
        this.saveTeams();
        this.saveUsers();
        this.saveTimers();
    }
    
    @Override
    public void loadTimers() {
        for (PlayerTimer timer : this.instance.getTimerManager().getPlayerTimers().values()) {
            Map<String, Object> normal = (Map<String, Object>) this.timersJson.getValues().get((timer.isPausable() ? "Normal:" : "") + timer.getName());
            Map<String, Object> paused = (Map<String, Object>) this.timersJson.getValues().get("Paused:" + timer.getName());
            if (normal != null) {
                timer.getTimerCache().putAll(normal.entrySet().stream().collect(Collectors.toMap(s -> UUID.fromString(s.getKey()), s -> Long.parseLong((String) s.getValue()))));
            }
            if (paused != null) {
                timer.getPausedCache().putAll(paused.entrySet().stream().collect(Collectors.toMap(s -> UUID.fromString(s.getKey()), s -> Long.parseLong((String) s.getValue()))));
            }
        }
        List<String> sotw = (List<String>) this.timersJson.getValues().get("SOTW_ENABLED:");
        if (sotw != null) {
            this.instance.getTimerManager().getSotwTimer().getEnabled().addAll(sotw.stream().map(UUID::fromString).collect(Collectors.toList()));
        }
        if (this.timersJson.getValues().containsKey("SOTW:")) {
            long sotwTime = Long.parseLong((String) this.timersJson.getValues().get("SOTW:"));
            if (sotwTime > 0L) {
                SOTWTimer timer = this.instance.getTimerManager().getSotwTimer();
                timer.setActive(true);
                timer.setRemaining(sotwTime);
            }
        }
        for (String s : this.timersJson.getValues().keySet()) {
            if (!s.contains("CTimer:")) {
                continue;
            }
            String value = (String) this.timersJson.getValues().get(s);
            String[] values = value.split(":");
            String name = s.split(":")[1];
            CustomTimer timer = new CustomTimer(this.instance.getTimerManager(), name, values[1], 0L);
            timer.setRemaining(Long.parseLong(values[0]));
        }
    }
    
    @Override
    public void saveTeam(Team team, boolean async) {
        if (async) {
            Tasks.executeAsync(this.manager, () -> this.saveTeam(team, false));
            return;
        }
        this.teamsJson.getValues().put(team.getUniqueID().toString(), team.serialize());
        this.teamsJson.save();
    }
    
    @Override
    public void saveUsers() {
        for (User user : this.instance.getUserManager().getUsers().values()) {
            this.usersJson.getValues().put(user.getUniqueID().toString(), user.serialize());
        }
        this.usersJson.save();
    }
    
    @Override
    public void saveTimers() {
        Map<String, Object> map = this.timersJson.getValues();
        map.clear();
        for (PlayerTimer timer : this.instance.getTimerManager().getPlayerTimers().values()) {
            map.put((timer.isPausable() ? "Normal:" : "") + timer.getName(), timer.getTimerCache().entrySet().stream().collect(Collectors.toMap(s -> s.getKey().toString(), s -> s.getValue().toString())));
            if (timer.isPausable()) {
                map.put("Paused:" + timer.getName(), timer.getPausedCache().entrySet().stream().collect(Collectors.toMap(s -> s.getKey().toString(), s -> s.getValue().toString())));
            }
        }
        map.put("SOTW:", String.valueOf(String.valueOf(this.instance.getTimerManager().getSotwTimer().getRemaining())));
        map.put("SOTW_ENABLED:", this.instance.getTimerManager().getSotwTimer().getEnabled().stream().map(UUID::toString).collect(Collectors.toList()));
        for (CustomTimer timer : this.instance.getTimerManager().getCustomTimers().values()) {
            map.put("CTimer:" + timer.getName(), timer.getRemaining().toString() + ":" + timer.getDisplayName());
        }
        this.timersJson.save();
    }
    
    public JsonStorage(StorageManager manager) {
        super(manager);
        this.teamsJson = new ConfigJson(this.instance, "data" + File.separator + "teams.json");
        this.usersJson = new ConfigJson(this.instance, "data" + File.separator + "users.json");
        this.timersJson = new ConfigJson(this.instance, "data" + File.separator + "timers.json");
    }
    
    @Override
    public void loadUsers() {
        Map<String, Object> map = this.usersJson.getValues();
        for (String s : map.keySet()) {
            Map<String, Object> fMap = (Map<String, Object>) map.get(s);
            new User(this.instance.getUserManager(), fMap);
        }
    }
    
    @Override
    public void saveUser(User user, boolean async) {
        if (async) {
            Tasks.executeAsync(this.manager, () -> this.saveUser(user, false));
            return;
        }
        Map<String, Object> map = user.serialize();
        this.usersJson.getValues().put(user.getUniqueID().toString(), map);
        this.usersJson.save();
    }
    
    @Override
    public void load() {
        this.loadTeams();
        this.loadUsers();
        this.loadTimers();
    }
    
    @Override
    public void saveTeams() {
        for (Team team : this.instance.getTeamManager().getTeams().values()) {
            this.teamsJson.getValues().put(team.getUniqueID().toString(), team.serialize());
        }
        this.teamsJson.save();
    }
    
    @Override
    public void deleteTeam(Team team) {
        this.teamsJson.getValues().remove(team.getUniqueID().toString());
        this.teamsJson.save();
    }
    
    @Override
    public void loadTeams() {
        Map<String, Object> teams = this.teamsJson.getValues();
        for (String name : teams.keySet()) {
            Map<String, Object> map = (Map<String, Object>) teams.get(name);
            Team team = null;
            switch (TeamType.valueOf((String) map.get("teamType"))) {
                case PLAYER: {
                    team = new PlayerTeam(this.instance.getTeamManager(), map);
                    break;
                }
                case SAFEZONE: {
                    team = new SafezoneTeam(this.instance.getTeamManager(), map);
                    break;
                }
                case ROAD: {
                    team = new RoadTeam(this.instance.getTeamManager(), map);
                    break;
                }
                case MOUNTAIN: {
                    team = new MountainTeam(this.instance.getTeamManager(), map);
                    break;
                }
                case EVENT: {
                    team = new EventTeam(this.instance.getTeamManager(), map);
                    break;
                }
                case CITADEL: {
                    team = new CitadelTeam(this.instance.getTeamManager(), map);
                    break;
                }
            }
            if (team == null) {
                this.instance.getLogger().log(Level.SEVERE, "[Titan] ¡Ocurrió un error al cargar un equipo! Informe con teams.json inmediatamente. - " + name);
            }
            else {
                this.instance.getTeamManager().getTeams().put(team.getUniqueID(), team);
                this.instance.getTeamManager().getStringTeams().put(team.getName(), team);
                for (Claim claim : team.getClaims()) {
                    this.instance.getTeamManager().getClaimManager().saveClaim(claim);
                }
            }
        }
    }
}
