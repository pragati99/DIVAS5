package edu.utdallas.mavs.divas.utils.collections;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class implements a bounded FIFO queue
 * 
 * @param <E> the queue elements type
 */
public class LinkedBoundedQueue<E> extends LinkedBlockingQueue<E>
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new linked queue
     */
    public LinkedBoundedQueue()
    {
        super();
    }

    /**
     * Creates a new linked queue with a predefined collection
     * 
     * @param c the collection to be added to the queue
     */
    public LinkedBoundedQueue(Collection<? extends E> c)
    {
        super(c);
    }

    /**
     * Creates a new linked queue with a specific capacity
     * 
     * @param capacity the queue's capacity
     */
    public LinkedBoundedQueue(int capacity)
    {
        super(capacity);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException
    {
        ensureBounds();
        return super.offer(e, timeout, unit);
    }

    @Override
    public boolean offer(E e)
    {
        ensureBounds();
        return super.offer(e);
    }

    @Override
    public void put(E e) throws InterruptedException
    {
        ensureBounds();
        super.put(e);
    }

    @Override
    public boolean add(E e)
    {
        ensureBounds();
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        for(E e : c)
            add(e);
        return true;
    }

    private void ensureBounds()
    {
        if(remainingCapacity() == 0)
        {
            poll();
        }
    }
}
