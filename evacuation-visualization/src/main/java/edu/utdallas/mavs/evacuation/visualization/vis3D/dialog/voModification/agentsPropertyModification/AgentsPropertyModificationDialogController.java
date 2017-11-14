package edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentsPropertyModification;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseEvent;
import edu.utdallas.mavs.divas.core.client.dto.GeneralAgentProperties;
import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerDefinition;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.AbstractDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.NiftyAttributes;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils.RegExpressionHelper;

/**
 * This class represents the controller for the AgentsPropertyModificationDialog Nifty control.
 */
public class AgentsPropertyModificationDialogController extends AbstractDialogController
{
    private static Logger             logger = LoggerFactory.getLogger(AgentsPropertyModificationDialogController.class);

    // Agent Properties
    private DropDown<VisionAlgorithm> visionAlgorithmDropDown;
    private Element                   visibleDistanceElement;
    private Element                   fovElement;
    private CheckBox                  coneEnabled;
    private CheckBox                  auditoryEnabled;
    private CheckBox                  olfactoryEnabled;
    private Element                   applyButtonElement;
    private TextField                 agentRangeTextField;
    private CheckBox                  applyAllEnabled;

    private String                    currentTextField;

    @Override
    public void bindNiftyElements()
    {
        setupButtons();
        setupCheckBoxes();
        setupDropDowns();
        setupSpinners();
        setupTextField();
    }

    private void setupTextField()
    {
        agentRangeTextField = getNiftyControl(AgentsPropertyModificationDialogDefinition.AGENT_RANGE_TEXTFIELD, TextField.class);
        currentTextField = agentRangeTextField.getText();
    }

    private void setupButtons()
    {
        applyButtonElement = getElement(AgentsPropertyModificationDialogDefinition.AGENT_PROPERTY_BUTTON);
    }

    private void setupCheckBoxes()
    {
        coneEnabled = getNiftyControl(AgentsPropertyModificationDialogDefinition.CONE_CHECKBOX, CheckBox.class);
        auditoryEnabled = getNiftyControl(AgentsPropertyModificationDialogDefinition.AUDITORY_ENABLED_CHECKBOX, CheckBox.class);
        olfactoryEnabled = getNiftyControl(AgentsPropertyModificationDialogDefinition.OLFACTORY_ENABLED_CHECKBOX, CheckBox.class);
        applyAllEnabled = getNiftyControl(AgentsPropertyModificationDialogDefinition.APPLY_ALL_CHECKBOX, CheckBox.class);
    }

    @SuppressWarnings("unchecked")
    private void setupDropDowns()
    {
        visionAlgorithmDropDown = (DropDown<VisionAlgorithm>) createDropDownControl("#visionAlgorithm", AgentsPropertyModificationDialogDefinition.VISION_ALGORITHM_PANEL);
    }

    private void setupSpinners()
    {
        SpinnerDefinition.labelSize = "110px";
        // SpinnerDefinition.register(nifty);

        visibleDistanceElement = createSpinnerControl("#visibleDistance", "Visible Distance:", 10f, 1000f, 0f, SimConfig.getInstance().default_visible_Distance, "m",
                AgentsPropertyModificationDialogDefinition.VISIBLE_DISTANCE_PANEL, true);
        fovElement = createSpinnerControl("#fov", "Field of View:", 10f, 360f, 0f, SimConfig.getInstance().default_fov, "degrees", AgentsPropertyModificationDialogDefinition.FOV_PANEL, true);
    }

    @Override
    public void populatePanel()
    {
        populateDropDown(visionAlgorithmDropDown, VisionAlgorithm.class);

        // Set checkbox controls
        auditoryEnabled.setChecked(true);
        olfactoryEnabled.setChecked(true);
        coneEnabled.setChecked(false);

        // Set the drop down
        visionAlgorithmDropDown.selectItem(SimConfig.getInstance().default_Vision_Algorithm);
    }

