package edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.dropdown.builder.CreateDropDownControl;
import de.lessvoid.nifty.controls.dynamic.CustomControlCreator;
import de.lessvoid.nifty.controls.window.builder.CreateWindow;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.button.CustomButtonController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.button.CustomButtonDefinition;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerDefinition;

/**
 * Helper class for Nifty GUI.
 */
public class NiftyGUIHelper
{
    /**
     * Creates a Nifty custom control.
     * 
     * @param id
     *        The Nifty control id.
     * @param name
     *        The name of the Nifty control.
     * @param controller
     *        The class name of the controller for the nifty control.
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
    public static Element createCustomControl(String id,
            String name,
            String controller,
            String alignment,
            String height,
            String width,
            Element parentElement,
            Nifty nifty,
            Screen screen,
            Map<String, String> parameter)
    {
        CustomControlCreator customControl = createCustomControlCreator(id, name, controller, alignment, height, width, parentElement, nifty, screen, parameter);

        return customControl.create(nifty, screen, parentElement);
    }

    /**
     * Creates a Nifty custom control.
     * 
     * @param id
     *        The nifty control id.
     * @param name
     *        The name of the Nifty control.
     * @param controller
     *        The class name of the controller for the nifty control.
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
    public static CustomControlCreator createCustomControlCreator(String id,
            String name,
            String controller,
            String alignment,
            String height,
            String width,
            Element parentElement,
            Nifty nifty,
            Screen screen,
            Map<String, String> parameter)
    {

        id = String.format("#%s#%s", parentElement.getId(), id);

        CustomControlCreator customControl = new CustomControlCreator(String.format("#%s", id), name);

        customControl.setController(controller);
        customControl.setAlign(alignment);
        customControl.setHeight(height);
        customControl.setWidth(width);

        for(String key : parameter.keySet())
        {
            customControl.set(key, parameter.get(key));
        }

        return customControl;
    }

    /**
     * Creates and returns a dropdown Nifty control for the given element.
     * 
     * @param id
     *        The id of the dropdown control.
     * @param parentElement
     *        The parent element for the dropdown control.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         The newly created dropdown control.
     */

    public static DropDown<?> createDropDownControl(String id, Element parentElement, Nifty nifty, Screen screen)
    {
        id = String.format("#%s#%s", parentElement.getId(), id);
        
        CreateDropDownControl dropDownControl = new CreateDropDownControl(id);
        dropDownControl.setFocusable("true");

        return dropDownControl.create(nifty, screen, parentElement);
    }

    /**
     * Populate the given dropdown control with the given enum type.
     * 
     * @param dropDown
     *        The dropdown to be populated.
     * @param enumType
     *        The enum type to populate the dropdown with.
     */
    public static <T extends Enum<T>> void populateDropDown(DropDown<T> dropDown, Class<T> enumType)
    {
        if(dropDown != null)
        {
            for(T enumeration : enumType.getEnumConstants())
            {
                dropDown.addItem(enumeration);
            }
        }
    }

    /**
     * Creates a custom button element.
     * 
     * @param id
     *        The custom button element id.
     * @param label
     *        The custom button label.
     * @param imagePath
     *        The image path
     * @param parentElement
     *        The parent element for the custom button element.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         The custom button element.
     */
    public static Element createCustomButtonElement(String label, String imagePath, Element parentElement, Nifty nifty, Screen screen)
    {
        CustomControlCreator customButtonCreator = createCustomButtonCreator(label, imagePath, parentElement, nifty, screen);

        return customButtonCreator.create(nifty, screen, parentElement);
    }

