package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voTracking;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;

/**
 * This class represents the agent tracking dialog.
 */
public class AgentTrackingDialog extends AbstractDialog<AgentTrackingDialogController>
{
    /**
     * Constructs a dialog for agent tracking
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public AgentTrackingDialog(final Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public String getWidth()
    {
        return "100%";
    }

    @Override
    public String getHeight()
    {
        return "70px";
    }

    @Override
    public String getAlignment()
    {
        return "center";
    }

    @Override
    public Map<String, String> getParameters()
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("title_label", "Agent Tracking");
        return parameters;
    }

    @Override
    public String getDialogId()
    {
        return "agentTrackingDialog";
    }

    @Override
    public Class<AgentTrackingDialogDefinition> getDefinitionClass()
    {
        return AgentTrackingDialogDefinition.class;
    }

    @Override
    public Class<AgentTrackingDialogController> getControllerClass()
    {
        return AgentTrackingDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        AgentTrackingDialogDefinition.register(nifty);
    }

    @Override
    public String getControllerName()
    {
        return (new AgentTrackingDialogController()).getClass().getName();
    }
}
