package com.ejemplo.economiaplugin.data;

import java.util.Map;
import java.util.UUID;

public interface DataManager {

    void setup();

    Map<String, Double> getBalances(UUID playerUUID);

    double getBalance(UUID playerUUID, String currencyId);

    void setBalance(UUID playerUUID, String currencyId, double balance);

    void close();
}
