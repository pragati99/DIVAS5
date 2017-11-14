package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The module that stores and manages an agent's plan.
 */
public class PlanStorageModule<P extends EvacuationPlan> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * A list of plans.
     */
    protected List<P>         plans;
    /**
     * The best plan
     */
    protected P               bestPlan;

    /**
     * Create a new plan storage module
     */
    @SuppressWarnings("unchecked")
    public PlanStorageModule()
    {
        plans = new ArrayList<P>();
        bestPlan = (P) new EvacuationPlan();
    }

    /**
     * Get the best plan.
     * 
     * @return the best plan
     */
    public P getBestPlan()
    {
        return bestPlan;
    }

    /**
     * Set the best plan
     * 
     * @param bestPlan
     *        the best plan
     */
    public void setBestPlan(P bestPlan)
    {
        this.bestPlan = bestPlan;
    }

    /**
     * Reset the plan module.
     */
    public void clear()
    {
        plans.clear();
    }

    /**
     * Add a plan to the planning module and redefine the best plan if needed.
     * 
     * @param plan
     *        the plan to be added
     */
    public void addPlan(P plan)
    {
        plans.add(plan);
        if(plan.getPlanUtility() > bestPlan.getPlanUtility())
        {
            bestPlan = plan;
        }
    }
}
