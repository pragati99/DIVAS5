package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;

/**
 * An Agent's Plan Evaluator
 */
public class PlanEvaluator<P extends EvacuationPlan> implements Serializable
{
    private static final long serialVersionUID = -8249907719082180498L;

    /**
     * The agent's knowledge module
     */
    HumanKnowledgeModule      knowledgeModule;

    /**
     * Create the plan evaluator
     * 
     * @param km
     *        the agent's knowledge module
     */
    public PlanEvaluator(HumanKnowledgeModule km)
    {
        knowledgeModule = km;
    }

    /**
     * Evaluate and assign a utility to the plan
     * 
     * @param plan
     *        the plan
     */
    public void evaluatePlan(P plan)
    {
        float utility = 0;

        utility = knowledgeModule.getTime();
        plan.setPlanUtility(utility);
    }
}
