package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.controls.dynamic.CustomControlCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;
import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyGUIHelper;

/**
 * This class represents the controller for the abstract Nifty control dialog.
 */
public abstract class AbstractDialogController implements Controller
{
    private final static Logger     logger = LoggerFactory.getLogger(AbstractDialogController.class);

    protected Screen                screen;
    protected Nifty                 nifty;

    protected Element               windowElement;
    protected String                windowId;

    /**
     * The JME application used for the 3D visualization of the simulation.
     */
    protected BaseApplication<?, ?> app;

    /**
     * The adapter for communicating with the simulation
     */
    protected SimAdapter            simCommander;

    /**
     * Constructs a new controller for a options dialog
     */
    public AbstractDialogController()
    {
        app = Visualizer3DApplication.getInstance().getApp();
        simCommander = Visualizer3DApplication.getInstance().getSimCommander();
    }

    /**
     * Binds the AbstractOptionsDialogController with the Nifty GUI.
     */
    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, final Properties parameter, final Attributes controlDefinitionAttributes)
    {
        logger.debug("Inside bind in AbstractOptionsDialogController()");

        this.screen = screen;
        this.nifty = nifty;

        this.windowElement = element.getParent();
        this.windowId = windowElement.getId();

        // init();
    }

    /**
     * Initializes local variables
     */
    public void init()
    {
        bindNiftyElements();
        populatePanel();
        subscriptions();
        updatePanel();
    }

    /**
     * Maps Nifty controls that are used in the Nifty Definition
     */
    public abstract void bindNiftyElements();

    /**
     * Populate the controls such as dropdown.
     */
    public abstract void populatePanel();

    /**
     * Adds Nifty subscriptions for Nifty controls.
     */
    public abstract void subscriptions();

    /**
     * Updates the values in the controls.
     */
    public abstract void updatePanel();

    /**
     * Gets a Nifty element with the given name.
     * 
     * @param name
     *        The element name.
     * @return
     *         The Nifty element.
     */
    protected Element getElement(String name)
    {
        return NiftyGUIHelper.getElement(windowElement, name);
    }

    /**
     * Returns a nifty control with the given name.
     * 
     * @param name
     *        The name of the nifty control.
     * @param requestedClass
     *        The class type of the nifty control.
     * @return
     *         The nifty control.
     */
    public <T extends NiftyControl> T getNiftyControl(String name, Class<T> requestedClass)
    {
        return NiftyGUIHelper.getNiftyControl(windowElement, name, requestedClass);
    }

    /**
     * Creates a Nifty custom control.
     * 
     * @param id
     *        The Nifty control id.
     * @param name
     *        The name of the Nifty control.
     * @param controller
     *        The controller for the control.
     * @param alignment
     *        The alignment of the control.
     * @param height
     *        The height of the control.
     * @param width
     *        The width of the control.
     * @param parentElement
     *        The parent element of the control.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @param parameter
     *        A map with key / value pairs with parameters of the element.
     * @return
     *         Returns an element for the newly created custom control.
     */
    public Element createCustomControl(String id, String name, String controller, String alignment, String height, String width, Nifty nifty, Screen screen, Map<String, String> parameter)
    {
        return NiftyGUIHelper.createCustomControl(id, name, controller, alignment, height, width, windowElement, nifty, screen, parameter);
    }

    /**
     * Creates a custom button control. The button contains an image and a label.
     * 
     * @param name
     *        The name of the custom button.
     * @param image
     *        The image path for the custom button.
     * @return
     */
    protected CustomControlCreator createCustomButton(Element parentElement, String name, String image)
    {
        return NiftyGUIHelper.createCustomButtonCreator(name, image, parentElement, nifty, screen);
    }

    /**
     * Creates a spinner control element for the given parent element.
     * 
     * @param id
     *        The spinner id.
     * @param label
     *        The spinner label.
     * @param step
     *        The step for increasing and decreasing the spinner value.
     * @param max
     *        The maximum value for the spinner.
     * @param min
     *        The minimum value for the spinner.
     * @param value
     *        The spinner value.
     * @param unit
     *        The unit for the value of the spinner.
     * @param parentElement
     *        .
     *        The parent element.
     * @param enabled
     *        A flag representing if the spinner is enabled or disabled.
     * @return
     *         The newly created spinner control element.
     */
    public Element createSpinnerControl(String id, String label, float step, float max, float min, float value, String unit, String parentElement, boolean enabled)
    {
        return NiftyGUIHelper.createSpinnerControl(id, label, step, max, min, value, unit, enabled, getElement(parentElement), nifty, screen);
    }

    /**
     * Creates and returns a dropdown Nifty control for the given element.
     * 
     * @param id
     *        The id of the dropdown control.
     * @param parentElement
     *        The parent element for the dropdown control.
     * @return
     *         The newly created dropdown control.
     */

    public DropDown<?> createDropDownControl(String id, String parentElement)
    {
        return NiftyGUIHelper.createDropDownControl(id, getElement(parentElement), nifty, screen);
    }

    /**
     * Populate the given dropdown control with the given enum type.
     * 
     * @param dropDown
     *        The dropdown to be populated.
     * @param enumType
     *        The enum type to populate the dropdown with.
     */
    public <T extends Enum<T>> void populateDropDown(DropDown<T> dropDown, Class<T> enumType)
    {
        NiftyGUIHelper.populateDropDown(dropDown, enumType);
    }

    /**
     * Init the AbstractOptionsDialogController. You can assume that bind() has been called already.
     * 
     * @see de.lessvoid.nifty.controls.Controller#init(java.util.Properties, de.lessvoid.xml.xpp3.Attributes)
     */
    @Override
    public void init(final Properties parameter, final Attributes controlDefinitionAttributes)
    {
        // logger.info("on init");
    }

    /**
     * Called when the screen is started. You can assume that bind() and init() have been called already.
     * 
     * @see de.lessvoid.nifty.controls.Controller#onStartScreen()
     */
    @Override
    public void onStartScreen()
    {
        // logger.info("onStartScreen");
    }

    /**
     * Gets the focus or looses the focus of this controller.
     * 
     * @see de.lessvoid.nifty.controls.Controller#onFocus(boolean)
     */
    @Override
    public void onFocus(final boolean getFocus)
    {
        // logger.info("onFocus");
    }

    /**
     * Processes the NiftyInputEvent.
     * 
     * @see de.lessvoid.nifty.controls.Controller#inputEvent(de.lessvoid.nifty.input.NiftyInputEvent)
     */
    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent)
    {
        return false;
    }

    // Helper method to get text field id for the given spinner control
    protected String getElementId(Element spinner)
    {
        return spinner.getControl(SpinnerController.class).getTextFieldId();
    }

    protected String getSpinnerText(Element spinner)
    {
        return spinner.getControl(SpinnerController.class).getSpinnerTextField().getText();
    }
}
