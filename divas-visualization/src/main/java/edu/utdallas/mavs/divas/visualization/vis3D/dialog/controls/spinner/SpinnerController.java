package edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;

/**
 * The SpinnerController implements the Controller interface.
 * <p>
 * The SpinnerController contains all the events that the Spinner element generates.
 */
public class SpinnerController implements Controller
{
    private static Logger logger = LoggerFactory.getLogger(SpinnerController.class);

    private Element       element;

    private String        id;
    private String        label;
    private float         step;
    private float         max;
    private float         min;
    private String        unit;
    private boolean       enabled;

    private TextField     textFieldValue;
    private String        textFieldValueId;

    /**
     * Creates a new Spinner Controller
     */
    public SpinnerController()
    {}

    /**
     * Binds the SpinnerController to the Spinner element. Nifty calls this
     * method when it initializes the element.
     * 
     * @see de.lessvoid.nifty.controls.Controller#bind(de.lessvoid.nifty.Nifty, de.lessvoid.nifty.screen.Screen, de.lessvoid.nifty.elements.Element, java.util.Properties,
     *      de.lessvoid.xml.xpp3.Attributes)
     */
    @Override
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes)
    {
        this.element = element;
        this.id = element.getId();

        textFieldValue = element.findNiftyControl(String.format("%s#textfield", element.getId()), TextField.class);

        if(textFieldValue != null)
        {
            textFieldValueId = textFieldValue.getId();
        }
        else
        {
            logger.error("A problem occurred finding the spinner nifty control.");
        }
    }

    /**
     * Init the SpinnerController. You can assume that bind() has been called
     * already.
     * 
     * @see de.lessvoid.nifty.controls.Controller#init(java.util.Properties, de.lessvoid.xml.xpp3.Attributes)
     */
    @Override
    public void init(Properties arg0, Attributes arg1)
    {}

    /**
     * Called when the screen is started. You can assume that bind() and init()
     * have been called already.
     * 
     * @see de.lessvoid.nifty.controls.Controller#onStartScreen()
     */
    @Override
    public void onStartScreen()
    {}

    /**
     * Processes the NiftyInputEvent.
     * 
     * @see de.lessvoid.nifty.controls.Controller#inputEvent(de.lessvoid.nifty.input.NiftyInputEvent)
     */
    @Override
    public boolean inputEvent(NiftyInputEvent arg0)
    {
        return false;
    }

    /**
     * Gets the focus or looses the focus of this controller.
     * 
     * @see de.lessvoid.nifty.controls.Controller#onFocus(boolean)
     */
    @Override
    public void onFocus(boolean arg0)
    {}

    /**
     * Decrements the spinner size by the step size.
     */
    public void decrementValue()
    {
        Float newValue = getDecrementedValue();
        setTextField(newValue);
        logger.debug("Value decreased to: {} WindowId: {} Element: ", new Object[] { newValue, element.getId(), textFieldValue.getId() });
    }

    /**
     * Increment the spinner size by the step size.
     */
    public void incrementValue()
    {
        Float newValue = getIncrementedValue();
        setTextField(newValue);
        logger.debug("Value increased to: {} WindowId: {} Element: ", new Object[] { newValue, id, textFieldValue.getId() });
    }

    private Float getIncrementedValue()
    {
        return ((getCurrentValue() + step) > max) ? max : getCurrentValue() + step;
    }

    private Float getDecrementedValue()
    {
        return ((getCurrentValue() - step) < min) ? min : getCurrentValue() - step;
    }

    /**
     * Gets the spinner control element
     * 
     * @return the spinner element
     */
    public Element getElement()
    {
        return element;
    }

    /**
     * Changes the spinner element with the given element.
     * 
     * @param element
     *        the new element
     */
    public void setElement(Element element)
    {
        this.element = element;
    }

    /**
     * Gets the text field nifty control of the spinner.
     * 
     * @return the spinner textfield.
     */
    public TextField getSpinnerTextField()
    {
        return textFieldValue;
    }

    /**
     * Changes the spinner text field
     * 
     * @param value
     *        the new text field value
     */
    public void setTextField(Float value)
    {
        if(textFieldValue != null)
        {
            textFieldValue.setText(String.format("%s", value));
        }
        else
        {
            logger.error("A problem occurred when updating the spinner text field");
        }
    }

    /**
     * Gets the spinner id
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Changes the spinner id with the given id.
     * 
     * @param id
     *        the new id.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the spinner text label.
     * 
     * @return the text label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Changes the spinner label with the given label.
     * 
     * @param label
     *        the new text label.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Gets the spinner value unit
     * 
     * @return the value unit
     */
    public String getUnit()
    {
        return unit;
    }

    /**
     * Changes the spinner unit
     * 
     * @param unit
     *        the new unit
     */
    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    /**
     * Gets the increment and decrement step size of the spinner.
     * 
     * @return the spinner step value.
     */
    public float getStep()
    {
        return step;
    }

    /**
     * Changes the spinner step size used to increase and decrease values in the textfield.
     * 
     * @param step
     *        the new step size
     */
    public void setStep(float step)
    {
        this.step = step;
    }

    /**
     * Gets the max value of the spinner value.
     * 
     * @return the max value
     */
    public float getMax()
    {
        return max;
    }

    /**
     * Changes the max value of the value in the textfield.
     * 
     * @param max
     *        the new max size
     */
    public void setMax(float max)
    {
        this.max = max;
    }

    /**
     * Gets the minimum value of the spinner value.
     * 
     * @return the min value.
     */
    public float getMin()
    {
        return min;
    }

    /**
     * Changes the minimum value of the value in the textfield.
     * 
     * @param min
     *        the new minimum value
     */
    public void setMin(float min)
    {
        this.min = min;
    }

    /**
     * Gets the current value of the spinner.
     * 
     * @return the current value.
     */
    public float getCurrentValue()
    {
        return Float.valueOf(getSpinnerTextField().getText());
    }

    /**
     * Changes the current value in the spinner textfield.
     * 
     * @param currentValue
     *        the new current value.
     */
    public void setCurrentValue(float currentValue)
    {
        setTextField(currentValue);
    }

    /**
     * @return true if the spinner is enabled and false otherwise.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Changes the spinner element to enabled or disabled.
     * 
     * @param enabled
     *        True sets the spinner enabled, false otherwise.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        if(enabled)
            this.element.enable();
        else
            this.element.disable();
    }

    /**
     * Gets the id of the spinner text field.
     * 
     * @return the text field id.
     */
    public String getTextFieldId()
    {
        return textFieldValueId;
    }

    /**
     * Changes the spinner textfield id with the given id.
     * 
     * @param textFieldId
     *        the new textfield id
     */
    public void setTextFieldId(String textFieldId)
    {
        this.textFieldValueId = textFieldId;
    }
}
