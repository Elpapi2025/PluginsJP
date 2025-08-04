package me.titan.core.modules.storage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.titan.core.modules.framework.HCFModule;
import me.titan.core.modules.storage.Storage;
import me.titan.core.modules.storage.StorageManager;
import me.titan.core.modules.storage.json.JsonStorage;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.claims.Claim;
import me.titan.core.modules.teams.enums.TeamType;
import me.titan.core.modules.teams.type.*;
import me.titan.core.modules.users.User;
import me.titan.core.utils.Tasks;
import org.bson.Document;

import java.util.logging.Level;

public class MongoStorage extends HCFModule<StorageManager> implements Storage {
    private final MongoCollection<Document> teamsCollection;
    private final MongoClient mongoClient;
    private final JsonStorage timerStorage;
    private final MongoCollection<Document> usersCollection;
    
    @Override
    public void loadTeams() {
        for (Document document : this.teamsCollection.find()) {
            Team team = null;
            switch (TeamType.valueOf(document.getString("type"))) {
                case PLAYER: {
                    team = new PlayerTeam(this.instance.getTeamManager(), document);
                    break;
                }
                case SAFEZONE: {
                    team = new SafezoneTeam(this.instance.getTeamManager(), document);
                    break;
                }
                case ROAD: {
                    team = new RoadTeam(this.instance.getTeamManager(), document);
                    break;
                }
                case MOUNTAIN: {
                    team = new MountainTeam(this.instance.getTeamManager(), document);
                    break;
                }
                case EVENT: {
                    team = new EventTeam(this.instance.getTeamManager(), document);
                    break;
                }
                case CITADEL: {
                    team = new CitadelTeam(this.instance.getTeamManager(), document);
                    break;
                }
            }
            if (team == null) {
                this.instance.getLogger().log(Level.SEVERE, "[Titan] ¡Ocurrió un error al cargar un equipo! ¡Reporte inmediatamente! (MONGO)");
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
    
    @Override
    public void saveUsers() {
        for (User user : this.instance.getUserManager().getUsers().values()) {
            this.saveUser(user, false);
        }
    }
    
    @Override
    public void load() {
        this.loadTeams();
        this.loadUsers();
        this.loadTimers();
    }
    
    @Override
    public void saveUser(User user, boolean async) {
        if (async) {
            Tasks.executeAsync(this.manager, () -> this.saveUser(user, false));
            return;
        }
        Document document = new Document("_id", user.getUniqueID().toString());
        document.putAll(user.serialize());
        this.usersCollection.replaceOne(Filters.eq("_id", user.getUniqueID().toString()), document, new ReplaceOptions().upsert(true));
    }
    
    @Override
    public void close() {
        this.saveTimers();
        this.saveTeams();
        this.saveUsers();
        this.mongoClient.close();
    }
    
    @Override
    public void loadUsers() {
        for (Document document : this.usersCollection.find()) {
            new User(this.instance.getUserManager(), document);
        }
    }
    
    @Override
    public void loadTimers() {
        this.timerStorage.loadTimers();
    }
    
    @Override
    public void saveTeam(Team team, boolean async) {
        if (async) {
            Tasks.executeAsync(this.manager, () -> this.saveTeam(team, false));
            return;
        }
        Document document = new Document("_id", team.getUniqueID().toString());
        document.putAll(team.serialize());
        this.teamsCollection.replaceOne(Filters.eq("_id", team.getUniqueID().toString()), document, new ReplaceOptions().upsert(true));
    }
    
    @Override
    public void deleteTeam(Team team) {
        this.teamsCollection.deleteOne(Filters.eq("_id", team.getUniqueID().toString()));
    }
    
    @Override
    public void saveTimers() {
        this.timerStorage.saveTimers();
    }
    
    @Override
    public void saveTeams() {
        for (Team team : this.instance.getTeamManager().getTeams().values()) {
            this.saveTeam(team, false);
        }
    }
    
    public MongoStorage(StorageManager manager, MongoClient client, MongoDatabase database) {
        super(manager);
        this.mongoClient = client;
        this.teamsCollection = database.getCollection("teams");
        this.usersCollection = database.getCollection("users");
        this.timerStorage = new JsonStorage(manager);
    }
}
