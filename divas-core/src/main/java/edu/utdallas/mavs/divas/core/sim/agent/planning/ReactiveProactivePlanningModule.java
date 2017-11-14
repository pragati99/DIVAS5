package edu.utdallas.mavs.divas.core.sim.agent.planning;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;

/**
 * An abstract implementation of the agent's PCM. (Planning and Control Module)
 * 
 * @param <KM> The agent's knowledge module
 * @param <TM> The agent's Task module
 * @param <PE> The Agent's Plan Executor
 * @param <PG> The agent's plan generator
 * @param <RM> The agent's reactive module
 */
public abstract class ReactiveProactivePlanningModule<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>, PE extends PlanExecutor<KM, TM>, PG extends PlanGenerator<KM, TM>, RM extends ReactiveModule<KM, TM>> extends AbstractPlanningModule<KM, TM>
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's Plan Generator
     */
    protected PG              planGenerator;

    /**
     * The agent's plan executor
     */
    protected PE              planExecutor;

    /**
     * The agent's reactionModule
     */
    protected RM              reactiveModule;

    /**
     * Create a new PCM module using the plan generator and plan executor.
     * 
     * @param planGenerator The Plan generator
     * @param planExecutor The Plan executor.
     * @param knowledgeModule The knowledge module
     * @param reactiveModule The reactionModule
     */
    public ReactiveProactivePlanningModule(PG planGenerator, PE planExecutor, KM knowledgeModule, RM reactiveModule)
    {
        super(knowledgeModule);
        this.planGenerator = planGenerator;
        this.planExecutor = planExecutor;
        this.reactiveModule = reactiveModule;
    }

    /**
     * run the PCM filter to test whether or not immediate reaction is necessary
     * 
     * @return true if react is required
     */
    public abstract boolean isImmediateReactionRequired();

    @Override
    public Stimuli plan()
    {
        Stimuli sm;
        if(isImmediateReactionRequired())
        {
            Plan plan = reactiveModule.react();
            sm = planExecutor.executePlan(plan);
        }
        else
        {
            Plan plan = planGenerator.plan();
            sm = planExecutor.executePlan(plan);
        }
        return sm;
    }
}
