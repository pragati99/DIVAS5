package edu.utdallas.mavs.divas.utils.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;

public class BoundedPrioritySet<V> extends HashMap<V, V>
{
    private static final long serialVersionUID = 1L;

    /**
     * The capacity of the map
     */
    protected int             capacity;

    /**
     * Insertion order of the map
     */
    protected Queue<V>        order;

    /**
     * Creates a new bounded FIFO hash map
     * 
     * @param capacity
     *        the capcaity of the hash map
     * @param comparator
     */
    public BoundedPrioritySet(int capacity, Comparator<V> comparator)
    {
        super();
        this.capacity = capacity;
        order = new BoundedPriorityQueue<>(capacity, comparator);
    }

    public void add(V e)
    {
        order.add(e);
        super.put(e, e);
        if(this.size() > capacity)
        {
            V removed = null;
            while(removed == null)
            {
                removed = remove(order.poll());
            }
        }
    }

}
