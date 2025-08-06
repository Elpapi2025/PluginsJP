package me.juanpiece.titan.modules.storage;

import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.users.User;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface Storage {

    void loadTeams();

    void saveTeams();

    void saveTeam(Team team, boolean async);

    void deleteTeam(Team team);

    void deleteUser(User user);

    void loadUsers();

    void saveUsers();

    void saveUser(User user, boolean async);

    void loadTimers();

    void saveTimers();

    void load();

    void close();
}