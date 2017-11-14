package edu.utdallas.mavs.divas.visualization.vis3D.dialog;

import java.util.HashMap;
import java.util.Map;

import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.HelpDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.MenuDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.AbstractPropertyDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyAttributes;

/**
 * This class describes a nifty screen.
 * 
 * @param <M>
 * @param <H>
 */
public class NiftyScreen<M extends MenuDialog, H extends HelpDialog>
{
    private static NiftyScreen<?, ?>                           instance;

    /**
     * The JME application used for the 3D visualization of the simulation.
     */
    public BaseApplication<?, ?>                               app;

    /**
     * The Nifty GUI used to render this screen.
     */
    public Nifty                                               nifty;

    /**
     * The current and only Screen in the Nifty GUI. This element is the parent element for all other Nifty GUI
     * elements.
     */
    public Screen                                              screen;

    /**
     * The parent Element (layer) for the nifty windows.
     */
    public static Element                                      windowLayerElement;

    /**
     * The parent element (layer) for the nifty menu.
     */
    public static Element                                      menuLayerElement;

    /**
     * The parent element (layer) for the nifty help menu.
     */
    public static Element                                      helpPanelElement;

    /**
     * A map of context dialogs currently displayed in the Nifty GUI.
     */
    protected static Map<String, AbstractPropertyDialog<?, ?>> contextWindows;

    protected H                                                helpDialog;

    protected M                                                menuDialog;

    /**
     * This method returns the singleton instance of {@link NiftyScreen}. If the instance is null, then the singleton
     * instance is created.
     * 
     * @return instance The Nifty Screen singleton instance.
     */
    public static <M extends MenuDialog, H extends HelpDialog> NiftyScreen<?, ?> getInstance()
    {
        if(instance == null)
            new NiftyScreen<M, H>();

        return instance;
    }

