package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentModification;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg.RuntimeAgentCommand;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentControlType;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerDefinition;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.AbstractPropertyDialogController;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.EvacuationHumanAgentVO;

/**
 * The AgentOptionsDialogController contains all the events that the AgentOptionsDialog element generates.
 */
public class AgentPropertyDialogController extends AbstractPropertyDialogController<EvacuationHumanAgentVO>
{
    private final static Logger        logger = LoggerFactory.getLogger(AgentPropertyDialogController.class);

    private EHumanAgentState  agent;

    // Agent Properties
    private Element                    position;
    private Element                    velocity;
    private Element                    acceleration;
    private Element                    heading;

    private Element                    desiredSpeed;
    private Element                    maxSpeed;
    private Element                    visibleDistance;
    private Element                    fov;
    private Element                    minAudibleThreshold;
    private Element                    smellSensitivity;

    private CheckBox                   auditoryEnabled;
    private CheckBox                   olfactoryEnabled;
    private CheckBox                   cameraEnabled;
    private CheckBox                   coneEnabled;

    private DropDown<Posture>          posture;
    private DropDown<AgentControlType> agentControlType;
    private DropDown<VisionAlgorithm>  visionAlgorithm;

    /**
     * Constructs a new agent properties dialog controller
     */
    public AgentPropertyDialogController()
    {}

    @Override
    public void init()
    {
        if(entityVO != null)
        {
            agent = (EHumanAgentState) entityVO.getState();
            super.init();
        }
    }

    @Override
    public void bindNiftyElements()
    {
        // Agent Properties
        setupLabels();
        setupCheckBoxes();
        setupDropDowns();
        setupSpinners();
    }

    private void setupLabels()
    {
        position = getElement(AgentPropertyDialogDefinition.POSITION_LABEL);
        velocity = getElement(AgentPropertyDialogDefinition.VELOCITY_LABEL);
        acceleration = getElement(AgentPropertyDialogDefinition.ACCELERATION_LABEL);
        heading = getElement(AgentPropertyDialogDefinition.HEADING_LABEL);
    }

    private void setupCheckBoxes()
    {
        auditoryEnabled = getNiftyControl(AgentPropertyDialogDefinition.AUDITORY_ENABLED_CHECKBOX, CheckBox.class);
        olfactoryEnabled = getNiftyControl(AgentPropertyDialogDefinition.OLFACTORY_ENABLED_CHECKBOX, CheckBox.class);
        cameraEnabled = getNiftyControl(AgentPropertyDialogDefinition.AGENT_CAM_CHECKBOX, CheckBox.class);
        coneEnabled = getNiftyControl(AgentPropertyDialogDefinition.CONE_CHECKBOX, CheckBox.class);
    }

    @SuppressWarnings("unchecked")
    private void setupDropDowns()
    {
        posture = (DropDown<Posture>) createDropDownControl("posture", AgentPropertyDialogDefinition.POSTURE_PANEL);
        agentControlType = (DropDown<AgentControlType>) createDropDownControl("agentControlType", AgentPropertyDialogDefinition.AGENT_CONTROL_PANEL);
        visionAlgorithm = (DropDown<VisionAlgorithm>) createDropDownControl("visionAlgorithm", AgentPropertyDialogDefinition.VISION_ALGORITHM_PANEL);
    }

    private void setupSpinners()
    {
        SpinnerDefinition.labelSize = "127px";
        //SpinnerDefinition.register(nifty);

        desiredSpeed = createSpinnerControl("#desiredSpeed", "Desired Speed:", 1f, 10f, 0f, agent.getDesiredSpeed(), "m/s", AgentPropertyDialogDefinition.DESIRED_SPEED_PANEL, false);
        maxSpeed = createSpinnerControl("#maxSpeed", "Speed:", 1f, 10f, 0f, agent.getMaxSpeed(), "m/s", AgentPropertyDialogDefinition.MAX_SPEED_PANEL, true);
        visibleDistance = createSpinnerControl("#visibleDistance", "Visible Distance:", 10f, 1000f, 0f, agent.getVisibleDistance(), "m", AgentPropertyDialogDefinition.VISIBLE_DISTANCE_PANEL, true);
        fov = createSpinnerControl("#fov", "Field of View:", 10f, 360f, 0f, agent.getFOV(), "degrees", AgentPropertyDialogDefinition.FOV_PANEL, true);
        minAudibleThreshold = createSpinnerControl("#minAudibleThreshold", "Audible Threshold: ", 10f, 100f, 0f, agent.getMinAudibleThreshold(), "dB",
                AgentPropertyDialogDefinition.MIN_AUDIBLE_THRESHOLD_PANEL, true);
        smellSensitivity = createSpinnerControl("#smellSensitivity", "Smell Sensitivity: ", 1f, 10f, 0f, agent.getSmellSensitivity(), "", AgentPropertyDialogDefinition.SMELL_SENSITIVITY_PANEL, true);
    }

