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
public class SelectionPicker extends Picker
{
    private AmbientLight agentTrackingLight;
    private AmbientLight objectTrackingLight;

    /**
     * Creates and initializes the selection picker
     * 
     * @param agentTrackingLight the selection light to highlight selected agents
     * @param objectTrackingLight the selection light to highlight selected objects
     */
    public SelectionPicker(AmbientLight agentTrackingLight, AmbientLight objectTrackingLight)
    {
        this.agentTrackingLight = agentTrackingLight;
        this.objectTrackingLight = objectTrackingLight;
    }

    @Override
    protected void updateEnvObjectSelection(EnvObjectVO envObj)
    {
        if(!envObj.isSelected() && !envObj.isLocked())
        {
            envObj.setTrackingLight(objectTrackingLight);
            envObj.setSelected(true);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("%s[%s] was selected", envObj.getState().getType(), envObj.getState().getID()));
        }

        else
        {
            envObj.setSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("%s[%s] was unselected", envObj.getState().getType(), envObj.getState().getID()));
        }
    }

    @Override
    protected void updateAgentSelection(AgentVO agent)
    {
        if(!agent.isSelected())
        {
            agent.setTrackingLight(agentTrackingLight);
            agent.setSelected(true);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("Agent[%s,%s] was selected", agent.getState().getAgentType(), agent.getState().getID()));
        }

        else
        {
            agent.setSelected(false);
            Visualizer3DApplication.getInstance().getApp().displayMessage(String.format("Agent[%s,%s] was unselected", agent.getState().getAgentType(), agent.getState().getID()));
        }
    }
}
