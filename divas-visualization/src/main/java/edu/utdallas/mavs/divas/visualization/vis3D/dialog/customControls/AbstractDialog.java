package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls;

import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyGUIHelper;

/**
 * This class describes an abstract dialog.
 * 
 * @param <C>
 *        Abstract dialog controller
 */
public abstract class AbstractDialog<C extends AbstractDialogController>
{
    protected NiftyScreen<?, ?> niftyScreen = NiftyScreen.getInstance();

    protected Nifty             nifty;

    protected Screen            screen;

    protected String            dialogId;

    protected String            dialogTitle;

    protected Element           content;

    protected Element           parentElement;

    protected AbstractDialog<C> instance;

    protected boolean           enabled     = false;

    /**
     * The {@link AbstractDialog} constructor.
     * 
     * @param parentElement
     *        The parent element for the dialog.
     */
    public AbstractDialog(Element parentElement)
    {
        this.parentElement = parentElement;

        nifty = niftyScreen.nifty;
        screen = niftyScreen.screen;

        instance = this;
    }

    /**
     * Shows an existing Nifty dialog for an object.
     */
    public void showDialog()
    {
        if(content == null)
        {
            createDialog();
        }
        else
        {
            content.show();
        }
        enabled = true;
    }

    /**
     * Hides an existing Nifty dialog for an object.
     */
    public void hideDialog()
    {
        if(content != null)
        {
            content.hide();
        }
        enabled = false;
    }

    /**
     * Creates a Nifty dialog (e.g., a Nifty Window) for an object (e.g., agent, environment object).
     */
    public void createDialog()
    {
        content = createCustomControl();
        content.getControl(getControllerClass()).init();
    }

    private Element createCustomControl()
    {
        registerNiftyDefinition(nifty);

        return NiftyGUIHelper.createCustomControl(getDialogId(), getDefinitionName(), getControllerName(), getAlignment(), getHeight(), getWidth(), parentElement, nifty, screen, getParameters());
    }

    /**
     * Gets the parent of the dialog element.
     * 
     * @return
     *         The dialog parent element.
     */
    public Element getParentElement()
    {
        return parentElement;
    }

    /**
     * Gets the dialog element.
     * 
     * @return
     *         The dialog element.
     */
    public Element getElement()
    {
        return content;
    }

    /**
     * Returns if the dialog is enabled or not.
     * 
     * @return True if the dialog is enabled, and false otherwise.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Gets the dialog element definition name.
     * 
     * @return
     *         Definition name.
     */
    public String getDefinitionName()
    {
        return getDefinitionClass().getName();
    }

    /**
     * Changes the dialog to enabled or disabled.
     * 
     * @param enabled
     *        the new status of the dialog.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Gets the id of the dialog.
     * 
     * @return
     *         Dialog id.
     */
    public abstract String getDialogId();

    /**
     * Gets the width of the dialog.
     * 
     * @return
     *         Dialog width.
     */
    public abstract String getWidth();

    /**
     * Gets the height of the dialog.
     * 
     * @return
     *         Dialog height.
     */
    public abstract String getHeight();

    /**
     * Gets the alignment of the dialog.
     * 
     * @return
     *         Dialog alignment
     */
    public abstract String getAlignment();

    /**
     * Gets parameters for the dialog.
     * 
     * @return a map of key, value pairs of parameters for the dialog.
     */
    public abstract Map<String, String> getParameters();

    /**
     * Registers the dialog element definition.
     * 
     * @param nifty
     *        The Nifty instance.
     */
    public abstract void registerNiftyDefinition(Nifty nifty);

    /**
     * Gets the dialog element definition class.
     * 
     * @return
     *         The dialog definition class.
     */
    public abstract Class<?> getDefinitionClass();

    /**
     * Gets the controller class for the dialog element
     * 
     * @return
     *         The dialog controller class.
     */
    public abstract Class<C> getControllerClass();

    /**
     * Gets the controller name.
     * 
     * @return
     *         The controller name.
     */
    public abstract String getControllerName();
}
