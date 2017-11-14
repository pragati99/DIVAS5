package edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.envObjModification;

import org.bushe.swing.event.EventTopicSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Quaternion;

import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.elements.Element;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerController;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.controls.spinner.SpinnerDefinition;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.AbstractPropertyDialogController;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;

/**
 * The EnvObjOptionsDialogController contains all the events that the EnvObjOptionsDialog element generates.
 */
public class EnvObjPropertyDialogController extends AbstractPropertyDialogController<EnvObjectVO>
{
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(EnvObjPropertyDialogController.class);

    private EnvObjectState      envObject;

    private Element             positionXSpinner;
    private Element             positionYSpinner;
    private Element             positionZSpinner;

    private Element             scaleXSpinner;
    private Element             scaleYSpinner;
    private Element             scaleZSpinner;

    private Element             rotationXSpinner;
    private Element             rotationYSpinner;
    private Element             rotationZSpinner;

    private Element             deleteButton;

    /**
     * Constructs a new environment object properties dialog controller
     */
    public EnvObjPropertyDialogController()
    {}

    @Override
    public void init()
    {
        if(entityVO != null)
        {
            this.envObject = entityVO.getState();
            super.init();
        }
    }

    @Override
    public void bindNiftyElements()
    {
        setupButtons();
        setupSpinners();
    }

    private void setupButtons()
    {
        deleteButton = getElement(EnvObjPropertyDialogDefinition.DELETE_BUTTON);
    }

    private void setupSpinners()
    {
        SpinnerDefinition.labelSize = "10px";

        positionXSpinner = createSpinnerControl("#positionXSpinner", "X", .5f, 1000f, -1000f, envObject.getPosition().getX(), "", EnvObjPropertyDialogDefinition.POSITION_X, true);
        positionYSpinner = createSpinnerControl("#positionYSpinner", "Y", .5f, 1000f, -1000f, envObject.getPosition().getY(), "", EnvObjPropertyDialogDefinition.POSITION_Y, false);
        positionZSpinner = createSpinnerControl("#positionZSpinner", "Z", .5f, 1000f, -1000f, envObject.getPosition().getZ(), "", EnvObjPropertyDialogDefinition.POSITION_Z, true);

        scaleXSpinner = createSpinnerControl("#scaleXSpinner", "X", 1.0f, 200f, .0001f, envObject.getScale().getX(), "", EnvObjPropertyDialogDefinition.SCALE_X, true);
        scaleYSpinner = createSpinnerControl("#scaleYSpinner", "Y", 1.0f, 200f, .0001f, envObject.getScale().getY(), "", EnvObjPropertyDialogDefinition.SCALE_Y, true);
        scaleZSpinner = createSpinnerControl("#scaleZSpinner", "Z", 1.0f, 200f, .0001f, envObject.getScale().getZ(), "", EnvObjPropertyDialogDefinition.SCALE_Z, true);

        rotationXSpinner = createSpinnerControl("#rotationXSpinner", "X", .1f, 1.0f, 0f, envObject.getRotation().getX(), "", EnvObjPropertyDialogDefinition.ROTATION_X, true);
        rotationYSpinner = createSpinnerControl("#rotationYSpinner", "Y", .1f, 1.0f, 0f, envObject.getRotation().getY(), "", EnvObjPropertyDialogDefinition.ROTATION_Y, true);
        rotationZSpinner = createSpinnerControl("#rotationZSpinner", "Z", .1f, 1.0f, 0f, envObject.getRotation().getZ(), "", EnvObjPropertyDialogDefinition.ROTATION_Z, true);
    }

    private void updateSpinners()
    {
        positionXSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getPosition().getX());
        positionYSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getPosition().getY());
        positionZSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getPosition().getZ());
        
        scaleXSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getScale().getX());
        scaleYSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getScale().getY());
        scaleZSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getScale().getZ());
        
        rotationXSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getRotation().getX());
        rotationYSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getRotation().getY());
        rotationZSpinner.getControl(SpinnerController.class).setCurrentValue(envObject.getRotation().getZ());
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
        nifty.subscribe(screen, getDeleteButton().getId(), ButtonClickedEvent.class, new EventTopicSubscriber<ButtonClickedEvent>()
        {
            @Override
            public void onEvent(final String id, final ButtonClickedEvent event)
            {
                simCommander.destroyEnvObject(envObject);
            }
        });

        /*
         * Subscriptions for Spinner Controls
         */
        nifty.subscribe(screen, getElementId(positionXSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getPosition().x = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }

        });

        nifty.subscribe(screen, getElementId(positionYSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getPosition().y = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(positionZSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getPosition().z = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(scaleXSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getScale().x = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(scaleYSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getScale().y = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(scaleZSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    envObject.getScale().z = Float.valueOf(event.getText());
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });

        nifty.subscribe(screen, getElementId(rotationXSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    Float x = Float.valueOf(event.getText());
                    Float y = envObject.getRotation().getY();
                    Float z = envObject.getRotation().getZ();
                    Float w = envObject.getRotation().getW();
                    envObject.setRotation(new Quaternion(x, y, z, w));
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(rotationYSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    Float x = envObject.getRotation().getX();
                    Float y = Float.valueOf(event.getText());
                    Float z = envObject.getRotation().getZ();
                    Float w = envObject.getRotation().getW();
                    envObject.setRotation(new Quaternion(x, y, z, w));
                    simCommander.sendStateUpdate(envObject);
                }
                catch(NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        });
        nifty.subscribe(screen, getElementId(rotationZSpinner), TextFieldChangedEvent.class, new EventTopicSubscriber<TextFieldChangedEvent>()
        {
            @Override
            public void onEvent(final String id, final TextFieldChangedEvent event)
            {
                try
                {
                    Float x = envObject.getRotation().getX();
                    Float y = envObject.getRotation().getY();
                    Float z = Float.valueOf(event.getText());
                    Float w = envObject.getRotation().getW();
                    envObject.setRotation(new Quaternion(x, y, z, w));
                    simCommander.sendStateUpdate(envObject);
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
        envObject = entityVO.getState();

        if(app.getSimulatingAppState().isEditing())
        {
            updateSpinners();
        }
    }

    /**
     * Gets the delete button for removing the environment object from the simulation.
     * 
     * @return the delete button
     */
    public Element getDeleteButton()
    {
        return deleteButton;
    }

    @Override
    public void setEntity(EnvObjectVO entity)
    {
        super.entityVO = entity;
    }
}
