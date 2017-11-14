package edu.utdallas.mavs.divas.visualization.vis3D.engine;

import com.jme3.light.AmbientLight;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;

/**
 * This class handles the mouse selection on the 3D visualization.
 * <p>
 * This class extends the general {@link Picker} for the selection and marking of agents. The selected agents get automatically highlighted by this picker.
 */
public class ContextSelectionPicker extends Picker
{
    private EnvObjectVO  selectedEnvObj      = null;
    private AgentVO      selectedAgent       = null;

    private AmbientLight agentTrackingLight  = null;
    private AmbientLight objectTrackingLight = null;

    /**
     * Creates and initializes the selection picker
     * 
     * @param agentTrackingLight the selection light to highlight selected agents
     * @param objectTrackingLight the selection light to highlight selected objects
     */
    public ContextSelectionPicker(AmbientLight agentTrackingLight, AmbientLight objectTrackingLight)
    {
        this.agentTrackingLight = agentTrackingLight;
        this.objectTrackingLight = objectTrackingLight;
    }

    @Override
    protected void updateEnvObjectSelection(EnvObjectVO envObj)
    {
        if(!envObj.isContextSelected())
        {
            removeSelections();

            selectedEnvObj = envObj;
            envObj.setTrackingLight(objectTrackingLight);
            envObj.setContextSelected(true);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("%s[%s] was selected", envObj.getState().getType(), envObj.getState().getID()));
        }
        else
        {
            selectedEnvObj = null;
            envObj.setContextSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("%s[%s] was unselected", envObj.getState().getType(), envObj.getState().getID()));
        }
    }

    @Override
    protected void updateAgentSelection(AgentVO agent)
    {
        if(!agent.isContextSelected())
        {
            removeSelections();

            selectedAgent = agent;
            agent.setTrackingLight(agentTrackingLight);
            agent.setContextSelected(true);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("Agent[%s,%s] was selected", agent.getState().getAgentType(), agent.getState().getID()));
        }
        else
        {
            selectedAgent = null;
            agent.setContextSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("Agent[%s,%s] was unselected", agent.getState().getAgentType(), agent.getState().getID()));
        }
    }

    /**
     * Removes context selections
     */
    public void removeSelections()
    {
        if(selectedAgent != null)
        {
            selectedAgent.setContextSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("Agent[%s,%s] was unselected", selectedAgent.getState().getAgentType(), selectedAgent.getState().getID()));
        }
        if(selectedEnvObj != null)
        {
            selectedEnvObj.setContextSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("%s[%s] was unselected", selectedEnvObj.getState().getType(), selectedEnvObj.getState().getID()));
        }
    }

    /**
     * Gets the context selected agent
     * 
     * @return the selected agents
     */
    public AgentVO getSelectedAgent()
    {
        return selectedAgent;
    }
}
