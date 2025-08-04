package me.titan.core.utils.extra;

import lombok.Data;

@Data
public class Pair<K, V> {
    
    private V value;
    private K key;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
