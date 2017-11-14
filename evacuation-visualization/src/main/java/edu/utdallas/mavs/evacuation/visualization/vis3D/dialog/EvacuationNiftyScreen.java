package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog;

import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.HelpDialog;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.panel.EvacuationMenuDialog;

/**
 * This class describes a nifty screen for the evacuation simulation.
 */
public class EvacuationNiftyScreen extends NiftyScreen<EvacuationMenuDialog, HelpDialog>
{
    /**
     * The {@link EvacuationNiftyScreen} constructor.
     */
    public EvacuationNiftyScreen()
    {
        super();
    }

    @Override
    protected void createNiftyPanels()
    {
        menuDialog = new EvacuationMenuDialog(menuLayerElement);
        helpDialog = new HelpDialog(helpPanelElement);
    }
}
