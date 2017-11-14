package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;

/**
 * This class represents the menu dialog.
 */
public class MenuDialog extends AbstractDialog<MenuDialogController> // implements Dialog<MenuDialogDefinition, MenuDialogController>
{
    /**
     * Constructs a new Menu dialog
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public MenuDialog(Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public String getWidth()
    {
        return "300px";
    }

    @Override
    public String getHeight()
    {
        return "100%";
    }

    @Override
    public String getAlignment()
    {
        return "right";
    }

    @Override
    public Map<String, String> getParameters()
    {
        return new HashMap<String, String>();
    }

    @Override
    public String getDialogId()
    {
        return "menuWindow";
    }

    @Override
    public Class<MenuDialogDefinition> getDefinitionClass()
    {
        return MenuDialogDefinition.class;
    }

    @Override
    public Class<MenuDialogController> getControllerClass()
    {
        return MenuDialogController.class;
    }

    @Override
    public void registerNiftyDefinition(Nifty nifty)
    {
        MenuDialogDefinition.register(nifty);
    }

    @Override
    public String getControllerName()
    {
        return (new MenuDialogController()).getClass().getName();
    }

    @Override
    public void showDialog()
    {
        super.showDialog();
        content.getControl(getControllerClass()).hideUnselected();
    }
}
