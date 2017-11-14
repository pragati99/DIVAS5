package edu.utdallas.mavs.divas.core.sim.agent.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;

/**
 * This class describes an agent's abstract task module.
 * 
 * @param <KM> the agent's knowledge module type
 */
public abstract class AbstractTaskModule<KM extends KnowledgeModule<?>> implements TaskModule<KM>, Serializable
{
    private static final long   serialVersionUID = 1L;

    /**
     * This agent's knowledge module
     */
    protected KM                knowledgeModule;

    /**
     * The list of available tasks in this task module
     */
    protected Map<String, Task> tasks;

    /**
     * Creates a new task module
     * 
     * @param knowledgeModule the agent's knowledge module
     */
    public AbstractTaskModule(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
        this.tasks = new HashMap<String, Task>();
        loadTasks();
    }

    /**
     * Load the agent's task definitions
     */
    protected abstract void loadTasks();

    @Override
    public Task createTask(String type, long executionCycle)
    {
        for(Task t : tasks.values())
        {
            if(t.getType().equals(type))
            {
                return t.createTask(executionCycle);
            }
        }
        return null;
    }

    @Override
    public boolean containsTask(String type)
    {
        for(Task t : tasks.values())
        {
            if(t.getType().equals(type))
            {
                return true;
            }
        }
        return false;
    }
}
