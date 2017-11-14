package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.eventCreation;

import edu.utdallas.mavs.divas.visualization.utils.EventLoader.Event;
import edu.utdallas.mavs.divas.visualization.utils.VisResourcesRepository;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.AbstractCreationDialogController;

/**
 * This class represents the controller for the event creation dialog Nifty control.
 */
public class EventCreationDialogController extends AbstractCreationDialogController<Event>
{
    @Override
    public void search(String text)
    {
        resetContentPanel();

        if(text.equals("*") || text.isEmpty())
        {
            updateContentPanel(VisResourcesRepository.findAllEvents());
        }
        else
        {
            updateContentPanel(VisResourcesRepository.findEvents(text));
        }
    }

    @Override
    public void createObject(final Object object)
    {
        app.getSimulatingAppState().setTriggerEventSelection(((Event) object).getInputMode());
    } 
}
