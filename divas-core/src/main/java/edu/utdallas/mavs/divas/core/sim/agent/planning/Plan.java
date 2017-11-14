package edu.utdallas.mavs.divas.core.sim.agent.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.task.Task;

/**
 * An agent's plan
 */
public class Plan implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Tasks of the plan.
     */
    protected List<Task>      tasks;

    /**
     * Create a new plan.
     */
    public Plan()
    {
        tasks = new ArrayList<Task>();
    }

    /**
     * Get all the tasks.
     * 
     * @return the tasks
     */
    public List<Task> getTasks()
    {
        return tasks;
    }

    /**
     * Add a task to the plan.
     * 
     * @param task
     *        a task to add
     */
    public void addTask(Task task)
    {
        tasks.add(task);
    }

    /**
     * Format plan for printing
     */
    @Override
    public String toString()
    {
        String str = "Tasks:";
        for(Task t : tasks)
        {
            str += " " + t.toString();
        }
        return str;
    }
}
