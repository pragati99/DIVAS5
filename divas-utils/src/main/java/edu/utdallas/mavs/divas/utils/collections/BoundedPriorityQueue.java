package edu.utdallas.mavs.divas.utils.collections;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BoundedPriorityQueue<E> extends PriorityQueue<E>
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
     * @param comparator 
     */
    public BoundedPriorityQueue(int capacity, Comparator<E> comparator)
    {
        super(capacity, comparator);
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