    @Override
    public void populatePanel()
    {
        // Populate dropdowns
        populateDropDown(posture, Posture.class);
        populateDropDown(agentControlType, AgentControlType.class);
        populateDropDown(visionAlgorithm, VisionAlgorithm.class);

        // Update agent properties
        updatePanel();

        // Select dropdown item
        posture.selectItem(agent.getPosture());
        agentControlType.selectItem(agent.getControlType());
        visionAlgorithm.selectItem(agent.getVisionAlgorithm());

        // Set checkbox controls
        auditoryEnabled.setChecked(agent.isAuditoryEnabled());
        olfactoryEnabled.setChecked(agent.isOlfactoryEnabled());
        cameraEnabled.setChecked(entityVO.isCamModeOn());
        coneEnabled.setChecked(entityVO.isVisionConeEnabled());
    }

    @Override
    public void subscriptions()
    {
        /*
         * Subscriptions for DropDown Controls
         */
        nifty.subscribe(screen, getPosture().getId(), DropDownSelectionChangedEvent.class, new EventTopicSubscriber<DropDownSelectionChangedEvent<Posture>>()
        {
            @Override
            public void onEvent(final String id, DropDownSelectionChangedEvent<Posture> event)
            {
                simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_POSTURE, event.getSelection().toString());
            }
        });

