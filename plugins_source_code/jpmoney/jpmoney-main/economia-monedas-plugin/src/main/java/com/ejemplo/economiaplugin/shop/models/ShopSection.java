package com.ejemplo.economiaplugin.shop.models;

import java.util.Map;

public class ShopSection {
    private String id;
    private String title;
    private int size;
    private Map<Integer, String> items; // Slot -> ItemId

    public ShopSection(String id, String title, int size, Map<Integer, String> items) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.items = items;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getSize() { return size; }
    public Map<Integer, String> getItems() { return items; }
}