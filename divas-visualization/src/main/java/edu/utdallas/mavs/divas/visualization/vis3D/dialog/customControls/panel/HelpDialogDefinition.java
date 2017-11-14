package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.tools.Color;

/**
 * The {@link HelpDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link HelpDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class HelpDialogDefinition
{
    /**
     * The name of the control {@link HelpDialogDefinition}.
     */
    public static final String NAME                 = HelpDialogDefinition.class.getName();

    public static final String HELP_CONTENT_HEADING = "headingHelpContent";

    public static final String HELP_CONTENT_LEFT    = "leftHelpContent";

    public static final String HELP_CONTENT_RIGHT   = "rightHelpContent";

    public static String       labelSize            = "*";

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
                controller(new HelpDialogController());
                panel(new PanelBuilder()
                {
                    {
                        backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));

                        padding("10px");

                        childLayoutVertical();

                        // On start effect
                        onStartScreenEffect(new EffectBuilder("move")
                        {
                            {
                                effectParameter("mode", "in");
                                effectParameter("direction", "top");
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
                                effectParameter("direction", "top");
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
                                effectParameter("direction", "top");
                                length(300);
                                startDelay(0);
                                inherit(true);
                            }
                        });

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();

                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutVertical();
                                        width("*");
                                    }
                                });

                                control(new LabelBuilder()
                                {
                                    {
                                        text("[ CLOSE ]");
                                        font("fonts/aurulent-sans-16-bold.fnt");
                                        color("#FFCC33");
                                        width("100px");
                                        alignLeft();
                                        interactOnClick("closeHelpPanel()");
                                        textVAlignCenter();
                                        textHAlignLeft();
                                    }
                                });

                            }

                        });

                        panel(new PanelBuilder(HELP_CONTENT_HEADING)
                        {
                            {
                                childLayoutVertical();
                            }
                        });

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutHorizontal();

                                panel(new PanelBuilder(HELP_CONTENT_LEFT)
                                {
                                    {
                                        childLayoutVertical();
                                    }
                                });

                                panel(new PanelBuilder(HELP_CONTENT_RIGHT)
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
        }.registerControlDefintion(nifty);
    }
}
