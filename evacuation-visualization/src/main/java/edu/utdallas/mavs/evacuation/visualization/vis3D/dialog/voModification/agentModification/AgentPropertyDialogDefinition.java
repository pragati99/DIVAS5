package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentModification;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DefaultController;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link AgentPropertyDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link AgentPropertyDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
public class AgentPropertyDialogDefinition
{
    /**
     * The name of the control {@link AgentPropertyDialogDefinition}.
     */
    public static final String    NAME                            = AgentPropertyDialogDefinition.class.getName();

    /**
     * The id for the label containing the heading of the agent.
     */
    public static final String    HEADING_LABEL                   = "#heading";

    /**
     * The id for the label containing the acceleration of the agent.
     */
    public static final String    ACCELERATION_LABEL              = "#acceleration";

    /**
     * The id for the label containing the velocity of the agent.
     */
    public static final String    VELOCITY_LABEL                  = "#velocity";

    /**
     * The id for the label containing the position of the agent.
     */
    public static final String    POSITION_LABEL                  = "#position";

    /**
     * The id of the panel for the posture of the agent.
     */
    public static final String    POSTURE_PANEL                   = "#posturePanel";

    /**
     * The id of the panel for the agent control (i.e. autonomous, keyboard) of the agent.
     */
    public static final String    AGENT_CONTROL_PANEL             = "#agentControlPanel";

    /**
     * The id of the panel for the vision algorithm of the agent.
     */
    public static final String    VISION_ALGORITHM_PANEL          = "#visionAlgorithmPanel";

    /**
     * The id for the checkbox containing whether the agent auditory sense is enabled or not.
     */
    public static final String    AUDITORY_ENABLED_CHECKBOX       = "#auditoryEnabled";

    /**
     * The id for the checkbox containing whether the agent olfactory sense is enabled or not.
     */
    public static final String    OLFACTORY_ENABLED_CHECKBOX      = "#olfactoryEnabled";

    /**
     * The id for the checkbox containing whether the agent camera is enabled or not.
     */
    public static final String    AGENT_CAM_CHECKBOX              = "#agentCamEnabled";

    /**
     * The id for the checkbox containing whether the agent cone is on or off.
     */
    public static final String    CONE_CHECKBOX                   = "#coneEnabled";

    /**
     * The id for the textfield containing the agent degree of smell sensitivity.
     */
    public static final String    SMELL_SENSITIVITY_TEXTFIELD     = "#smellSensitivity";

    /**
     * The id for the panel containing the agent degree of smell sensitivity.
     */
    public static final String    SMELL_SENSITIVITY_PANEL         = "#smellSensitivityPanel";

    /**
     * The id for the textfield containing the minimum audible threshold of the agent.
     */
    public static final String    MIN_AUDIBLE_THRESHOLD_TEXTFIELD = "#minAudibleThreshold";

    /**
     * The id for the panel containing the minimum audible threshold of the agent.
     */
    public static final String    MIN_AUDIBLE_THRESHOLD_PANEL     = "#minAudibleThresholdPanel";

    /**
     * The id for the textfield containing the agent field of vision degree.
     */
    public static final String    FOV_TEXTFIELD                   = "#FOV";

    /**
     * The id for the panel containing the agent field of vision degree.
     */
    public static final String    FOV_PANEL                       = "#FOVPanel";

    /**
     * The id for the textfield containing the visible distance the agent can perceive.
     */
    public static final String    VISIBLE_DISTANCE_TEXTFIELD      = "#visibleDistance";

    /**
     * The id for the panel containing the visible distance the agent can perceive.
     */
    public static final String    VISIBLE_DISTANCE_PANEL          = "#visibleDistancePanel";

    /**
     * The id for the textfield containing the maximum speed of the agent.
     */
    public static final String    MAX_SPEED_TEXTFIELD             = "#maxSpeed";

    /**
     * The id for the panel containing the maximum speed of the agent.
     */
    public static final String    MAX_SPEED_PANEL                 = "#maxSpeedPanel";