    /**
     * Creates a custom button control. The button contains an image and a label.
     * 
     * @param id
     *        The custom button element id.
     * @param label
     *        The custom button label.
     * @param imagePath
     *        The image path
     * @param parentElement
     *        The parent element for the custom button element.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         A Nifty custom control creator for the custom button control.
     */
    public static CustomControlCreator createCustomButtonCreator(String label, String imagePath, Element parentElement, Nifty nifty, Screen screen)
    {
        CustomButtonDefinition.register(nifty);
        
        String id = String.format("#customButton#%s", imagePath);

        String name = CustomButtonDefinition.NAME;
        String controller = (new CustomButtonController()).getClass().getName();

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put("button_label", label);
        parameter.put("image_filename", imagePath);

        return createCustomControlCreator(id, name, controller, "center", "100%", "100%", parentElement, nifty, screen, parameter);
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
     * @param enabled
     *        A flag representing if the spinner is enabled or disabled.
     * @param parentElement
     *        The parent element of the spinner.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         The newly created spinner control element.
     */
    public static Element createSpinnerControl(String id, String label, float step, float max, float min, float value, String unit, boolean enabled, Element parentElement, Nifty nifty, Screen screen)
    {
        SpinnerDefinition.register(nifty);

        id = String.format("#%s#%sSpinner", parentElement.getId(), id);

        /*
         * String name = SpinnerDefinition.NAME;
         * String controller = (new SpinnerController()).getClass().getName();
         * Map<String, String> parameter = new HashMap<String, String>();
         * parameter.put("label", label);
         * parameter.put("unit", unit);
         * parameter.put("id", id);
         */

        CustomControlCreator spinner = new CustomControlCreator(id, SpinnerDefinition.NAME);
        spinner.set("id", id);
        spinner.set("label", label);
        spinner.set("unit", unit);
        Element el = spinner.create(nifty, screen, parentElement);

        // Element el = createCustomControl(name, controller, "center", "*", "*", parentElement, nifty, screen, parameter);

        el.getControl(SpinnerController.class).setStep(step);
        el.getControl(SpinnerController.class).setMax(max);
        el.getControl(SpinnerController.class).setMin(min);
        el.getControl(SpinnerController.class).setCurrentValue(value);
        el.getControl(SpinnerController.class).setEnabled(enabled);

        return el;
    }

    /**
     * Creates and return a Nifty Window.
     * 
     * @param id
     *        The window id.
     * @param title
     *        The window title.
     * @param positionX
     *        The window x position.
     * @param positionY
     *        The window y position.
     * @param height
     *        The window height.
     * @param width
     *        The window width.
     * @param parentElement
     *        The parent element of the window.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         The newly created Nifty Window.
     */
    public static Window createWindow(final String id,
            final String title,
            final String positionX,
            final String positionY,
            final String height,
            final String width,
            final Element parentElement,
            final Nifty nifty,
            final Screen screen)
    {
        // Creates a nifty window element
        CreateWindow windowAttributes = new CreateWindow(id, title);
        windowAttributes.setX(String.valueOf(positionX));
        windowAttributes.setY(String.valueOf(positionY));
        windowAttributes.set("hideOnClose", "false");

        // Adds a window to the screen
        Window window = windowAttributes.create(nifty, screen, parentElement);
        window.setHeight(new SizeValue(height));
        window.setWidth(new SizeValue(width));
        window.setFocusable(true);

        return window;
    }

    /**
     * Gets a nifty control with the given name and requested class for the given nifty element.
     * 
     * @param parentElement
     *        The parent element of the nifty control.
     * @param name
     *        The control name.
     * @param requestedClass
     *        The control type.
     * @return
     *         The nifty control.
     */
    public static <T extends NiftyControl> T getNiftyControl(Element parentElement, String name, Class<T> requestedClass)
    {
        return parentElement.findNiftyControl(name, requestedClass);
    }

    /**
     * Returns a Nifty element with the given name for the given parent element.
     * 
     * @param parentElement
     *        The parent element.
     * @param name
     *        The name of the element to be returned.
     * @return
     *         The element.
     */
    public static Element getElement(Element parentElement, String name)
    {
        return parentElement.findElementByName(name);
    }

    /**
     * Creates and returns a spinner element for the given element and id
     * 
     * @param id
     * @param label
     * @param step
     * @param max
     * @param min
     * @param value
     * @param unit
     * @param enabled
     * @param parentElement
     * @param nifty
     * @param screen
     * @return
     */
    /*
     * protected Element createSpinnerControl(String id, String label, float step, float max, float min, float value, String unit, boolean enabled, Element parentElement, Nifty nifty, Screen screen)
     * {
     * id = String.format("#%s#%sSpinner", windowId, id);
     * CustomControlCreator spinner = new CustomControlCreator(id, SpinnerDefinition.NAME);
     * spinner.set("id", id);
     * spinner.set("label", label);
     * spinner.set("unit", unit);
     * Element el = spinner.create(nifty, screen, parentElement);
     * el.getControl(SpinnerController.class).setStep(step);
     * el.getControl(SpinnerController.class).setMax(max);
     * el.getControl(SpinnerController.class).setMin(min);
     * el.getControl(SpinnerController.class).setCurrentValue(value);
     * el.getControl(SpinnerController.class).setEnabled(enabled);
     * return el;
     * }
     */

    /**
     * Creates a custom button element.
     * 
     * @param id
     *        The custom button element id.
     * @param label
     *        The custom button label.
     * @param imagePath
     *        The image path
     * @param parentElement
     *        The parent element for the custom button element.
     * @param nifty
     *        The nifty instance.
     * @param screen
     *        The nifty screen.
     * @return
     *         The custom button element.
     */
    /*
     * public static Element createCustomButtonElement(String id, String label, String imagePath, Element parentElement, Nifty nifty, Screen screen)
     * {
     * CustomControlCreator button = createCustomButton(label, imagePath, nifty);
     * return button.create(nifty, screen, parentElement);
     * }
     */

    /**
     * Creates a custom button control. The button contains an image and a label.
     * 
     * @param label
     *        The custom button label.
     * @param imagePath
     *        The image path.
     * @param nifty
     *        The nifty instance.
     * @return a Nifty custom control creator for the custom button control.
     */
    /*
     * public static CustomControlCreator createCustomButton(String label, String imagePath, Nifty nifty)
     * {
     * CustomButtonDefinition.register(nifty);
     * CustomControlCreator button = new CustomControlCreator(CustomButtonDefinition.NAME);
     * button.setController((new CustomButtonController()).getClass().getName());
     * button.set("button_label", label);
     * button.set("image_filename", imagePath);
     * return button;
     * }
     */
}
