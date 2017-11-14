package edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.button;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DefaultController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link CustomButtonDefinition} registers a new control with Nifty that
 * represents the whole {@link CustomButtonDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class CustomButtonDefinition
{
    /**
     * The name of the control {@link CustomButtonDefinition}.
     */
    public static final String    NAME      = "#custom_button";

    public static final String    BUTTON    = "#custom_button";

    public static final String    LABEL     = "#button_label";

    public static String          labelSize = "*";                 // 80px";

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
                controller(new DefaultController()); // CustomButtonController()); *******

                visibleToMouse();

                panel(new PanelBuilder(BUTTON)
                {
                    {
                        childLayoutVertical();

                        width("*");
                        height("27px");

                        visibleToMouse();

                        valignCenter();
                        alignCenter();

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();

                                visibleToMouse();

                                image(new ImageBuilder()
                                {
                                    {
                                        childLayoutVertical();
                                        width("24px");
                                        height("24px");
                                        valignCenter();
                                        filename("$image_filename");

                                        interactOnClick("$clickMethod");
                                    }
                                });

                                panel(builders.hspacer("10px"));

                                textHAlignCenter();

                                control(builders.createLabel("$button_label", labelSize));

                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }
}
