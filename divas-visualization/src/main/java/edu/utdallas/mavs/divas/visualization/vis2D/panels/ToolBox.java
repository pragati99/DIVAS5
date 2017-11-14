package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis2D.panels.HeaderPanel.SimulationMode;

/**
 * This class is the parent panel or the container panel for all the tools panels used
 * to interact with the simulation.
 */
public class ToolBox extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	private SimulationMode		simMode				= SimulationMode.SELECTION_MODE;
	private final HeaderPanel	headerPanel;
	private SimulationModePanel	simulationModePanel;
	private AgentTrackingPanel	agentTrackingPanel;
	private AddEnvObjPanel		addEnvObjPanel;

	/**
	 * Constructs the container panel for all tools panels in the 2D visualizer. 
	 * And creates all child tools panels inside it.
	 */
	public ToolBox()
	{
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		headerPanel = new HeaderPanel();
		headerPanel.setLayout(new GridLayout(4, 1));
		add(headerPanel);

		simulationModePanel = new SimulationModePanel(this);

		add(simulationModePanel);

		agentTrackingPanel = new AgentTrackingPanel();

		add(agentTrackingPanel);

		addEnvObjPanel = new AddEnvObjPanel(this);
		add(addEnvObjPanel);

		simMode = SimulationMode.SELECTION_MODE;
		headerPanel.getSimModeLabel().setText("Current Simulation Mode: " + simMode.toString());
	}

	/**
	 * Gets the current simulation mode that specifies the mouse click behavior on the
	 * visualization panel.
	 * 
	 * @return The current simulation mode
	 */
	public SimulationMode getSimMode()
	{
		return simMode;
	}

	/**
	 * @return boolean flag that indicates if the vision cone is enabled for the agents in the visualization.
	 */
	public boolean isVisionCone()
	{
		return simulationModePanel.getVisionCone();
	}

	/**
	 * @return boolean flag that indicates if the agent Id tool tip is attached to the agent in the visualizer.
	 */
	public boolean isAgentIdToolTip()
	{
		return simulationModePanel.getAgentIdToolTip();
	}

	/**
	 * Indicates if the agent is in the track list. Agents in the track list will be visualized to
	 * be distinguished from other agents in order to ease the ability to track an agent in the visualizer.
	 * 
	 * @param agentId
	 *        Agent Id to be tested if it's tracked
	 * @return boolean flag that indicates if the agent of the given Id is in the track list.
	 */
	public boolean agentIsTracked(Integer agentId)
	{
		return agentTrackingPanel.agentIsTracked(agentId);
	}

	/**
	 * Gets the environment object state that is generated when the user chooses an
	 * environment object from Adding environment object panel. This state will be
	 * used later to create the environment object in the simulation.
	 * 
	 * @return An environment object state for the selected environment object.
	 */
	public EnvObjectState getCandidateEnvObj()
	{
		return addEnvObjPanel.getCandidateEnvObj();
	}

	/**
	 * @return boolean flag that indicates if the image of the environment object will be
	 *         shown instead of the rectangular shape of the object.
	 */
	public boolean isShowEnvObjImage()
	{
		return addEnvObjPanel.getShowEnvObjImage();
	}

	/**
	 * Sets the current simulation mode to the given simulation mode.
	 * 
	 * @param simMode
	 *        simulation mode to be set.
	 */
	public void setSimMode(SimulationMode simMode)
	{
		this.simMode = simMode;
	}

	/**
	 * Sets the simulation mode label in the header panel with the given string.
	 * 
	 * @param simMode
	 *        String to be set in the simulation mode label
	 */
	public void setSimModeLabel(String simMode)
	{
		headerPanel.getSimModeLabel().setText(simMode);
	}

	/**
	 * Gets the current zoom factor for the 2D visualizer normalized to values between 0.1 and 2.
	 * Because the <code>JSlider</code> constructor only accept integer values.
	 * 
	 * @return The current zoom factor for the 2D visualizer.
	 */
	public Double getZoomFactor()
	{
		return headerPanel.getZoomFactor() / 5.0;
	}

}
