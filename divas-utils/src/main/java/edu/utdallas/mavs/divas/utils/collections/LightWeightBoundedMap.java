package edu.utdallas.mavs.divas.utils.collections;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * @param <K>
 * @param <V>
 */
public class LightWeightBoundedMap<K, V> extends HashMap<K, V>
{
    private static final long serialVersionUID = 1L;

    /**
     * The capacity of the map
     */
    protected int             capacity;

    /**
     * Insertion order of the map
     */
    protected ArrayDeque<K>   order;

    /**
     * Creates a new bounded FIFO hash map
     * 
     * @param capacity
     *        the capcaity of the hash map
     */
    public LightWeightBoundedMap(int capacity)
    {
        super();
        this.capacity = capacity;
        order = new ArrayDeque<>();
    }

    @Override
    public V put(K k, V v)
    {
        order.add(k);
        if(this.size() >= capacity)
        {
            this.remove(order.poll());
        }
        return super.put(k, v);
    }
}