    @Override
    public void subscriptions()
    {
        /*
         * Subscriptions for button control
         */
        nifty.subscribe(screen, getApplyButton().getId(), NiftyMouseEvent.class, new EventTopicSubscriber<NiftyMouseEvent>()
        {
            @Override
            public void onEvent(final String id, final NiftyMouseEvent event)
            {
                if(event.isButton0Down())
                {
                    if(!getAgentRange().isEnabled())
                    {
                        applyPropertyAll();
                    }
                    else
                    {
                        applyProperty(getAgentRange().getText());
                    }
                }
            }
        });

        /*
         * Subscriptions for textfield
         */
        nifty.subscribe(screen, getAgentRange().getId(), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                logger.info("text field event");

                if(!event.getText().equals(NiftyAttributes.AGENT_RANGE_EXAMPLE) && getCurrentTextField().equals(NiftyAttributes.AGENT_RANGE_EXAMPLE))
                {
                    setAgentRangeTextField("");
                }
            }
        });

        /*
         * Subscriptions for DropDown Controls
         */
        nifty.subscribe(screen, getVisionAlgorithm().getId(), DropDownSelectionChangedEvent.class, new EventTopicSubscriber<DropDownSelectionChangedEvent<VisionAlgorithm>>()
        {
            @Override
            public void onEvent(final String id, DropDownSelectionChangedEvent<VisionAlgorithm> event)
            {
                VisionAlgorithm vision = event.getSelection();

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
        nifty.subscribe(screen, getApplyAllEnabled().getId(), CheckBoxStateChangedEvent.class, new EventTopicSubscriber<CheckBoxStateChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final CheckBoxStateChangedEvent event)
            {
                if(getApplyAllEnabled().isChecked())
                {
                    getAgentRange().disable();
                }
                else
                {
                    getAgentRange().enable();
                }
            }
        });

    }

    @Override
    public void updatePanel()
    {}

    /**
     * Gets the apply property button for applying the properties to the selected agents in the simulation.
     * 
     * @return the apply property button
     */
    public Element getApplyButton()
    {
        return applyButtonElement;
    }

    private void applyPropertyAll()
    {
        app.getSimulatingAppState().applyAgentPropertiesForAll(getGeneralAgentProperties());
    }

    private void applyProperty(String text)
    {
        if(RegExpressionHelper.isNumberPattern(text))
        {
            app.getSimulatingAppState().applyAgentPropertiesForAgent(Integer.valueOf(text), getGeneralAgentProperties());
        }

        else if(RegExpressionHelper.isNumberRangePattern(text))
        {
            String[] range = text.split("-");
            app.getSimulatingAppState().applyAgentPropertiesForRange(Integer.valueOf(range[0]), Integer.valueOf(range[1]), getGeneralAgentProperties());
        }
    }

    private GeneralAgentProperties getGeneralAgentProperties()
    {
        VisionAlgorithm visionAlgorithm = visionAlgorithmDropDown.getSelection(); 
        float visDist = Float.valueOf(getSpinnerText(visibleDistanceElement));
        float fov = Float.valueOf(getSpinnerText(fovElement));
        boolean cone = coneEnabled.isChecked();
        boolean auditory = auditoryEnabled.isChecked();
        boolean olfactory = olfactoryEnabled.isChecked();

        return new GeneralAgentProperties(visionAlgorithm, visDist, fov, cone, auditory, olfactory);
    }

    private void setPartialProperties(boolean enabled)
    {
        logger.info("Partial properties is enabled? {}", enabled);

        if(enabled)
        {
            fovElement.enable();
            visibleDistanceElement.enable();
            coneEnabled.enable();
        }
        else
        {
            fovElement.disable();
            visibleDistanceElement.disable();
            coneEnabled.disable();
        }
    }

    private DropDown<VisionAlgorithm> getVisionAlgorithm()
    {
        return visionAlgorithmDropDown;
    }

    private Element getVisibleDistance()
    {
        return visibleDistanceElement;
    }

    private Element getFov()
    {
        return fovElement;
    }

    private CheckBox getConeEnabled()
    {
        return coneEnabled;
    }

    private CheckBox getAuditoryEnabled()
    {
        return auditoryEnabled;
    }

    private CheckBox getOlfactoryEnabled()
    {
        return olfactoryEnabled;
    }

    private TextField getAgentRange()
    {
        return agentRangeTextField;
    }

    private CheckBox getApplyAllEnabled()
    {
        return applyAllEnabled;
    }

    private String getCurrentTextField()
    {
        return currentTextField;
    }

    private void setAgentRangeTextField(String string)
    {
        getAgentRange().setText(string);
        currentTextField = string;
    }
}
