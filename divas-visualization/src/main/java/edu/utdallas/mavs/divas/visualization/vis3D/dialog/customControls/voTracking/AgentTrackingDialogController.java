package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voTracking;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyAttributes;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.RegExpressionHelper;

/**
 * This class represents the controller for the AgentTrackingDialog Nifty control.
 */
public class AgentTrackingDialogController extends AbstractDialogController
{
    private static Logger logger = LoggerFactory.getLogger(AgentTrackingDialogController.class);

    private Element       trackButton;
    private Element       untrackButton;
    private TextField     agentTrackingTextField;

    private String        currentTextField;

    @Override
    public void bindNiftyElements()
    {
        trackButton = getElement(AgentTrackingDialogDefinition.AGENT_TRACK_BUTTON);
        untrackButton = getElement(AgentTrackingDialogDefinition.AGENT_UNTRACK_BUTTON);
        agentTrackingTextField = getNiftyControl(AgentTrackingDialogDefinition.AGENT_TRACKING_RANGE_TEXTFIELD, TextField.class);

        currentTextField = agentTrackingTextField.getText();
    }

    @Override
    public void populatePanel()
    {}

    @Override
    public void subscriptions()
    {
        nifty.subscribe(screen, getTrackButton().getId(), NiftyMouseEvent.class, new EventTopicSubscriber<NiftyMouseEvent>()
        {
            @Override
            public void onEvent(final String id, final NiftyMouseEvent event)
            {
                if(event.isButton0Down())
                {
                    trackAgent(getAgentTrackingTextField().getText());
                }
            }
        });

        nifty.subscribe(screen, getUntrackButton().getId(), NiftyMouseEvent.class, new EventTopicSubscriber<NiftyMouseEvent>()
        {
            @Override
            public void onEvent(final String id, final NiftyMouseEvent event)
            {
                if(event.isButton0Down())
                {
                    untrackAllAgents();
                }
            }
        });

        nifty.subscribe(screen, getAgentTrackingTextField().getId(), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                if(!event.getText().equals(NiftyAttributes.AGENT_RANGE_EXAMPLE) && getCurrentTextField().equals(NiftyAttributes.AGENT_RANGE_EXAMPLE))
                {
                    setAgentTrackingTextField("");
                }
            }
        });
    }

    @Override
    public void updatePanel()
    {}

    private void trackAgent(String string)
    {
        if(RegExpressionHelper.isNumberPattern(string))
        {
            app.getSimulatingAppState().trackAgent(Integer.valueOf(string));
        }

        else if(RegExpressionHelper.isNumberRangePattern(string))
        {
            String[] range = string.split("-");
            app.getSimulatingAppState().trackAgentsInRange(Integer.valueOf(range[0]), Integer.valueOf(range[1]));
        }
    }

    private void untrackAllAgents()
    {
        logger.info("Untracking all agents");
        app.getSimulatingAppState().untrackAllAgents();
    }

    /**
     * Gets the button to track.
     * 
     * @return
     *         The track button.
     */
    public Element getTrackButton()
    {
        return trackButton;
    }

    /**
     * Gets the untrack button.
     * 
     * @return
     *         The untrack button.
     */
    public Element getUntrackButton()
    {
        return untrackButton;
    }

    /**
     * Gets the agent tracking textfield.
     * 
     * @return The agent tracking textfield.
     */
    public TextField getAgentTrackingTextField()
    {
        return agentTrackingTextField;
    }

    /**
     * Gets the current string of the agent tracking textfield.
     * 
     * @return
     *         The text in the agent tracking textfield.
     */
    public String getCurrentTextField()
    {
        return currentTextField;
    }

    private void setAgentTrackingTextField(String string)
    {
        getAgentTrackingTextField().setText(string);
        currentTextField = string;
    }
}
