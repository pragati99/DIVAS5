package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentsPropertyModification;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DefaultController;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyAttributes;

/**
 * The {@link AgentsPropertyModificationDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link AgentsPropertyModificationDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class AgentsPropertyModificationDialogDefinition
{
    /**
     * The name of the control {@link AgentsPropertyModificationDialogDefinition}.
     */
    public static final String    NAME                       = AgentsPropertyModificationDialogDefinition.class.getName();

    /**
     * The id of the panel for the vision algorithm of the agent.
     */
    public static final String    VISION_ALGORITHM_PANEL     = "#visionAlgorithmPanel";

    /**
     * The id for the panel containing the visible distance the agent can perceive.
     */
    public static final String    VISIBLE_DISTANCE_PANEL     = "#visibleDistancePanel";

    /**
     * The id for the panel containing the agent field of vision degree.
     */
    public static final String    FOV_PANEL                  = "#FOVPanel";

    /**
     * The id for the checkbox containing whether the agent cone is on or off.
     */
    public static final String    CONE_CHECKBOX              = "#coneEnabled";

    /**
     * The id for the checkbox containing whether the agent auditory sense is enabled or not.
     */
    public static final String    AUDITORY_ENABLED_CHECKBOX  = "#auditoryEnabled";

    /**
     * The id for the checkbox containing whether the agent olfactory sense is enabled or not.
     */
    public static final String    OLFACTORY_ENABLED_CHECKBOX = "#olfactoryEnabled";

    public static final String    AGENT_RANGE_TEXTFIELD      = "#agentRangeTextfield";

    public static final String    APPLY_ALL_CHECKBOX         = "#applyAllCheckbox";

    /**
     * The id for the agent property button.
     */
    public static final String    AGENT_PROPERTY_BUTTON      = "#agent_property_button";

    /**
     * The label size for the panel title.
     */
    public static String          labelSize                  = "*";

    private static CommonBuilders builders                   = new CommonBuilders();

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
                controller(new DefaultController());
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

                        /*
                         * Agent vision Properties
                         */
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Vision"));
                                        panel(builders.hspacer("10px"));
                                        panel(new PanelBuilder(VISION_ALGORITHM_PANEL)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(VISIBLE_DISTANCE_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                        panel(builders.hspacer("10px"));
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(FOV_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                        panel(builders.hspacer("10px"));
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        panel(builders.hspacer("10px"));
                                        control(builders.createLabel("Vision Cone"));
                                        panel(builders.hspacer("10px"));
                                        control(new CheckboxBuilder(CONE_CHECKBOX));
                                    }
                                });
                            }
                        });
                        panel(builders.vspacer("5px"));

                        /*
                         * Agent auditory property
                         */
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Hearing enabled"));
                                        panel(builders.hspacer("10px"));
                                        control(new CheckboxBuilder(AUDITORY_ENABLED_CHECKBOX));
                                    }
                                });
                            }
                        });
                        panel(builders.vspacer("5px"));

                        /*
                         * Agent olfactory property
                         */
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Smell enabled"));
                                        panel(builders.hspacer("10px"));
                                        control(new CheckboxBuilder(OLFACTORY_ENABLED_CHECKBOX));
                                    }
                                });
                                panel(builders.vspacer());
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

                                control(new TextFieldBuilder(AGENT_RANGE_TEXTFIELD)
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

                                panel(new PanelBuilder()
                                {
                                    {
                                        // width("42px");
                                        childLayoutHorizontal();
                                        control(new CheckboxBuilder(APPLY_ALL_CHECKBOX));
                                        panel(builders.hspacer("5px"));
                                        control(builders.createLabel("All", "20px"));
                                    }
                                });

                                panel(builders.hspacer("5px"));

                                control(new ButtonBuilder(AGENT_PROPERTY_BUTTON, "Apply")
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
