package me.keano.azurite.utils.extra;

import lombok.Setter;

import java.util.Objects;

/**
 * <p>A convenience class to represent name-value pairs.</p>
 *
 * @since JavaFX 2.0
 */
@Setter
public class Pair<K, V> {

    /**
     * Key of this <code>Pair</code>.
     */
    private K key;

    /**
     * Gets the key for this pair.
     *
     * @return key for this pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Value of this <code>Pair</code>.
     */
    private V value;

    /**
     * Gets the value for this pair.
     *
     * @return value for this pair
     */
    public V getValue() {
        return value;
    }

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}