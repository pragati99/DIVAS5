package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception;

import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * This class describes an agent's perception module.
 */
public interface PerceptionModule
{
    /**
     * Configures and initializes this module's sensors
     */
    public void prepareSensors();

    /**
     * Process perception of a cell state, which represents a portion (total or partial) of the situated environment.
     * 
     * @param cell a cell state
     */
    public void perceive(CellState cell);

    /**
     * Combines perceptions. For instance, if a bomb emits both sound and light, after a bomb event, the
     * agent would have two distinct perceptions in its knowledge base if no combination of perceptions were applied.
     * After perception combination, both sound and light perceptions become associated to a single perceived event in
     * the agent's knowledge.
     */
    public void combinePerceptions();
}
