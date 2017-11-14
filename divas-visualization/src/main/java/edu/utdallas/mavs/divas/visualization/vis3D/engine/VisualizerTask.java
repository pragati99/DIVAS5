package edu.utdallas.mavs.divas.visualization.vis3D.engine;

import java.util.concurrent.Callable;

import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;

/**
 * This class describes a task of the visualizer.
 * <p>
 * Instance of this class represents tasks to be enqueued in the update queue of the {@link BaseApplication} and
 * processed concurrently during the simple update loop.
 */
public class VisualizerTask implements Callable<Object>
{
    private long             cycle;
    private Callable<Object> task;

    /**
     * Constructs a task to be executed.
     * 
     * @param task a {@link Callable} to be executed in the {@link BaseApplication} update loop
     * @param cycle the cycle number of the simulation relative to this task
     */
    public VisualizerTask(Callable<Object> task, long cycle)
    {
        super();
        this.cycle = cycle;
        this.task = task;
    }

    @Override
    public Object call() throws Exception
    {
        return task.call();
    }

    /**
     * Gets the cycle number of this task
     * 
     * @return the current cycle number of the simulation relative to this task
     */
    public long getCycle()
    {
        return cycle;
    }

}