        nifty.subscribe(screen, getAgentControlType().getId(), DropDownSelectionChangedEvent.class, new EventTopicSubscriber<DropDownSelectionChangedEvent<AgentControlType>>()
        {

            @Override
            public void onEvent(final String id, DropDownSelectionChangedEvent<AgentControlType> event)
            {
                simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_CONTROL_TYPE, event.getSelection().toString());
            }
        });

        nifty.subscribe(screen, getVisionAlgorithm().getId(), DropDownSelectionChangedEvent.class, new EventTopicSubscriber<DropDownSelectionChangedEvent<VisionAlgorithm>>()
        {
            @Override
            public void onEvent(final String id, DropDownSelectionChangedEvent<VisionAlgorithm> event)
            {
                VisionAlgorithm vision = event.getSelection();                
                
                simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_VISION_ALG, vision.toString());

                if(vision.equals(VisionAlgorithm.DivasVision) || vision.equals(VisionAlgorithm.NDDivasVision))
                {
                    setPartialProperties(true);
                }
                else
                {
                    setPartialProperties(false);
                }
            }
        });

        /*
         * Subscriptions for CheckBox Controls
         */
        nifty.subscribe(screen, getAuditoryEnabled().getId(), CheckBoxStateChangedEvent.class, new EventTopicSubscriber<CheckBoxStateChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final CheckBoxStateChangedEvent event)
            {
                if(auditoryEnabled.isChecked())
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.ENABLE_AUDITORY_SENSOR, null);
                }
                else
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.DISABLE_AUDITORY_SENSOR, null);
                }
            }
        });

        nifty.subscribe(screen, getOlfactoryEnabled().getId(), CheckBoxStateChangedEvent.class, new EventTopicSubscriber<CheckBoxStateChangedEvent>()
        {

            @Override
            public void onEvent(final String id, final CheckBoxStateChangedEvent event)
            {
                if(olfactoryEnabled.isChecked())
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.ENABLE_OLFACTORY_SENSOR, null);
                }
                else
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.DISABLE_OLFACTORY_SENSOR, null);
                }
            }
        });

        nifty.subscribe(screen, getConeEnabled().getId(), CheckBoxStateChangedEvent.class, new EventTopicSubscriber<CheckBoxStateChangedEvent>()
        {

            @Override
            public void onEvent(final String id, final CheckBoxStateChangedEvent event)
            {
                if(coneEnabled.isChecked())
                {
                    entityVO.setVisionCone(true);
                }
                else
                {
                    entityVO.setVisionCone(false);
                }
            }
        });

        nifty.subscribe(screen, getCameraEnabled().getId(), CheckBoxStateChangedEvent.class, new EventTopicSubscriber<CheckBoxStateChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final CheckBoxStateChangedEvent event)
            {
                if(event.getCheckBox().isChecked())
                {
                    // Dettach free camera
                    entityVO.setCamMode(true);
                    app.dettachFreeCamera();
                }

                else if(!event.getCheckBox().isChecked())
                {
                    entityVO.setCamMode(false);
                    app.attachFreeCamera();
                }
            }
        });

        /*
         * Subscriptions for Spinner Controls
         */
        nifty.subscribe(screen, getElementId(maxSpeed), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_MAX_VELOCITY, event.getText());

                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        nifty.subscribe(screen, getElementId(visibleDistance), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {

            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_VISIBLE_DISTANCE, event.getText());
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        nifty.subscribe(screen, getElementId(fov), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_FOV, event.getText());
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        nifty.subscribe(screen, getElementId(minAudibleThreshold), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {

            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_MIN_AUDIBLE_THRESHOLD, event.getText());
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        nifty.subscribe(screen, getElementId(smellSensitivity), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {

            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    simCommander.sendRuntimeAgentCommand(agent.getID(), RuntimeAgentCommand.SET_SMELL_SENSITIVITY, event.getText());
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updatePanel()
    {
        agent = (EHumanAgentState) entityVO.getState();
        updatePosition();
        updateVelocity();
        updateAcceleration();
        updateHeading();
    }

    private void updateHeading()
    {
        heading.getRenderer(TextRenderer.class).setText(String.format("(%.2f, %.2f, %.2f)", agent.getHeading().x, agent.getHeading().y, agent.getHeading().z));
    }

    private void updateAcceleration()
    {
        acceleration.getRenderer(TextRenderer.class).setText(String.format("(%.2f, %.2f, %.2f) m/s^2", agent.getAcceleration().x, agent.getAcceleration().y, agent.getAcceleration().z));
    }

    private void updateVelocity()
    {
        velocity.getRenderer(TextRenderer.class).setText(String.format("(%.2f, %.2f, %.2f) m/s", agent.getVelocity().x, agent.getVelocity().y, agent.getVelocity().z));
    }

    private void updatePosition()
    {
        position.getRenderer(TextRenderer.class).setText(String.format("(%.2f, %.2f, %.2f)", agent.getPosition().x, agent.getPosition().y, agent.getPosition().z));
    }

    /**
     * Gets the camera check box control of the nifty gui window.
     * 
     * @return the camera check box control.
     */
    public CheckBox getCameraEnabled()
    {
        return cameraEnabled;
    }

    private CheckBox getAuditoryEnabled()
    {
        return auditoryEnabled;
    }

    private CheckBox getOlfactoryEnabled()
    {
        return olfactoryEnabled;
    }

    private CheckBox getConeEnabled()
    {
        return coneEnabled;
    }

    private DropDown<Posture> getPosture()
    {
        return posture;
    }

    private DropDown<AgentControlType> getAgentControlType()
    {
        return agentControlType;
    }

    private DropDown<VisionAlgorithm> getVisionAlgorithm()
    {
        return visionAlgorithm;
    }

    @SuppressWarnings("unused")
    private String getDesiredSpeedTextFieldId()
    {
        return desiredSpeed.getControl(SpinnerController.class).getTextFieldId();
    }

    private void setPartialProperties(boolean enabled)
    {
        logger.info("Partial properties is enabled? {}", enabled);

        if(enabled)
        {
            fov.enable();
            visibleDistance.enable();
            coneEnabled.enable();
        }
        else
        {
            fov.disable();
            visibleDistance.disable();
            coneEnabled.disable();
        }
    }

    @Override
    public void setEntity(EvacuationHumanAgentVO entity)
    {
        super.entityVO = entity;
    }
}
