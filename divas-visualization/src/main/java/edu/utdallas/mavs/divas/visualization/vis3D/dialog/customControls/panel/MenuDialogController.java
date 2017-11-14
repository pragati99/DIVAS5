package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel;

import java.util.HashMap;
import java.util.Map;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.agentCreation.AgentCreationDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.agentCreation.AgentCreationDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.envObjectCreation.EnvObjectCreationDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.envObjectCreation.EnvObjectCreationDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.eventCreation.EventCreationDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voCreation.eventCreation.EventCreationDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voTracking.AgentTrackingDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voTracking.AgentTrackingDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.CommonBuilders;

/**
 * This class represents the controller for the MenuDialog Nifty control.
 */
public class MenuDialogController extends AbstractDialogController
{
    private static Logger           logger       = LoggerFactory.getLogger(MenuDialogController.class);

    protected Button                agentButton;
    protected Button                envObjButton;
    protected Button                eventButton;

    protected Button                selectedButton;

    protected Element               agentContentPanel;
    protected Element               eventContentPanel;
    protected Element               envObjectContentPanel;

    protected Map<Button, Element>  menuElements = new HashMap<Button, Element>();

    protected static CommonBuilders builders     = new CommonBuilders();

//    @Override
//    public void onStartScreen()
//    {}

    @Override
    public void bindNiftyElements()
    {
        setupElements();
        setupCustomControls();
    }

    private void setupElements()
    {
        agentButton = getNiftyControl(MenuDialogDefinition.AGENT_BUTTON, Button.class);
        eventButton = getNiftyControl(MenuDialogDefinition.EVENT_BUTTON, Button.class);
        envObjButton = getNiftyControl(MenuDialogDefinition.ENV_OBJ_BUTTON, Button.class);

        agentContentPanel = getElement(MenuDialogDefinition.AGENT_CONTENT_PANEL);
        eventContentPanel = getElement(MenuDialogDefinition.EVENT_CONTENT_PANEL);
        envObjectContentPanel = getElement(MenuDialogDefinition.ENV_OBJ_CONTENT_PANEL);

        menuElements.put(getAgentButton(), agentContentPanel);
        menuElements.put(getEventButton(), eventContentPanel);
        menuElements.put(getEnvObjButton(), envObjectContentPanel);

        selectButton(getAgentButton());
    }

    private void setupCustomControls()
    {
        createAgentContent();
        createEventContent();
        createEnvObjectContent();
    }

    protected void createAgentContent()
    {
        AbstractDialog<AgentCreationDialogController> agentCreationDialog = new AgentCreationDialog(agentContentPanel);
        AbstractDialog<AgentTrackingDialogController> agentTrackingDialog = new AgentTrackingDialog(agentContentPanel);

        agentCreationDialog.createDialog();
        breakLine(agentContentPanel);
        agentTrackingDialog.createDialog();
        breakLine(agentContentPanel);
    }

    protected void createEnvObjectContent()
    {
        AbstractDialog<EnvObjectCreationDialogController> envObjectCreationDialog = new EnvObjectCreationDialog(envObjectContentPanel);
        envObjectCreationDialog.createDialog();
        //breakLine(envObjectContentPanel);
    }

    protected void createEventContent()
    {
        AbstractDialog<EventCreationDialogController> eventCreationDialog = new EventCreationDialog(eventContentPanel);
        eventCreationDialog.createDialog();
        //breakLine(eventContentPanel);
    }

    protected void selectButton(Button button)
    {
        if(selectedButton == null || !selectedButton.equals(button))
        {
            // TODO Change the button color when selected and deselected.
            if(selectedButton != null)
            {
                hide(menuElements.get(selectedButton));
            }

            selectedButton = button;

            show(menuElements.get(button));
        }
    }

    protected void hide(Element element)
    {
        if(element != null)
            element.hide();
    }

    protected void show(Element element)
    {
        if(element != null)
            element.show();
    }

    @Override
    public void populatePanel()
    {}

    @Override
    public void subscriptions()
    {
        /*
         * Subscriptions for button control
         */
        nifty.subscribe(screen, getAgentButton().getId(), ButtonClickedEvent.class, new EventTopicSubscriber<ButtonClickedEvent>()
        {
            @Override
            public void onEvent(final String id, final ButtonClickedEvent event)
            {
                // Show the agent panel and hide all other panels
                selectButton(getAgentButton());
            }
        });

        nifty.subscribe(screen, getEnvObjButton().getId(), ButtonClickedEvent.class, new EventTopicSubscriber<ButtonClickedEvent>()
        {
            @Override
            public void onEvent(final String id, final ButtonClickedEvent event)
            {
                // Show the env obj panel and hide all other panels
                selectButton(getEnvObjButton());
            }
        });

        nifty.subscribe(screen, getEventButton().getId(), ButtonClickedEvent.class, new EventTopicSubscriber<ButtonClickedEvent>()
        {
            @Override
            public void onEvent(final String id, final ButtonClickedEvent event)
            {
                // Show the event panel and hide all other panels
                selectButton(getEventButton());
            }
        });
    }

    @Override
    public void updatePanel()
    {}

    protected void hideUnselected()
    {
        if(selectedButton != null)
        {
            for(Element element : menuElements.values())
            {
                if(element != null && !element.equals(menuElements.get(selectedButton)))
                {
                    logger.info("Element: {}", element.getId());

                    hide(element);
                }
            }
        }
    }

