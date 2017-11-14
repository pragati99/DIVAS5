package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;

public class EvacuationPlan extends Plan
{
    /**
     * The utility of the plan.
     */
    protected float planUtility;
    /**
     * The most recently selected task from the plan
     */
    protected int   lastUsedIndex;

    AgentPath       agentPath;

    public EvacuationPlan()
    {
        super();
        planUtility = Float.NEGATIVE_INFINITY;
        agentPath = new AgentPath();
    }

    /**
     * Get the plans utility.
     * 
     * @return utility
     */
    public float getPlanUtility()
    {
        return planUtility;
    }

    /**
     * Set the plan's utility.
     * 
     * @param planUtility
     *        the utility
     */
    public void setPlanUtility(float planUtility)
    {
        this.planUtility = planUtility;
    }

    public AgentPath getAgentPath()
    {
        return agentPath;
    }

    public void setAgentPath(AgentPath agentPath)
    {
        this.agentPath = agentPath;
    }

}
