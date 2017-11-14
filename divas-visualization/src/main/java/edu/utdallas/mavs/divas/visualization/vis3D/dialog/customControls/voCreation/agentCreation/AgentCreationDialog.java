package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.agentCreation;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.AbstractCreationDialogDefinition;

/**
 * This class represents the agent creation dialog.
 */
public class AgentCreationDialog extends AbstractDialog<AgentCreationDialogController>
{
    /**
     * Constructs a dialog for agent creation
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public AgentCreationDialog(final Element parentElement)
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
        return "*";
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

        parameters.put("title_label", "Agent Creation");
        parameters.put("search_button", "Search");

        return parameters;
    }

    @Override
    public String getDialogId()
    {
        return "agentCreationDialog";
    }

    @Override
    public Class<AgentCreationDialogController> getControllerClass()
    {
        return AgentCreationDialogController.class;
    }

    @Override
    public Class<AbstractCreationDialogDefinition> getDefinitionClass()
    {
        return AbstractCreationDialogDefinition.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        AbstractCreationDialogDefinition.register(nifty);
    }

    @Override
    public String getControllerName()
    {
        return (new AgentCreationDialogController()).getClass().getName();
    }
}
