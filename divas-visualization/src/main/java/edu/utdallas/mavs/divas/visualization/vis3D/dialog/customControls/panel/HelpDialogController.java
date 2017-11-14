package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * This class represents the controller for the HelpDialog Nifty control.
 */
public class HelpDialogController extends AbstractDialogController
{
    private Element               rightPanelElement;
    private Element               helpPanelElement;
    private Element               headingElement;

    private static CommonBuilders builders = new CommonBuilders();

//    @Override
//    public void onStartScreen()
//    {}

    @Override
    public void bindNiftyElements()
    {
        headingElement = getElement(HelpDialogDefinition.HELP_CONTENT_HEADING);
        helpPanelElement = getElement(HelpDialogDefinition.HELP_CONTENT_LEFT);
        rightPanelElement = getElement(HelpDialogDefinition.HELP_CONTENT_RIGHT);
    }
    
    @Override
    public void populatePanel()
    {
        breakLine();
        h1("DIVAs Keyboard Shortcuts", headingElement);
        breakLine();
        h2("Actions");
        pair("F1:", "Opens help panel");
        pair("F5:", "Shows visualizer statistics on screen");
        pair("F6:", "Toggles Debug Mode ON/OFF");
        pair("F10:", "Opens visualizer control panel");
        pair("F11:", "Toggles visualization ON/OFF");
        pair("F12:", "Toggles visualization of the simulation ON/OFF");
        pair("<Esc>:", "Removes selections, returns to selection mode");
        pair("mouse wheel:", "selects operation mode");
        pair("left click:", "selects an agent or object");
        pair("right click:", "if agent or object selected, shows contextual menu");
        breakLine();
        h2("Editing");
        pair("F2 then drag mouse:", "If “drag and drop” ON, scale along x axis");
        pair("F3 then drag mouse:", " If “drag and drop” ON, scale along y axi");
        pair("F4 then drag mouse:", "If “drag and drop” ON, scale along z axis");
        pair("F9:", "Turns “drag and drop” ON/OFF");
        pair("<Ctrl> then drag mouse:", "If “drag and drop” ON, copy the selected object");
        pair("<Del>:", "Delete selected objects and agents");
        nextCol();
        /*h2("Camera Navigation");
        pair("right click then drag mouse:", "Rotate camera");
        pair("q:", "[description]");
        pair("w:", "[description]");
        pair("e:", "[description]");*/
    }

    private void nextCol()
    {
        helpPanelElement = rightPanelElement;
    }

    @Override
    public void subscriptions()
    {

    }

    @Override
    public void updatePanel()
    {}

    private PanelBuilder getHorizontalPanel()
    {
        PanelBuilder panel = new PanelBuilder();
        panel.childLayoutHorizontal();
        return panel;
    }

    private void pair(final String key, final String value)
    {
        Element panelElement = getHorizontalPanel().build(nifty, screen, helpPanelElement);
        h3Bold(key, panelElement, "150px");
        space(panelElement);
        h3(value, panelElement);
    }

    private void h1(final String text, Element parent)
    {
        TextBuilder label = new TextBuilder();
        label.text(text);
        label.font("fonts/caption.fnt");
        label.color("#FFCC33");
        label.set("textLineHeight", "30px");
        label.set("textMinHeight", "30px");
        label.build(nifty, screen, parent);
    }

    private void h2(String text)
    {
        Element panelElement = getHorizontalPanel().build(nifty, screen, helpPanelElement);

        h3Bold("", panelElement, "150px");
        space(panelElement);
        TextBuilder label = new TextBuilder();
        label.font("fonts/aurulent-sans-16-bold.fnt");
        label.color("#FFCC33");
        label.text(text);
        label.set("textLineHeight", "25px");
        label.set("textMinHeight", "25px");
        label.build(nifty, screen, panelElement);
    }

    private void h3(final String text, final Element parentElement)
    {
        TextBuilder label = new TextBuilder();
        label.font("fonts/aurulent-sans-16.fnt");
        label.color("#FFFFF");
        label.text(text);
        label.set("textLineHeight", "25px");
        label.set("textMinHeight", "25px");
        label.build(nifty, screen, parentElement);
    }

    private void h3Bold(final String text, final Element parentElement, String width)
    {
        TextBuilder label = new TextBuilder();
        label.font("fonts/aurulent-sans-16-bold.fnt");
        label.color("#FFCC33");
        label.text(text);
        label.width(width);
        label.textHAlignRight();
        label.set("textLineHeight", "25px");
        label.set("textMinHeight", "25px");
        label.build(nifty, screen, parentElement);
    }

    private void breakLine()
    {
        (builders.vspacer("10px")).build(nifty, screen, helpPanelElement);
    }

    private void space(Element parentElement)
    {
        (builders.hspacer("10px")).build(nifty, screen, parentElement);
    }
}
