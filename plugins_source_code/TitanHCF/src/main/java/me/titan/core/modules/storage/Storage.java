package me.titan.core.modules.storage;

import me.titan.core.modules.teams.Team;
import me.titan.core.modules.users.User;

public interface Storage {
    void saveTeam(Team team, boolean async);
    
    void close();
    
    void loadTeams();
    
    void loadTimers();
    
    void saveTimers();
    
    void saveUsers();
    
    void load();
    
    void saveTeams();
    
    void saveUser(User user, boolean async);
    
    void deleteTeam(Team team);
    
    void loadUsers();
}