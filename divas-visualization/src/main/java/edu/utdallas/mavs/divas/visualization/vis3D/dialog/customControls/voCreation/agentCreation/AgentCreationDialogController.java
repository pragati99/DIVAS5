package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.agentCreation;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.visualization.utils.VisResourcesRepository;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.AbstractCreationDialogController;

/**
 * This class represents the controller for the agent creation dialog Nifty control.
 */
public class AgentCreationDialogController extends AbstractCreationDialogController<AgentState>
{
    @Override
    public void search(String text)
    {
        resetContentPanel();

        if(text.equals("*") || text.isEmpty())
        {
            updateContentPanel(VisResourcesRepository.findAllAgents());
        }
        else
        {
            updateContentPanel(VisResourcesRepository.findAgents(text));
        }
    }

    @Override
    public void createObject(final Object object)
    {
        app.getSimulatingAppState().setAddAgentSelection((AgentState) object);
    }
}
