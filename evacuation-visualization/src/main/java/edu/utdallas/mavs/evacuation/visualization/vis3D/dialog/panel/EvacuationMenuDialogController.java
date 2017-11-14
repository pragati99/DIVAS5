package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.panel;

import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.MenuDialogController;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentsPropertyModification.AgentsPropertyModificationDialog;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentsPropertyModification.AgentsPropertyModificationDialogController;

/**
 * This class represents the controller for the Evacuation MenuDialog Nifty control.
 */
public class EvacuationMenuDialogController extends MenuDialogController
{
    @Override
    protected void createAgentContent()
    {
        super.createAgentContent();
        
        // Specific content for the evacuation agents
        AbstractDialog<AgentsPropertyModificationDialogController> agentsPropertyModificationDialog = new AgentsPropertyModificationDialog(agentContentPanel);
        agentsPropertyModificationDialog.createDialog();
        //breakLine(agentContentPanel);
    }
     
    @Override
    protected void createEnvObjectContent()
    {
        super.createEnvObjectContent();
    }
    
    @Override
    protected void createEventContent()
    {
        super.createEventContent();
    }
}
