package edu.utdallas.mavs.divas.utils.collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class describes a bounded FIFO hash map.
 * 
 * @param <K> key type
 * @param <V> value type
 */
public class FastBoundedHashMap<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = 1L;

    /**
     * The capacity of the map
     */
    protected int             capacity;

    /**
     * Creates a new bounded FIFO hash map
     * 
     * @param capacity the capcaity of the hash map
     */
    public FastBoundedHashMap(int capacity)
    {
        super();
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {        
        return size() >= capacity;
    }
}
