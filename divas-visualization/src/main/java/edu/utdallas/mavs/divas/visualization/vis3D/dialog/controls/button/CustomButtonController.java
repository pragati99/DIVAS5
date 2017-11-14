package edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;

@SuppressWarnings("javadoc")
public class CustomButtonController extends AbstractDialogController
{
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CustomButtonController.class);

    private Element       button;

    @Override
    public void init()
    {}

    @Override
    public void bindNiftyElements()
    {
        button = getElement(CustomButtonDefinition.BUTTON);
    }

    @Override
    public void populatePanel()
    {}

    @Override
    public void subscriptions()
    {
    }

    @Override
    public void updatePanel()
    {

    }

    public Element getElement()
    {
        return button;
    }

    public void setElement(Element element)
    {
        this.button = element;
    }
}
