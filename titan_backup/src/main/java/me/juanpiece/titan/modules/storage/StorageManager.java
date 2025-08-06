package me.juanpiece.titan.modules.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.storage.json.JsonStorage;
import me.juanpiece.titan.modules.storage.mongo.MongoStorage;
import me.juanpiece.titan.utils.NameThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class StorageManager extends Manager {

    private final Storage storage;
    private final ExecutorService executor;

    public StorageManager(HCF instance) {
        super(instance);
        this.storage = this.load();
        this.executor = Executors.newSingleThreadExecutor(new NameThreadFactory("Titan - StorageThread"));
    }

    @Override
    public void enable() {
        storage.load();
    }

    @Override
    public void disable() {
        storage.close();
        executor.shutdown();
    }

    public Storage load() {
        if (StorageType.valueOf(getConfig().getString("STORAGE_TYPE")) == StorageType.MONGO) {
            String URI;
            String configURI = getConfig().getString("MONGO.URI");

            if (configURI.isEmpty()) {
                URI = configURI;

            } else {
                URI = "mongodb://" + (getConfig().getBoolean("MONGO.AUTH.ENABLED") ?
                        getConfig().getString("MONGO.AUTH.USERNAME") + ":" + getConfig().getString("MONGO.AUTH.PASSWORD") + "@" : "") +
                        getConfig().getString("MONGO.SERVER_IP");
            }

            MongoClient client = MongoClients.create(URI);
            return new MongoStorage(this, client, client.getDatabase(getConfig().getString("MONGO.DATABASE")));

        } else {
            return new JsonStorage(this);
        }
    }
}