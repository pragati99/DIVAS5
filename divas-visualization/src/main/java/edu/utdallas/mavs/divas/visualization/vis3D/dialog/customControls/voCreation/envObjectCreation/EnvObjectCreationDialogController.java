package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.envObjectCreation;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.utils.VisResourcesRepository;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.AbstractCreationDialogController;

/**
 * This class represents the controller for the environment object creation dialog Nifty control.
 */
public class EnvObjectCreationDialogController extends AbstractCreationDialogController<EnvObjectState>
{

    @Override
    public void search(String text)
    {
        resetContentPanel();

        if(text.isEmpty() || text.equals("*"))
        {
            updateContentPanel(VisResourcesRepository.findAllEnvObjects());
        }
        else
        {
            updateContentPanel(VisResourcesRepository.findEnvObjects(text));
        }
    }

    public void createObject(final Object object)
    {        
        app.getSimulatingAppState().setAddEnvObjSelection((EnvObjectState) object);
    }
}
