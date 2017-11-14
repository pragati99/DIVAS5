package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voTracking;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyAttributes;

/**
 * The {@link AgentTrackingDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link AgentTrackingDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class AgentTrackingDialogDefinition
{
     /**
     * The name of the control {@link AgentTrackingDialogDefinition}.
     */
    public static final String    NAME                           = AgentTrackingDialogDefinition.class.getName();

    public static final String    AGENT_TRACKING_RANGE_TEXTFIELD = "#agent_tracking_range_textfield";

    public static final String    AGENT_TRACK_BUTTON             = "#agent_track_button";

    public static final String    AGENT_UNTRACK_BUTTON           = "#agent_untrack_button";

    /**
     * The label size for the panel title.
     */
    public static String          labelSize                      = "*";

    private static CommonBuilders builders                       = new CommonBuilders();

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
                controller(new AgentTrackingDialogController());
                panel(new PanelBuilder()
                {
                    {
                        // style("nifty-panel");
                        backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));

                        padding("5px,10px,5px,10px"); // top, right, bottom, left

                        childLayoutVertical();

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                control(builders.createLabel("$title_label", labelSize));
                            }
                        });

                        /**
                         * Apply all button
                         */
                        panel(builders.vspacer("5px"));
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();

                                control(new TextFieldBuilder(AGENT_TRACKING_RANGE_TEXTFIELD)
                                {
                                    {
                                        width("100px");
                                        padding("5px");
                                        alignLeft();
                                        textVAlignCenter();
                                        textHAlignLeft();
                                        text(NiftyAttributes.AGENT_RANGE_EXAMPLE);
                                    }
                                });

                                panel(builders.hspacer("10px"));

                                control(new ButtonBuilder(AGENT_TRACK_BUTTON, "Track")
                                {
                                    {
                                        width("60px");
                                    }
                                });

                                panel(builders.hspacer("10px"));

                                control(new ButtonBuilder(AGENT_UNTRACK_BUTTON, "Untrack")
                                {
                                    {
                                        width("60px");
                                    }
                                });

                            }
                        });
                        panel(builders.vspacer("20px"));
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }
}
