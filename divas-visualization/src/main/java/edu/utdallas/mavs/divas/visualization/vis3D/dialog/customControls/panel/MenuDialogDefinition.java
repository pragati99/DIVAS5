package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link MenuDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link MenuDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class MenuDialogDefinition
{
    /**
     * The name of the control {@link MenuDialogDefinition}.
     */
    public static final String    NAME                  = MenuDialogDefinition.class.getName();

    public static final String    AGENT_BUTTON          = "#agent_button";

    public static final String    ENV_OBJ_BUTTON        = "#env_obj_button";

    public static final String    EVENT_BUTTON          = "#event_button";

    public static final String    AGENT_CONTENT_PANEL   = "#agentContentPanel";

    public static final String    ENV_OBJ_CONTENT_PANEL = "#envObjContentPanel";

    public static final String    EVENT_CONTENT_PANEL   = "#eventContentPanel";

    /**
     * The label size for the panel title.
     */
    public static String          labelSize             = "80px";

    private static CommonBuilders builders              = new CommonBuilders();

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
                controller(getController());
                panel(new PanelBuilder()
                {
                    {
                        // style("nifty-panel");
                        backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));

                        padding("5px,20px,0px,19px"); // top, right, bottom, left

                        childLayoutVertical();

                        // On start effect
                        onStartScreenEffect(new EffectBuilder("move")
                        {
                            {
                                effectParameter("mode", "in");
                                effectParameter("direction", "right");
                                length(300);
                                startDelay(0);
                                inherit(true);
                            }
                        });

                        // On show effect
                        onShowEffect(new EffectBuilder("move")
                        {
                            {
                                effectParameter("mode", "in");
                                effectParameter("direction", "right");
                                length(300);
                                startDelay(0);
                                inherit(true);
                            }
                        });

                        // On hide effect
                        onHideEffect(new EffectBuilder("move")
                        {
                            {
                                effectParameter("mode", "out");
                                effectParameter("direction", "right");
                                length(300);
                                startDelay(0);
                                inherit(true);
                            }
                        });

                        /*
                         * Menu Buttons
                         */
                        panel(builders.vspacer("10px"));
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();
                                control(new ButtonBuilder(AGENT_BUTTON, "Agent")
                                {
                                    {
                                        alignRight();
                                        width("100%");
                                        color(new Color(0.0f, 0.0f, 0.0f, 0.5f));
                                        selectionColor(new Color(0.0f, 0.0f, 0.0f, 0.9f));
                                    }
                                });
                            }
                        });
                        // panel(builders.vspacer());
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();
                                control(new ButtonBuilder(ENV_OBJ_BUTTON, "Environment Object")
                                {
                                    {
                                        alignRight();
                                        width("100%");
                                    }
                                });
                            }
                        });
                        // panel(builders.vspacer());
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();
                                control(new ButtonBuilder(EVENT_BUTTON, "Event")
                                {
                                    {
                                        alignRight();
                                        width("100%");
                                    }
                                });
                            }
                        });
                        panel(builders.vspacer("15px"));

                        /*
                         * Content
                         */
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutOverlay();

                                        panel(new PanelBuilder(AGENT_CONTENT_PANEL)
                                        {
                                            {
                                                childLayoutVertical();
                                            }
                                        });

                                        // panel(builders.vspacer());
                                        panel(new PanelBuilder(ENV_OBJ_CONTENT_PANEL)
                                        {
                                            {
                                                childLayoutVertical();
                                            }
                                        });
                                        // panel(builders.vspacer());
                                        panel(new PanelBuilder(EVENT_CONTENT_PANEL)
                                        {
                                            {
                                                childLayoutVertical();
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }

    public static Controller getController()
    {
        return new MenuDialogController();
    }
    
    public String getDefinitionName()
    {
        return NAME;
    }  
}
