package edu.utdallas.mavs.divas.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is used to help the developers in the statistics operations.
 */
public class StatsHelper
{
    private Queue<Float> list       = new LinkedList<Float>();
    private int          windowSize = Integer.MAX_VALUE;

    /**
     * Constructs the StatsHelper.
     * 
     * @param windowSize
     *        The max count of numbers that could be stored in the queue.
     */
    public StatsHelper(int windowSize)
    {
        super();
        this.windowSize = windowSize;
    }

    /**
     * Enqueue the given number to the list. The list should not contain numbers more than the <code>windowSize</code>.
     * 
     * @param e
     *        The number to be enqueued
     */
    public void add(Float e)
    {
        if(list.size() > windowSize)
        {
            list.poll();
        }
        list.add(e);
    }

    /**
     * @return The average of the numbers stored in the list
     */
    public float getAverage()
    {
        float total = 0;
        for(Float f : list)
        {
            total += f;
        }
        return total / list.size();
    }

}
