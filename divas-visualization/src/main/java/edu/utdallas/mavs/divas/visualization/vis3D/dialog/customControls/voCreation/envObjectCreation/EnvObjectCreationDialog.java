package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.envObjectCreation;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.AbstractCreationDialogDefinition;

/**
 * This class represents the environment object creation dialog.
 */
public class EnvObjectCreationDialog extends AbstractDialog<EnvObjectCreationDialogController>
{
    /**
     * Constructs a dialog for environment object creation
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public EnvObjectCreationDialog(final Element parentElement)
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
        return "100%";
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
        
        parameters.put("title_label", "Environment Objects Creation");
        parameters.put("search_button", "Search");

        return parameters;
    }

    @Override
    public String getDialogId()
    {
        return "envObjectCreationDialog";
    }

    @Override
    public Class<AbstractCreationDialogDefinition> getDefinitionClass()
    {
        return AbstractCreationDialogDefinition.class;
    }

    @Override
    public Class<EnvObjectCreationDialogController> getControllerClass()
    {
        return EnvObjectCreationDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        AbstractCreationDialogDefinition.register(nifty);
    }
    
    @Override
    public String getControllerName()
    {
        return (new EnvObjectCreationDialogController()).getClass().getName();
    }
}
