package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.envObjModification;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DefaultController;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link EnvObjPropertyDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link EnvObjPropertyDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class EnvObjPropertyDialogDefinition
{
    /**
     * The name of the control {@link EnvObjPropertyDialogDefinition}.
     */
    public static final String    NAME          = EnvObjPropertyDialogDefinition.class.getName();

    public static final String    POSITION_X    = "#position_x";

    public static final String    POSITION_Y    = "#position_y";

    public static final String    POSITION_Z    = "#position_z";

    public static final String    SCALE_X       = "#scale_x";

    public static final String    SCALE_Y       = "#scale_y";

    public static final String    SCALE_Z       = "#scale_z";

    public static final String    ROTATION_X    = "#rotation_x";

    public static final String    ROTATION_Y    = "#rotation_y";

    public static final String    ROTATION_Z    = "#rotation_z";

    public static final String    DELETE_BUTTON = "#delete_button";

    private static CommonBuilders builders      = new CommonBuilders();

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

                        // Config Panel
                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();

                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Position: ", "80px"));
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        width("*");
                                        panel(new PanelBuilder(POSITION_X)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));
                                        panel(new PanelBuilder(POSITION_Y)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));
                                        panel(new PanelBuilder(POSITION_Z)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Scale: ", "80px"));
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();

                                        panel(new PanelBuilder(SCALE_X)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));
                                        panel(new PanelBuilder(SCALE_Y)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));
                                        panel(new PanelBuilder(SCALE_Z)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer());

                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(builders.createLabel("Rotation: ", "80px"));
                                    }
                                });
                                panel(builders.vspacer());
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();

                                        panel(new PanelBuilder(ROTATION_X)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));

                                        panel(new PanelBuilder(ROTATION_Y)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                        panel(builders.hspacer("2px"));

                                        panel(new PanelBuilder(ROTATION_Z)
                                        {
                                            {
                                                childLayoutHorizontal();
                                            }
                                        });
                                    }
                                });
                                panel(builders.vspacer("20px"));
                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutHorizontal();
                                        control(new ButtonBuilder(DELETE_BUTTON, "Delete"));
                                    }
                                });
                                panel(builders.vspacer());
                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(nifty);
    }
}