    /**
     * The {@link NiftyScreen} constructor.
     */
    public NiftyScreen()
    {
        instance = this;
        this.app = Visualizer3DApplication.getInstance().getApp();

        NiftyJmeDisplay niftyDisplay = new NiftyDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();

        // attach the nifty display to the gui view port as a processor
        app.getGuiViewPort().addProcessor(niftyDisplay);

        nifty.loadStyleFile("styles/nifty-controls-style-black/src/main/resources/nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        createNiftyScreen();

        nifty.gotoScreen(NiftyAttributes.NIFTY_SCREEN);
        screen = nifty.getCurrentScreen();

        // Layer elements
        windowLayerElement = screen.findElementByName(NiftyAttributes.NIFTY_LAYER_WINDOWS);
        menuLayerElement = screen.findElementByName(NiftyAttributes.NIFTY_LAYER_MENU);
        helpPanelElement = screen.findElementByName(NiftyAttributes.NIFTY_LAYER_HELP_MENU);

        // For debugging Nifty layouts
        // nifty.setDebugOptionPanelColors(true);

        contextWindows = new HashMap<String, AbstractPropertyDialog<?, ?>>();

        createNiftyPanels();
    }

    @SuppressWarnings("unchecked")
    protected void createNiftyPanels()
    {
        menuDialog = (M) new MenuDialog(menuLayerElement);
        helpDialog = (H) new HelpDialog(helpPanelElement);
    }

    /**
     * Creates a new Nifty Screen
     */
    private void createNiftyScreen()
    {
        new ScreenBuilder(NiftyAttributes.NIFTY_SCREEN)
        {
            {
                layer(new LayerBuilder(NiftyAttributes.NIFTY_LAYER_WINDOWS)
                {
                    {
                        childLayoutAbsolute();
                    }
                });
                layer(new LayerBuilder(NiftyAttributes.NIFTY_LAYER_MENU)
                {
                    {
                        childLayoutVertical();
                    }
                });
                layer(new LayerBuilder(NiftyAttributes.NIFTY_LAYER_HELP_MENU)
                {
                    {
                        childLayoutCenter();
                        paddingLeft("5%");
                    }
                });
            }
        }.build(nifty);
    }

    /**
     * Shows the Nifty element for the given id.
     * 
     * @param id
     *        The id of the element to show.
     * @return false, if no element with the given id exits and true, if an element with the given id exists and was
     *         showed.
     */
    public boolean showElement(String id)
    {
        if(contextWindows.containsKey(id))
        {
            contextWindows.get(id).showDialog();
            return true;
        }
        return false;
    }

    /**
     * Shows all context windows.
     */
    public void showContextWindows()
    {
        for(AbstractPropertyDialog<?, ?> dialog : contextWindows.values())
        {
            dialog.showDialog();
        }
    }

    /**
     * Removes the Nifty element with the given id. The element is marked for removal and Nifty takes care of removing when
     * it's ready.
     * 
     * @param id
     *        The id of the element to be removed.
     */
    public void removeContextWindow(String id)
    {
        if(contextWindows.containsKey(id))
        {
            //System.out.println("removing window with id: " +id);
            contextWindows.remove(id);
        }
    }

    /**
     * Removes all Nifty elements. The elements are marked for removal and Nifty takes care of removing when it's ready.
     */
    public void removeAllElements()
    {
        removeAllContextWindows();

        if(helpDialog.isEnabled())
        {
            helpDialog.hideDialog();
        }

        if(menuDialog.isEnabled())
        {
            menuDialog.hideDialog();
        }
    }

    /**
     * Removes all context windows. The elements are marked for removal and Nifty takes care of removing when it's ready.
     */
    public void removeAllContextWindows()
    {
        if(helpDialog.isEnabled())
        {
            helpDialog.hideDialog();
        }

        for(AbstractPropertyDialog<?, ?> dialog : contextWindows.values())
        {
            dialog.removeDialog();
        }
        contextWindows.clear();
    }

    /**
     * Hides all Nifty elements.
     */
    public void hideAllElements()
    {
        hideContextWindows();

        if(helpDialog != null)
        {
            helpDialog.hideDialog();
        }
        if(menuDialog != null)
        {
            menuDialog.hideDialog();
        }
    }

    /**
     * Hides all context windows.
     */
    public void hideContextWindows()
    {
        for(AbstractPropertyDialog<?, ?> dialog : contextWindows.values())
        {
            dialog.hideDialog();
        }
    }

    /**
     * If the given dialog is hidden this method shows it, otherwise hides it.
     * 
     * @param dialog
     *        The dialog to be shown or hidden.
     */
    public void showHide(AbstractDialog<?> dialog)
    {
        if(dialog != null)
        {
            if(dialog.isEnabled())
            {
                dialog.hideDialog();

                if(dialog instanceof HelpDialog)
                {
                    showContextWindows();
                }
            }
            else
            {
                if(dialog instanceof HelpDialog)
                {
                    hideContextWindows();
                }

                dialog.showDialog();
            }
        }
    }

    /**
     * Gets the help dialog.
     * 
     * @return
     *         The help dialog.
     */
    public AbstractDialog<?> getHelpDialog()
    {
        return helpDialog;
    }

    /**
     * Gets the menu dialog.
     * 
     * @return
     *         The menu dialog.
     */
    public AbstractDialog<?> getMenuDialog()
    {
        return menuDialog;
    }

    /**
     * Updates the content of the context windows.
     */
    public void updateContextWindows()
    {
        for(AbstractPropertyDialog<?, ?> dialog : contextWindows.values())
        {
            dialog.updateDialog();
        }
    }

    /**
     * Adds the dialog to the context window map.
     * 
     * @param id
     *        The dialog id
     * @param dialog
     *        The dialog instance
     */
    public void addContextWindow(String id, AbstractPropertyDialog<?, ?> dialog)
    {
        contextWindows.put(id, dialog);
    }

    /**
     * @param dialogId
     *        The dialog id.
     * @return
     *         true if the dialog is present in the context window map, otherwise returns false.
     */
    public boolean contains(String dialogId)
    {
        return contextWindows.containsKey(dialogId);
    }
}
