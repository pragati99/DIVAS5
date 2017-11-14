package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;

/**
 * This class represents the help dialog.
 */
public class HelpDialog extends AbstractDialog<HelpDialogController>
{
    /**
     * Constructs a new Help dialog
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public HelpDialog(final Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public String getWidth()
    {
        return "70%";
    }

    @Override
    public String getHeight()
    {
        return "85%";
    }

    @Override
    public String getAlignment()
    {
        return "left";
    }
    
    @Override
    public Map<String, String> getParameters()
    {
        return new HashMap<String, String>();
    }

    @Override
    public String getDialogId()
    {
        return "helpPanel";
    }

    @Override
    public Class<HelpDialogDefinition> getDefinitionClass()
    {
        return HelpDialogDefinition.class;
    }

    @Override
    public Class<HelpDialogController> getControllerClass()
    {
        return HelpDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        HelpDialogDefinition.register(nifty);
    }

    @Override
    public String getControllerName()
    {
        return (new HelpDialogController()).getClass().getName();
    }
}
