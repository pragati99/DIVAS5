package edu.utdallas.mavs.divas.core.sim.env;

import java.io.File;
import java.util.List;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.Phase;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * The environment manager oversees the execution of locally-run cell controllers, provides a
 * shared communication module, and contains an Environment Cell Map which is shared by
 * the locally-run cell controllers. The cell map contains an updated view of the cell structure
 * of the entire decentralized virtual environment.
 * 
 * @param <CC>
 *        the cell controller's type
 */
public interface Environment<CC extends CellController>
{
    /**
     * @return The <code>CellMap</code> of the environment.
     */
    public CellMap getCellMap();

    /**
     * @param id
     *        The <code>CellID</code> for the cell controller to be found.
     * @return The <code>CellController</code> for the given <code>CellID</code>
     */
    public CC getCellController(CellID id);

    /**
     * Gets the cell controller that contains the given position.
     * 
     * @param position
     *        A <code>Vector3f</code> to be looked for.
     * @return A <code>CellController</code> that contains the given position.
     */
    public CC getCellController(Vector3f position);

    /**
     * Gets a list of all cell controllers in the simulation.
     * 
     * @return The list of cell controllers.
     */
    public List<CC> getCellControllers();

    /**
     * Loads a simulation environment from.
     * 
     * @param file
     *        the environment file
     */
    public void loadEnvironment(File file);
    
    /**
     * Saves the simulation environment to the given name.
     * 
     * @param name
     *        the name of the file to save the environment to.
     */
    public void saveEnvironment(String name);

    /**
     * Loading a previously saved simulation environment from the given environment instance.
     * 
     * @param <E>
     *        The simulation environment type
     * @param environment
     *        The environment instance from which values will be retrieved in order to update this simulation environment with.
     */
    public <E extends Environment<?>> void copyFrom(E environment);

    /**
     * Executes the given phase of the simulation.
     * 
     * @param cycles
     *        The cycle number.
     * @param phase
     *        The phase to be executed.
     */
    public void executePhase(long cycles, Phase phase);

    /**
     * Checks if the environment has already combined all perception data for a given agent in the current simulation
     * cycle.
     * 
     * @param agentId
     *        the ID of the agent
     * @return true if the environment state has already been combined. Otherwise, false.
     */
    public boolean hasCombinedPerceptionDataFor(int agentId);

    /**
     * Retrieves the environment combined perception data for a given agent.
     * 
     * @param agentId
     *        the ID of the agent
     * @return the combined cell state (containing the perception data) of the environment.
     */
    public CellState retrievePerceptionDataFor(int agentId);

    /**
     * Terminates the environment by destroying all the cell controllers, clearing the event listeners,
     * deleting the observers and disconnecting from the communication module.
     */
    public void terminate();
}
