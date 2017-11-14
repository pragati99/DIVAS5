package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentsPropertyModification;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;

/**
 * This class represents the dialog for modification of general properties for agents.
 */
public class AgentsPropertyModificationDialog extends AbstractDialog<AgentsPropertyModificationDialogController>
{
    /**
     * Constructs a dialog for general properties modification for agents.
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public AgentsPropertyModificationDialog(final Element parentElement)
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
        return "280px";
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
        
        parameters.put("title_label", "Agent General Properties");
       
        return parameters;
    }

    @Override
    public String getDialogId()
    {
        return "agentsPropertyModificationDialog";
    }
   
    @Override
    public Class<AgentsPropertyModificationDialogDefinition> getDefinitionClass()
    {
        return AgentsPropertyModificationDialogDefinition.class;
    }

    @Override
    public Class<AgentsPropertyModificationDialogController> getControllerClass()
    {
        return AgentsPropertyModificationDialogController.class;
    }

    @Override
    public String getDefinitionName()
    {
        return AgentsPropertyModificationDialogDefinition.NAME;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        AgentsPropertyModificationDialogDefinition.register(nifty);
    }

    @Override
    public String getControllerName()
    {
        return (new AgentsPropertyModificationDialogController()).getClass().getName();
    }
}
