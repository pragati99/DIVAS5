package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.DefaultController;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.tools.Color;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * The {@link AbstractCreationDialogDefinition} registers a new control with Nifty that
 * represents the whole {@link AbstractCreationDialogDefinition}. This gives us later an
 * appropriate ControlBuilder to actual construct the Dialog (as a control) with
 * the given NAME.
 */
@SuppressWarnings("javadoc")
public class AbstractCreationDialogDefinition
{
    /**
     * The name of the control {@link AbstractCreationDialogDefinition}.
     */
    public static final String    NAME                      = AbstractCreationDialogDefinition.class.getName();

    public static final String    SEARCH_TEXTFIELD          = "#env_obj_search";

    public static final String    SEARCH_BUTTON             = "#env_obj_search_button";

    public static final String    GRID_SCROLL_PANEL         = "#grid_scroll_panel";

    public static final String    GRID_PANEL                = "#env_obj_grid";

    public static final String    LAST_USED_RESOURCES_PANEL = "#lastUsedResourcePanel";

    /**
     * The label size for the panel title.
     */
    public static String          labelSize                 = "100%";

    private static CommonBuilders builders                  = new CommonBuilders();

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
                        childLayoutVertical();

                        visibleToMouse();

                        panel(new PanelBuilder()
                        {
                            {
                                childLayoutVertical();

                                panel(new PanelBuilder()
                                {
                                    {
                                        childLayoutVertical();

                                        padding("5px,10px,5px,10px"); // top, right, bottom, left

                                        backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));

                                        panel(new PanelBuilder()
                                        {
                                            {
                                                childLayoutVertical();

                                                control(builders.createLabel("$title_label", labelSize));
                                            }
                                        });

                                        panel(builders.vspacer("5px"));

                                        panel(new PanelBuilder()
                                        {
                                            {
                                                childLayoutHorizontal();

                                                control(new TextFieldBuilder(SEARCH_TEXTFIELD)
                                                {
                                                    {
                                                        width("*");
                                                        padding("5px");
                                                        alignLeft();
                                                        textVAlignCenter();
                                                        textHAlignLeft();
                                                    }
                                                });

                                                panel(builders.hspacer("5px"));

                                                control(new ButtonBuilder(SEARCH_BUTTON, "Search")
                                                {
                                                    {
                                                        width("70px");
                                                    }
                                                });
                                            }
                                        });

                                        panel(builders.vspacer("10px"));
                                    }
                                });

                                panel(builders.vspacer("10px"));

                                // panel(new PanelBuilder(GRID_PANEL)
                                // {
                                // {
                                // childLayoutVertical();
                                //
                                // height("80%");
                                // width("100%");
                                // }
                                // });

                                // control(new ScrollPanelBuilder(GRID_SCROLL_PANEL)
                                // {
                                // {
                                // childLayoutVertical();

                                // visibleToMouse(false);

                                // interactOnClick("interact()");

                                backgroundColor(new Color(0.0f, 0.0f, 0.0f, 0.4f));

                                height("*");
                                alignCenter();
                                set("horizontal", "false");

                                panel(new PanelBuilder(GRID_PANEL)
                                {
                                    {
                                        padding("10px,10px,10px,10px"); // top, right, bottom, left

                                        alignCenter();
                                        valignCenter();
                                        childLayoutVertical();
                                    }
                                });
                                // }
                                // });

                            }
                        });

                        // panel(builders.vspacer());

                        // panel(new PanelBuilder(LAST_USED_RESOURCES_PANEL)
                        // {
                        // {
                        // padding("10px,10px,10px,10px"); // top, right, bottom, left
                        //
                        // alignCenter();
                        // valignCenter();
                        // childLayoutVertical();
                        // }
                        // });

                        // panel(builders.vspacer());
                    }
                });
            }

        }.registerControlDefintion(nifty);
    }
}
