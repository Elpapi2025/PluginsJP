package me.titan.core.modules.storage;

import lombok.Getter;
import me.titan.core.modules.framework.*;
import me.titan.core.*;
import me.titan.core.modules.storage.mongo.*;
import me.titan.core.modules.storage.json.*;
import com.mongodb.client.*;

@Getter
public class StorageManager extends Manager {
    private final Storage storage;
    private final StorageType storageType;
    
    public StorageManager(HCF plugin) {
        super(plugin);
        this.storageType = StorageType.valueOf(this.getConfig().getString("STORAGE_TYPE"));
        this.storage = this.load();
    }
    
    @Override
    public void disable() {
        this.storage.close();
    }
    
    @Override
    public void enable() {
        this.storage.load();
    }
    
    public Storage load() {
        if (this.storageType == StorageType.MONGO) {
            String uri = "mongodb://" + (this.getConfig().getBoolean("MONGO.AUTH.ENABLED") ? this.getConfig().getString("MONGO.AUTH.USERNAME") + ":" + this.getConfig().getString("MONGO.AUTH.PASSWORD") + "@" : "") + this.getConfig().getString("MONGO.SERVER_IP");
            MongoClient client = MongoClients.create(uri);
            return new MongoStorage(this, client, client.getDatabase(this.getConfig().getString("MONGO.DATABASE")));
        }
        return new JsonStorage(this);
    }

}