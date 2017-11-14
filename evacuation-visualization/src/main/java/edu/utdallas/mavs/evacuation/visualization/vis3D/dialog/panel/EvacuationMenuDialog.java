package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.panel;

import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.MenuDialog;

/**
 * This class represents the evacuation simulation nifty menu dialog.
 */
public class EvacuationMenuDialog extends MenuDialog
{
    /**
     * Constructs a new evacuation simulation nifty Menu dialog
     * 
     * @param parentElement
     *        The parent element of this dialog
     */
    public EvacuationMenuDialog(Element parentElement)
    {
        super(parentElement);
    }

    @Override
    public String getControllerName()
    {
        return (new EvacuationMenuDialogController()).getClass().getName();
    }
}
