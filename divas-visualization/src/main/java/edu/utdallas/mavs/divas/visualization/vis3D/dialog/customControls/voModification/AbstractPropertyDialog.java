package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification;

import java.util.Observable;
import java.util.Observer;

import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyGUIHelper;

/**
 * This class describes an abstract property dialog.
 * 
 * @param <E>
 *        The type of the entity this dialog belongs to.
 * @param <C>
 */
public abstract class AbstractPropertyDialog<E, C extends AbstractPropertyDialogController<E>> extends AbstractDialog<C> implements Observer
{
    protected E entity;

    /**
     * The {@link AbstractPropertyDialog} constructor.
     * 
     * @param parentElement
     *        The parent element for the dialog.
     */
    public AbstractPropertyDialog(Element parentElement)
    {
        super(parentElement);
    }

    /**
     * Shows or hides the Nifty Window for the given entity (e.g., agent, env
     * object). This method is called when an entity gets selected or unselected
     * in the simulation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object object)
    {
        entity = (E) object;

        if(isContextSelected(entity))
        {
            niftyScreen.removeAllContextWindows();

            dialogId = createDialogId(getEntityId(entity));
            dialogTitle = createDialogTitle(getEntityId(entity));

            showDialog();
        }
        else
        {
            removeDialog();
        }
    }

    @Override
    public void showDialog()
    {
        if(niftyScreen.contains(dialogId))
        {
            content.show();
        }
        else
        {
            createDialog();
        }
        enabled = true;
    }

    /**
     * Removes the content for the dialog.
     */
    public void removeDialog()
    {
        if(content != null)
        {
            content.hide();
            content.markForRemoval();
            content = null;
        }
        niftyScreen.removeContextWindow(dialogId);
        enabled = false;
    }

    @Override
    public void createDialog()
    {
        Window window = NiftyGUIHelper.createWindow(dialogId, dialogTitle, getPositionX(), getPositionY(), getHeight(), getWidth(), getParentElement(), nifty, screen);

        content = createCustomControl(window.getElement());
        content.getControl(getControllerClass()).setEntity(entity);
        content.getControl(getControllerClass()).init();
        content = window.getElement();

        // Add the window to the list of context windows.
        niftyScreen.addContextWindow(window.getId(), this);
    }

    private Element createCustomControl(Element element)
    {
        registerNiftyDefinition(nifty);

        return NiftyGUIHelper.createCustomControl(dialogId, getDefinitionName(), getControllerName(), getAlignment(), getContentHeight(), getContentWidth(), element, nifty, screen, getParameters());
    }

    @Override
    public String getDialogId()
    {
        return dialogId;
    }

    /**
     * Returns true if the entity is selected and false otherwise.
     * 
     * @param object
     *        The entity for which this dialog belongs to.
     * @return A flag indicating whether the object is selected or unselected.
     */
    public abstract boolean isContextSelected(Object object);

    /**
     * @return the property dialog x position.
     */
    public abstract String getPositionX();

    /**
     * @return the property dialog y position.
     */
    public abstract String getPositionY();

    /**
     * @return the window content height.
     */
    public abstract String getContentHeight();

    /**
     * @return the window content width.
     */
    public abstract String getContentWidth();

    /**
     * Updates an existing Nifty dialog for an object.
     */
    public abstract void updateDialog();

    protected abstract String getEntityId(Object object);

    protected abstract String createDialogId(String id);

    protected abstract String createDialogTitle(String id);
}
