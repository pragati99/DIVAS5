package edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link SpinnerDefinition} registers a new control with Nifty that
 * represents the whole {@link SpinnerDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
public class SpinnerDefinition
{
    /**
     * The label size for the spinner. The default is 80px.
     */
    public static String          labelSize = "80px";

    /**
     * The name of the control {@link SpinnerDefinition}.
     */
    public static final String    NAME      = "spinner";

    private static CommonBuilders builders  = new CommonBuilders();

    /**
     * This registers the dialog as a new ControlDefintion with Nifty so that we can
     * later create the dialog dynamically.
     * 
     * @param nifty
     *        The Nifty instance
     */
    public static void register(final Nifty nifty)
    {
        new ControlDefinitionBuilder(NAME)
        {
            {
                controller(new SpinnerController());
                // height("95%");
                panel(new PanelBuilder("$id")
                {
                    {
                        childLayoutHorizontal();
                        control(builders.createLabel("$label", labelSize));
                        control(new TextFieldBuilder("#textfield")
                        {
                            {
                                width("70px");
                                alignLeft();
                                textVAlignCenter();
                                textHAlignLeft();
                            }
                        });
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                valignCenter();
                                width("27px");
                                image(new ImageBuilder("#up")
                                {
                                    {
                                        filename("images/arrowup.png");
                                        interactOnClick("incrementValue()");
                                    }
                                });
                                // panel(builders.vspacer(".5px"));
                                image(new ImageBuilder("#down")
                                {
                                    {
                                        filename("images/arrowdown.png");
                                        interactOnClick("decrementValue()");
                                    }
                                });
                            }
                        });
                        control(builders.createLabel("$units"));
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }
}