    /**
     * The id for the textfield containing the desired speed of the agent.
     */
    public static final String    DESIRED_SPEED_TEXTFIELD         = "#desiredSpeed";

    /**
     * The id for the panel containing the desired speed of the agent.
     */
    public static final String    DESIRED_SPEED_PANEL             = "#desiredSpeedPanel";

    private static CommonBuilders builders                        = new CommonBuilders();

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
                controller(new DefaultController()); // AgentOptionsDialogController());
                panel(new PanelBuilder()
                {
                    {
                        // style("nifty-panel");
                        padding("5px,20px,0px,19px"); // top, right, bottom, left
                        backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));
                        // height("590px");
                        width("100%");
                        childLayoutVertical();

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();

                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Position: ", "80px"));
                                        panel(builders.hspacer("0px"));
                                        control(new LabelBuilder(POSITION_LABEL)
                                        {
                                            {
                                                width("*");
                                                alignLeft();
                                                textVAlignCenter();
                                                textHAlignLeft();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Velocity: ", "80px"));
                                        panel(builders.hspacer("0px"));
                                        control(new LabelBuilder(VELOCITY_LABEL)
                                        {
                                            {
                                                width("*");
                                                alignLeft();
                                                textVAlignCenter();
                                                textHAlignLeft();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Acceleration: ", "80px"));
                                        panel(builders.hspacer("0px"));
                                        control(new LabelBuilder(ACCELERATION_LABEL)
                                        {
                                            {
                                                width("*");
                                                alignLeft();
                                                textVAlignCenter();
                                                textHAlignLeft();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Heading: ", "80px"));
                                        panel(builders.hspacer("0px"));
                                        control(new LabelBuilder(HEADING_LABEL)
                                        {
                                            {
                                                width("*");
                                                alignLeft();
                                                textVAlignCenter();
                                                textHAlignLeft();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(POSTURE_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(DESIRED_SPEED_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();

                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(MAX_SPEED_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                    }
                                });
                            }
                        });

                        panel(builders.vspacer("5px"));

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder(AGENT_CONTROL_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                    }
                                });
                            }
                        });

                        panel(builders.vspacer("5px"));

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Agent Camera"));
                                        panel(builders.hspacer("10px"));
                                        control(new CheckboxBuilder(AGENT_CAM_CHECKBOX)
                                        {
                                            {
                                                // checked(false);
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());

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
                                                panel(builders.hspacer("30px"));
                                                control(new CheckboxBuilder(CONE_CHECKBOX));
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer("5px"));

                            }
                        });

                        panel(builders.vspacer("5px"));

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {

                                        childLayoutHorizontal();
                                        control(builders.createLabel("Hearing Enabled"));
                                        panel(builders.hspacer("40px"));
                                        control(new CheckboxBuilder(AUDITORY_ENABLED_CHECKBOX)
                                        {
                                            {
                                                // checked(true);
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(MIN_AUDIBLE_THRESHOLD_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                    }
                                });

                            }
                        });

                        panel(builders.vspacer("5px"));

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Smell Enabled"));
                                        panel(builders.hspacer("40px"));
                                        control(new CheckboxBuilder(OLFACTORY_ENABLED_CHECKBOX)
                                        {
                                            {
                                                // checked(true);
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder(SMELL_SENSITIVITY_PANEL)
                                {
                                    {
                                        childLayoutHorizontal();
                                        // control(builders.createLabel("Smell Sensitivity: "));
                                        // panel(builders.hspacer("10px"));
                                        // control(new TextFieldBuilder(SMELL_SENSITIVITY_TEXTFIELD)
                                        // {
                                        // {
                                        // width("60px");
                                        // alignLeft();
                                        // textVAlignCenter();
                                        // textHAlignLeft();
                                        // }
                                        // });
                                        // panel(new PanelBuilder()
                                        // {
                                        // {
                                        // childLayoutVertical();
                                        // valignCenter();
                                        // }
                                        // });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }
}