    /**
     * Gets the button with content regarding agent creation, modification, etc.
     * 
     * @return
     *         The agent button
     */
    public Button getAgentButton()
    {
        return agentButton;
    }

    /**
     * Gets the button with content regarding environment object creation.
     * 
     * @return
     *         The environment object button
     */
    public Button getEnvObjButton()
    {
        return envObjButton;
    }

    /**
     * Gets the button with content regarding event selection.
     * 
     * @return
     *         The event button
     */
    public Button getEventButton()
    {
        return eventButton;
    }

    protected void breakLine(Element element)
    {
        (builders.vspacer("20px")).build(nifty, screen, element);
    }

    /*
     * private void createDefaultEventContent()
     * {
     * CustomControlCreator customControl;
     * Element element;
     * Element el = (builders.createLabel("Default events")).build(nifty, screen, eventContentPanel);
     * el.hideWithoutEffect();
     * EventDialogDefinition.register(nifty);
     * customControl = new CustomControlCreator(String.format("#%s", "eventDefaultContent"), EventDialogDefinition.NAME);
     * customControl.setController((new EventDialogController()).getClass().getName());
     * element = customControl.create(nifty, screen, eventContentPanel);
     * element.hideWithoutEffect();
     * }
     */

    // private void createAgentContent()
    // {
    // createAgentCreationContent();
    //
    // breakLine(agentContentPanel);
    //
    // createAgentTrackContent();
    //
    // breakLine(agentContentPanel);
    //
    // createAgentPropertiesContent();
    //
    // menuElements.put(agentButton, agentContentPanel);
    //
    // selectButton(getAgentButton());
    // }

    /**
     * Create Event Content.
     */
    /*
     * private void createEventContent()
     * {
     * createEventCreationContent();
     * menuElements.put(eventButton, eventContentPanel);
     * }
     */

    /**
     * Create Environment Object Content
     */
    /*
     * private void createEnvObjectContent()
     * {
     * // CreationDialogDefinition.labelSize = "100%";
     * // CreationDialogDefinition.register(nifty);
     * //
     * // CustomControlCreator customControl = new CustomControlCreator(String.format("#%s", "envObjContent"), CreationDialogDefinition.NAME);
     * // customControl.setController((new EnvObjectCreationDialogController()).getClass().getName());
     * // customControl.set("title_label", "Environment Objects Creation");
     * // customControl.set("search_button", "Search");
     * //
     * // Element element = customControl.create(nifty, screen, getElement(MenuDialogDefinition.ENV_OBJ_CONTENT_PANEL));
     * // element.hideWithoutEffect();
     * AbstractDialog dialog = new EnvObjectCreationDialog(envObjectContentPanel);
     * dialog.createDialog();
     * menuElements.put(envObjButton, dialog.getElement());
     * }
     */

    /**
     * Agent creation
     */
    /*
     * private void createAgentCreationContent()
     * {
     * /*
     * CreationDialogDefinition.labelSize = "100%";
     * CreationDialogDefinition.register(nifty);
     * CustomControlCreator customControl = new CustomControlCreator(String.format("#%s", "agentCreationContent"), CreationDialogDefinition.NAME);
     * customControl.setController((new AgentCreationDialogController()).getClass().getName());
     * customControl.set("title_label", "Agent Creation");
     * customControl.set("search_button", "Search");
     * Element element = customControl.create(nifty, screen, agentContentPanel);
     * element.hideWithoutEffect();
     */

    /*
     * AbstractDialog dialog = new AgentCreationDialog(agentContentPanel);
     * dialog.createDialog();
     * }
     * private void createAgentTrackContent()
     * {
     * AgentTrackingDialogDefinition.labelSize = "100%";
     * AgentTrackingDialogDefinition.register(nifty);
     * CustomControlCreator customControl = getCustomControl(String.format("#%s", "agentTrackContent"), AgentTrackingDialogDefinition.NAME);
     * Element element = customControl.create(nifty, screen, agentContentPanel);
     * element.hideWithoutEffect();
     * }
     * private void createAgentPropertiesContent()
     * {
     * AgentGlobalOptionsDialogDefinition.labelSize = "100%";
     * AgentGlobalOptionsDialogDefinition.register(nifty);
     * // Adds a custom control to the window
     * CustomControlCreator customControl = new CustomControlCreator(String.format("#%s", "agentOptionsContent"), AgentGlobalOptionsDialogDefinition.NAME);
     * customControl.setController((new AgentGlobalOptionsDialogController()).getClass().getName());
     * customControl.set("title_label", "Agent General Properties");
     * Element element = customControl.create(nifty, screen, agentContentPanel);
     * element.hideWithoutEffect();
     * }
     * private void createEventCreationContent()
     * {
     * CreationDialogDefinition.labelSize = "100%";
     * CreationDialogDefinition.register(nifty);
     * CustomControlCreator customControl = new CustomControlCreator(String.format("#%s", "eventContent"), CreationDialogDefinition.NAME);
     * customControl.setController((new EventCreationDialogController()).getClass().getName());
     * customControl.set("title_label", "Events");
     * customControl.set("search_button", "Search");
     * Element element = customControl.create(nifty, screen, eventContentPanel);
     * element.hideWithoutEffect();
     * }
     */

    /*
     * private CustomControlCreator getCustomControl(String id, String name)
     * {
     * CustomControlCreator customControl = new CustomControlCreator(id, name);
     * customControl.setController((new AgentTrackingDialogController()).getClass().getName());
     * customControl.set("title_label", "Agent Tracking");
     * return customControl;
     * }
     */
}
