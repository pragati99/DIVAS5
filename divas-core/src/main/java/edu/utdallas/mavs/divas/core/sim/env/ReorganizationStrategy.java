package edu.utdallas.mavs.divas.core.sim.env;

/**
 * Enumeration of the environment reorganization strategies.
 */
public enum ReorganizationStrategy
{
    /**
     * There is no reorganization strategy.
     */
    No_Reorganization,
    /**
     * There is no reorganization strategy and the environment is organized with one cell.
     */
    One_Cell,
    /**
     * The environment reorganization strategy is based on the agent population per cell controller.
     */
    Basic_Agent_Population_Balance,
    /**
     * The environment reorganization strategy is based on the agent and environment objects population
     * per cell controller.
     */
    Basic_Agent_and_EnvObject_Population_Balance,
    /**
     * The environment reorganization strategy is based on the agent population per cell controller, and the
     * priority is given to split cell controllers requests over merge cell controllers requests.
     */
    Greedy_Agent_Population_Balance,
    /**
     * The environment reorganization strategy is based on the agent and environment objects population per
     * cell controller and the priority is given to splitting cell controllers requests over merge cell
     * controllers requests.
     */
    Greedy_Agent_and_EnvObject_Population_Balance,
    /**
     * The environment reorganization strategy is based on the agent population per cell controller,
     * and the split and merge requests have a fair priority.
     */
    Fair_Agent_Population_Balance,
    /**
     * The environment reorganization strategy is based on the agent and environment objects population per
     * cell controller, and the split and merge requests have a fair priority.
     */
    Fair_Agent_and_EnvObjects_Population_Balance,
    /**
     * The environment reorganization strategy is based on bottom-up interactions solely, without any form of coordination.
     * The coordinator acts as pass-through agent. This strategy may lead to chaotic scenarios.
     */
    Self_Organizing,
    /**
     * The environment reorganization strategy is based on bottom-up, top-down interactions between cell controllers and coordinators.
     */
    Autonomic

}
