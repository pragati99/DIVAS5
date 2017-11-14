package edu.utdallas.mavs.divas.visualization.vis2D;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.visualization.vis2D.spectator.VisualSpectator2D;

/**
 * Attaches the simAdapter to the 2D visualizer and start the visual spectator
 */
public class Visualizer2DApplication
{

	private static Visualizer2DApplication	instance;

	private final SimAdapter				simClientAdapter;

	private final VisualSpectator2D			spectator;

	/**
	 * Constructs the Visualizer2DApplication class
	 * 
	 * @param simClientAdapter
	 *        The SimAdapter which communicate with the simulation core
	 * @param spectator
	 *        The visual spectator which handles the messages coming from the core
	 */
	@Inject
	public Visualizer2DApplication(SimAdapter simClientAdapter, VisualSpectator2D spectator)
	{

		this.simClientAdapter = simClientAdapter;
		this.spectator = spectator;
		instance = this;
	}

	/**
	 * gets the singleton instance of the <code>Visualizer2DApplication<code>
	 * 
	 * @return Static instance of the Visualizer2DApplication
	 */
	public static Visualizer2DApplication getInstance()
	{
		return instance;
	}

	/**
	 * Calls the spectator Start method to setup the subscriptions for messages coming from the simulation
	 */
	public void start()
	{
		spectator.start();
	}

	/**
	 * Calls the spectator stop method to end the subscriptions for messages coming from the simulation
	 */
	public void stop()
	{
		spectator.stop();
	}

	/**
	 * Gets the <code>SimCommander</code> attached to this application
	 * 
	 * @return An instance of the simClientAdapter that communicate with the simulation core
	 */
	public SimAdapter getSimCommander()
	{
		return simClientAdapter;
	}
}
