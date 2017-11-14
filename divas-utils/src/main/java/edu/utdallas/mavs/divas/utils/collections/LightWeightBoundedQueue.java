package edu.utdallas.mavs.divas.utils.collections;

import java.util.ArrayDeque;

/**
 * @param <E>
 */
public class LightWeightBoundedQueue<E> extends ArrayDeque<E>
{
    private static final long serialVersionUID = 1L;

    /**
     * The capacity of the Queue
     */
    protected int             capacity;

    /**
     * Creates a new bounded FIFO hash map
     * 
     * @param capacity
     *        the capcaity of the hash map
     */
    public LightWeightBoundedQueue(int capacity)
    {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public boolean add(E e)
    {
        if(this.size() >= capacity)
        {
            this.poll();
        }
        return super.add(e);
    }
}
