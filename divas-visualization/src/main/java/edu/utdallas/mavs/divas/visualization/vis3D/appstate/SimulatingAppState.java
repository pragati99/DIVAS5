package edu.utdallas.mavs.divas.visualization.vis3D.appstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.client.dto.GeneralAgentProperties;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;
import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMapping;
import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMode;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.voModification.envObjModification.EnvObjPropertyDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.ContextSelectionPicker;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.Picker;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.SelectionPicker;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.VisualizerTask;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.VisualSpectator;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.SnapAndGlueHelper;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;

/**
 * This class represents the main application state of the 3D visualizer.
 * <p>
 * It allows users to interact with the visualized simulation, by selecting visualized objects (VOs) and triggering events to the simulation. It also allows users to add agents to simulation.
 * 
 * @param <AP> the application type
 */
public abstract class SimulatingAppState<AP extends BaseApplication<?, ?>> extends AbstractAppState
{
    private final static Logger             logger          = LoggerFactory.getLogger(SimulatingAppState.class);

    /**
     * The visualizer application
     */
    protected AP                            app;

    /**
     * Listener for mouse left button clicks. Handles selection of VOs.
     */
    protected static ActionListener         pickListener;

    /**
     * Listener for mouse scrolling wheel. Responsible for changing this application state mode of operation.
     */
    protected static ActionListener         modeListener;

    /**
     * Listener for mouse dragging. Used for dragging objects on the screen.
     */
    protected static AnalogListener         dragListener;

    /**
     * Listener for mouse cursors.
     */
    protected static AnalogListener         cursorListener;

    /**
     * Picker for selection of VOs
     */
    protected static SelectionPicker        selectionPicker;

    /**
     * Picker for selection of VOs
     */
    protected static ContextSelectionPicker contextSelectionPicker;

    /**
     * Picker for VOs being modified
     */
    protected Picker                        dragPicker;

    /**
     * Picker for adding agents and triggering events to specific points in the visualized environment
     */
    protected static Picker                 addPicker;

    /**
     * Picker for changing cursor image
     */
    protected Picker                        cursorPicker;

    /**
     * Mode of operation of the application state. Default is <code>SELECTION</code>
     */
    protected InputMode                     mode            = InputMode.SELECTION;

    /**
     * The position of the mouse cursor
     */
    protected Vector2f                      cursorPosition;

    /**
     * A list of context selection observers
     */
    protected List<Observer>                contextObservers;

    /**
     * The context selection click position
     */
    protected Vector3f                      contextClickCursorPosition;

    /**
     * Mouse left button pressed flag
     */
    protected boolean                       isPressed       = false;

    /**
     * Flag that indicates if the user is holding/dragging a VO
     */
    protected boolean                       isEditing       = false;

    /**
     * Snapping to grid flag
     */
    protected boolean                       isSnapToGridOn  = false;

    /**
     * Copy object flag
     */
    protected boolean                       isCopyRequested = false;

    /**
     * Mouse dragging flag
     */
    protected boolean                       isDragAndDropOn = false;

    /**
     * The VO currently being dragged
     */
    private EnvObjectVO                     draggingVO      = null;

    /**
     * The environment object VO to be copied
     */
    private EnvObjectVO                     copyingVO       = null;

    /**
     * Indicates if an object is being copied by dragging
     */
    private boolean                         isCopying       = false;

    /**
     * Indicates the editing mode of the application
     */
    private EditingMode                     editingMode     = EditingMode.TRANSLATION;

    /**
     * The current selection for adding agents
     */
    private AgentState                      agentTemplate   = null;

    /**
     * The current selection for adding objects
     */
    private EnvObjectState                  envObjTemplate  = null;

    /**
     * Tracking light for tracking agents on the screen
     */
    private AmbientLight                    agentTrackingLight;

    /**
     * Tracking light for tracking objects on the screen
     */
    private AmbientLight                    objectTrackingLight;

    /**
     * Indicates which axis should be scaled
     */
    public enum EditingMode
    {
        TRANSLATION, SCALE_X, SCALE_Y, SCALE_Z
    }

    /**
     * Triggers an event at the given position
     * 
     * @param position the position to trigger the event
     */
    protected abstract void triggerEvent(Vector3f position);

    /**
     * Get the event input mode given a resource string
     * 
     * @param inputMode the input mode string from the vis resources
     * @return the associated input mode
     */
    protected abstract InputMode getMappedEventInputMode(String inputMode);

    @Override
    public void update(float tpf)
    {}

    @Override
    public void stateDetached(AppStateManager stateManager)
    {
        super.stateDetached(stateManager);
        removeMappings();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        setup(app);
    }

    /**
     * Prepares this application state
     * 
     * @param app
     *        the 3D visualizer application
     */
    @SuppressWarnings("unchecked")
    protected void setup(Application app)
    {
        this.app = (AP) app;

        contextObservers = new ArrayList<>();

        agentTrackingLight = VisToolbox.createAmbientLight(ColorRGBA.Pink.mult(8f));
        objectTrackingLight = VisToolbox.createAmbientLight(ColorRGBA.White.mult(2f));

        selectionPicker = new SelectionPicker(agentTrackingLight, objectTrackingLight);
        contextSelectionPicker = new ContextSelectionPicker(agentTrackingLight, objectTrackingLight);

        dragPicker = new Picker();
        addPicker = new Picker();
        cursorPicker = new Picker();

        setupInput();
        setMode(InputMode.SELECTION);
        setupContextObservers();
    }

    /**
     * Adds context selection observers to the appropriate context ready objects in the visualization
     */
    protected void setupContextObservers()
    {
        contextObservers.add(new EnvObjPropertyDialog(NiftyScreen.windowLayerElement));
    }

    /**
     * Prepares the input listeners for this application state.
     */
    protected void setupInput()
    {
        // for changing modes
        modeListener = new ActionListener()
        {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf)
            {
                handleChangeModeAction(name);
            }
        };

        // for object picking
        pickListener = new ActionListener()
        {
            private NamedSemaphore semaphore = new NamedSemaphore(1);

            @Override
            public void onAction(String name, boolean keyPressed, float tpf)
            {
                if(mode == InputMode.SELECTION)
                {
                    if(name.equals(InputMapping.CONTEXT.getKey()) && semaphore.tryAcquire(InputMapping.CONTEXT.getKey()))
                    {
                        handleContextAction(keyPressed);
                        semaphore.release();
                    }
                    if(name.equals(InputMapping.SNAP.getKey()))
                    {
                        isSnapToGridOn = keyPressed;
                        dragPicker.pick();
                    }
                    if(name.equals(InputMapping.COPY.getKey()) && semaphore.tryAcquire(InputMapping.COPY.getKey()))
                    {
                        isCopyRequested = keyPressed;
                        handleCursorPositionChange();
                        if(!keyPressed)
                        {
                            semaphore.release();
                        }
                    }
                    if(name.equals(InputMapping.DELETE.getKey()) && semaphore.tryAcquire(InputMapping.DELETE.getKey()))
                    {
                        handleDeleteAction(keyPressed);
                        if(!keyPressed)
                        {
                            semaphore.release();
                        }
                    }
                    if(name.equals(InputMapping.SCALE_X.getKey()) && semaphore.tryAcquire(InputMapping.SCALE.getKey()))
                    {
                        editingMode = keyPressed ? EditingMode.SCALE_X : EditingMode.TRANSLATION;
                        handleCursorPositionChange();
                        if(!keyPressed)
                        {
                            semaphore.release();
                        }
                    }
                    if(name.equals(InputMapping.SCALE_Y.getKey()) && semaphore.tryAcquire(InputMapping.SCALE.getKey()))
                    {
                        editingMode = keyPressed ? EditingMode.SCALE_Y : EditingMode.TRANSLATION;
                        handleCursorPositionChange();
                        if(!keyPressed)
                        {
                            semaphore.release();
                        }
                    }
                    if(name.equals(InputMapping.SCALE_Z.getKey()) && semaphore.tryAcquire(InputMapping.SCALE.getKey()))
                    {
                        editingMode = keyPressed ? EditingMode.SCALE_Z : EditingMode.TRANSLATION;
                        handleCursorPositionChange();
                        if(!keyPressed)
                        {
                            semaphore.release();
                        }
                    }
                }
                if(name.equals(InputMapping.SHOOT.getKey()))
                {
                    handleSelectionAction(keyPressed);
                }
            }

            class NamedSemaphore extends Semaphore
            {
                private static final long serialVersionUID = 1L;
                String                    name             = null;

                public NamedSemaphore(int permits)
                {
                    super(permits);
                }

                public boolean tryAcquire(String name)
                {
                    boolean acquired = super.tryAcquire() || this.name.equals(name);
                    if(acquired)
                        this.name = name;
                    return acquired;
                }

                @Override
                public void release()
                {
                    this.name = null;
                    super.release();
                }
            }

        };

        // for dragging objects on edit mode
        dragListener = new AnalogListener()
        {
            private Vector3f scale              = Vector3f.UNIT_XYZ.clone();
            private Vector3f center             = Vector3f.UNIT_XYZ.clone();
            private Vector2f previousCoordinate = Vector2f.ZERO.clone();

            @Override
            public void onAnalog(String name, float value, float tpf)
            {
                if(name.equals(InputMapping.SHOOT.getKey()))
                {
                    if(isEditing)
                    {
                        handleVOEditing(scale, center, previousCoordinate);
                    }
                    else if(isCopying)
                    {
                        handleVOCopying(scale, center);
                    }
                }
            }
        };

        // for dragging objects on edit mode
        cursorListener = new AnalogListener()
        {
            @Override
            public void onAnalog(String name, float value, float tpf)
            {
                if(mode == InputMode.SELECTION)
                {
                    handleCursorPositionChange();
                }
            }
        };

        addMappings();
    }

    /**
     * Handles change in cursor position (responsible for changing the cursor image depending on the state of the applicaiton).
     */
    protected void handleCursorPositionChange()
    {
        cursorPicker.pick();

        if(isEditing && editingMode == EditingMode.TRANSLATION)
        {
            app.setCursor(CursorType.MOVE);
            return;
        }

        if(editingMode != EditingMode.TRANSLATION)
        {
            if(editingMode == EditingMode.SCALE_X)
                app.setCursor(CursorType.SCALE_X);
            else if(editingMode == EditingMode.SCALE_Y)
                app.setCursor(CursorType.SCALE_Y);
            else if(editingMode == EditingMode.SCALE_Z)
                app.setCursor(CursorType.SCALE_Z);
            return;
        }

        if(isCopying)
        {
            app.setCursor(CursorType.COPY);
            return;
        }

        if(isDragAndDropOn && cursorPicker.hasSelection() && cursorPicker.isSelectedEnvObject())
        {
            EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(cursorPicker.getCurrentlySelectedID());
            if(envObjVO != null && !envObjVO.isLocked())
            {
                if(isCopyRequested)
                {
                    app.setCursor(CursorType.COPY);
                }
                else
                {
                    if(editingMode == EditingMode.TRANSLATION)
                        app.setCursor(CursorType.MOVE);
                    else if(editingMode == EditingMode.SCALE_X)
                        app.setCursor(CursorType.SCALE_X);
                    else if(editingMode == EditingMode.SCALE_Y)
                        app.setCursor(CursorType.SCALE_Y);
                    else if(editingMode == EditingMode.SCALE_Z)
                        app.setCursor(CursorType.SCALE_Z);
                }
            }
            else
            {
                app.setCursor(CursorType.ARROW);
            }
        }
        else
        {
            app.setCursor(CursorType.ARROW);
        }
    }

    /**
     * Handles VO dragging on the screen. This method is only invoked if the flag <code>isDragging</code> evaluates to true.
     * 
     * @param scale
     *        the saved scale of the object
     * @param center
     *        the saved center of the object
     * @param previousCoordinate
     */
    protected void handleVOEditing(Vector3f scale, Vector3f center, Vector2f previousCoordinate)
    {
        if(editingMode == EditingMode.TRANSLATION)
        {
            dragPicker.pick();
            if(dragPicker.hasSelection())
            {
                if(selectionPicker.isSelectedEnvObject())
                {
                    EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(selectionPicker.getCurrentlySelectedID());
                    EnvObjectState envObj = (envObjVO != null) ? envObjVO.getState() : null;
                    if(envObjVO != null && !envObjVO.isLocked())
                    {
                        if(isSnapToGridOn)
                        {
                            center = dragPicker.getCurrentlySelected().getContactPoint().setY(envObj.getPosition().getY());
                            scale = envObj.getScale();

                            Vector3f trans = SnapAndGlueHelper.calculateSnapTranslation(center, scale);
                            envObj.setPosition(trans);

                            logger.debug("Center " + center);
                            logger.debug("New Trans: " + trans);

                        }
                        else
                        {
                            envObj.setPosition(dragPicker.getCurrentlySelected().getContactPoint().setY(envObj.getPosition().getY()));
                        }
                        envObjVO.updateHUD();
                        envObjVO.enqueueStateUpdate(VisualSpectator.getCycles());
                    }
                }
            }
        }
        else
        {
            float scalingStep = 1.0f;

            if(editingMode == EditingMode.SCALE_X)
            {
                if(selectionPicker.isSelectedEnvObject())
                {
                    EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(selectionPicker.getCurrentlySelectedID());
                    if(envObjVO != null)
                    {
                        Vector3f localScale = envObjVO.getLocalScale();
                        EnvObjectState envObj = envObjVO.getState();
                        float scl = localScale.getX();
                        float y = app.getInputManager().getCursorPosition().y;
                        if(previousCoordinate.y < y)
                        {
                            scl = scl + scalingStep;
                        }
                        else if(previousCoordinate.y > y)
                        {
                            scl = scl - scalingStep;
                            if(scl <= 0.0)
                            {
                                scl = 0.10f;
                            }
                        }
                        previousCoordinate.y = y;
                        envObj.getScale().setX(scl);
                        envObj.getScale().setY(localScale.getY());
                        envObj.getScale().setZ(localScale.getZ());
                        envObjVO.updateHUD();
                        envObjVO.enqueueStateUpdate(VisualSpectator.getCycles());
                    }
                }
            }
            else if(editingMode == EditingMode.SCALE_Y)
            {

                if(selectionPicker.isSelectedEnvObject())
                {
                    EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(selectionPicker.getCurrentlySelectedID());
                    if(envObjVO != null)
                    {
                        Vector3f localScale = envObjVO.getLocalScale();
                        EnvObjectState envObj = envObjVO.getState();
                        float scl = localScale.getY();
                        float y = app.getInputManager().getCursorPosition().y;
                        if(previousCoordinate.y < y)
                        {
                            scl = scl + scalingStep;

                        }
                        else if(previousCoordinate.y > y)
                        {
                            scl = scl - scalingStep;
                            if(scl <= 0.0)
                            {
                                scl = 0.10f;
                            }
                        }
                        previousCoordinate.y = y;
                        envObj.getScale().setX(localScale.getX());
                        envObj.getScale().setY(scl);
                        envObj.getScale().setZ(localScale.getZ());
                        envObjVO.updateHUD();
                        envObjVO.enqueueStateUpdate(VisualSpectator.getCycles());
                    }
                }
            }
            else if(editingMode == EditingMode.SCALE_Z)
            {

                if(selectionPicker.isSelectedEnvObject())
                {
                    EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(selectionPicker.getCurrentlySelectedID());
                    if(envObjVO != null)
                    {
                        Vector3f localScale = envObjVO.getLocalScale();
                        EnvObjectState envObj = envObjVO.getState();
                        float scl = localScale.getZ();
                        float y = app.getInputManager().getCursorPosition().y;
                        if(previousCoordinate.y < y)
                        {
                            scl = scl + scalingStep;

                        }
                        else if(previousCoordinate.y > y)
                        {
                            scl = scl - scalingStep;
                            if(scl <= 0.0)
                            {
                                scl = 0.10f;
                            }
                        }
                        previousCoordinate.y = y;
                        envObj.getScale().setX(localScale.getX());
                        envObj.getScale().setY(localScale.getY());
                        envObj.getScale().setZ(scl);
                        envObjVO.updateHUD();
                        envObjVO.enqueueStateUpdate(VisualSpectator.getCycles());
                    }
                }
            }
        }

    }

    /**
     * Handles VO copying on the screen. This method is only invoked if the flag <code>isDragging</code> evaluates to true.
     * 
     * @param scale
     *        the saved scale of the object
     * @param center
     *        the saved center of the object
     */
    protected void handleVOCopying(Vector3f scale, Vector3f center)
    {
        dragPicker.pick();
        if(dragPicker.hasSelection())
        {
            if(selectionPicker.isSelectedEnvObject())
            {
                final EnvObjectVO envObjVO = copyingVO;
                EnvObjectState envObj = (envObjVO != null) ? envObjVO.getState() : null;
                if(envObjVO != null && !envObjVO.isLocked())
                {
                    if(isSnapToGridOn)
                    {
                        center = dragPicker.getCurrentlySelected().getContactPoint();
                        scale = envObj.getScale();

                        Vector3f trans = SnapAndGlueHelper.calculateSnapTranslation(center, scale);
                        envObj.setPosition(trans);

                        logger.debug("Center " + center);
                        logger.debug("New Trans: " + trans);

                    }
                    else
                    {
                        envObj.setPosition(dragPicker.getCurrentlySelected().getContactPoint());
                    }
                    envObjVO.updateHUD();
                    envObjVO.enqueueStateUpdate(VisualSpectator.getCycles());
                }
            }
        }
    }

    /**
     * Handle application modes change.
     * 
     * @param name
     */
    protected void handleChangeModeAction(String name)
    {
        if(name.equals(InputMapping.SEL.getKey()))
        {
            removeSelections();
            setMode(InputMode.SELECTION);
        }
        else
        {
            int newModeNum = mode.getCode();
            if(name.equals(InputMapping.MODE_UP.getKey()))
            {
                newModeNum = (newModeNum + 1) % InputMode.size();

            }
            if(name.equals(InputMapping.MODE_DOWN.getKey()))
            {
                newModeNum = (newModeNum - 1) % InputMode.size();
                if(newModeNum < 0)
                {
                    newModeNum += InputMode.size();
                }
            }
            setMode(InputMode.getInputMode(newModeNum));
        }
    }

    /**
     * Remove all VO selections from screen
     */
    public void removeSelections()
    {
        for(AgentVO<?> vo : VisualSpectator.getPlayGround().findAllAgents())
        {
            vo.setSelected(false);
            vo.setContextSelected(false);
        }
        for(EnvObjectVO vo : VisualSpectator.getPlayGround().findAllEnvObjects())
        {
            vo.setSelected(false);
            vo.setContextSelected(false);
        }
    }

    /**
     * Handles selection actions according to the current state of the application state.
     * 
     * @param keyPressed
     */
    protected void handleSelectionAction(boolean keyPressed)
    {
        isPressed = keyPressed;

        if(keyPressed)
        {
            cursorPosition = app.getInputManager().getCursorPosition();

            if(mode == InputMode.SELECTION)
            {
                selectionPicker.pick();

                if(isDragAndDropOn)
                {
                    if(selectionPicker.isSelectedEnvObject())
                    {
                        EnvObjectVO pickedObject = VisualSpectator.getPlayGround().findEnvObject(selectionPicker.getCurrentlySelectedID());
                        if(isCopyRequested)
                        {
                            copyingVO = (pickedObject != null) ? pickedObject.copy() : null;
                            if(copyingVO != null && !copyingVO.isLocked())
                            {
                                isCopying = true;
                                Callable<Object> task = new Callable<Object>()
                                {
                                    @Override
                                    public Object call() throws Exception
                                    {
                                        Visualizer3DApplication.getInstance().getApp().getStateManager().getState(DivasAppState.class).getEditRootNode().attachChild(copyingVO);
                                        return null;
                                    }
                                };
                                Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, VisualSpectator.getCycles()));
                                copyingVO.setModifying(true);
                            }
                        }
                        else
                        {
                            draggingVO = pickedObject;
                            if(draggingVO != null && !draggingVO.isLocked())
                            {
                                isEditing = true;
                                Callable<Object> task = new Callable<Object>()
                                {
                                    @Override
                                    public Object call() throws Exception
                                    {
                                        app.getStateManager().getState(DivasAppState.class).getVisRootNode().detachChild(draggingVO);
                                        app.getStateManager().getState(DivasAppState.class).getEditRootNode().attachChild(draggingVO);
                                        return null;
                                    }
                                };
                                Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, VisualSpectator.getCycles()));
                                draggingVO.setModifying(true);
                            }
                        }
                    }
                }
            }
            else if(mode == InputMode.ADD_AGENT)
            {
                addAgent();
            }
            else if(mode == InputMode.ADD_OBJECT)
            {
                addEnvObject();
            }
            else
            {
                triggerEvent();
            }
        }
        else
        {
            if(isEditing == true && draggingVO != null)
            {
                isEditing = false;
                final EnvObjectVO envObjVO = draggingVO;
                Callable<Object> task = new Callable<Object>()
                {
                    @Override
                    public Object call() throws Exception
                    {
                        app.getStateManager().getState(DivasAppState.class).getEditRootNode().detachChild(envObjVO);
                        app.getStateManager().getState(DivasAppState.class).getVisRootNode().attachChild(envObjVO);
                        return null;
                    }
                };
                Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, VisualSpectator.getCycles()));
                Visualizer3DApplication.getInstance().getSimCommander().sendStateUpdate(envObjVO.getState());
                draggingVO.setModifying(false);
            }

            if(isCopying == true && copyingVO != null)
            {
                isCopying = false;
                EnvObjectVO envObjVO = copyingVO;
                Callable<Object> task = new Callable<Object>()
                {
                    @Override
                    public Object call() throws Exception
                    {
                        app.getStateManager().getState(DivasAppState.class).getEditRootNode().detachChild(copyingVO);
                        return null;
                    }
                };
                Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, VisualSpectator.getCycles()));
                Visualizer3DApplication.getInstance().getSimCommander().createEnvObject(envObjVO.getState());
                copyingVO.setModifying(false);
            }
        }
    }

    /**
     * Triggers an event on the screen
     */
    protected void triggerEvent()
    {
        Vector3f addPickerPosition = getAddPosition();
        triggerEvent(addPickerPosition);
    }

    /**
     * Handles contextual clicks on the screen. Used to display details/option on VOs displayed on a screen.
     * 
     * @param keyPressed
     */
    protected void handleContextAction(boolean keyPressed)
    {
        Vector2f cursorPosition = app.getInputManager().getCursorPosition();
        Vector3f worldCoordinates = app.getCamera().getWorldCoordinates(new Vector2f(cursorPosition.x, cursorPosition.y), 0f);

        if(keyPressed)
        {
            contextClickCursorPosition = worldCoordinates;
        }
        else if(contextClickCursorPosition.equals(worldCoordinates))
        {
            contextSelectionPicker.pick();
            if(contextSelectionPicker.hasSelection())
            {
                if(contextSelectionPicker.isSelectedEnvObject())
                {
                    EnvObjectVO envObjVO = VisualSpectator.getPlayGround().findEnvObject(contextSelectionPicker.getCurrentlySelectedID());
                    notifyContextSelectionObservers(envObjVO);
                }
                else if(contextSelectionPicker.isSelectedAgent())
                {
                    AgentVO<?> agentVO = VisualSpectator.getPlayGround().findAgent(contextSelectionPicker.getCurrentlySelectedID());
                    notifyContextSelectionObservers(agentVO);
                }
            }
        }
    }

    /**
     * Handles delete clicks on the screen.
     * 
     * @param keyPressed
     */
    protected void handleDeleteAction(boolean keyPressed)
    {
        if(keyPressed)
        {
            for(EnvObjectVO object : VisualSpectator.getPlayGround().findAllEnvObjects())
            {
                if(object.isSelected() && !object.isLocked())
                {
                    object.setContextSelected(false);
                    object.setSelected(false);
                    Visualizer3DApplication.getInstance().getSimCommander().destroyEnvObject(object.getState());
                }
            }
            for(AgentVO<?> agent : VisualSpectator.getPlayGround().findAllAgents())
            {
                if(agent.isSelected())
                {
                    agent.setContextSelected(false);
                    agent.setSelected(false);
                    Visualizer3DApplication.getInstance().getSimCommander().destroyAgent(agent.getState());
                }
            }
        }
    }

    /**
     * Adds the currently selected agent from template to the simulation. No agent is added if no template has been previously selected.
     */
    public void addAgent()
    {
        if(agentTemplate != null)
        {
            Vector3f addPickerPosition = getAddPosition();
            addAgent(agentTemplate, addPickerPosition);
        }
        else
        {
            Vector3f addPickerPosition = getAddPosition();
            // app.displayMessage("Please select an agent from the template bar");
            addAgent(agentTemplate, addPickerPosition); // adding default agent (less confusing)
        }
    }

    /**
     * Adds the currently selected object from template to the simulation. No object is added if no template has been previously selected.
     */
    public void addEnvObject()
    {
        if(envObjTemplate != null)
        {
            Vector3f addPickerPosition = getAddPosition();
            addEnvObject(envObjTemplate, addPickerPosition);
        }
    }

    /**
     * Adds an agent to the specified location in the visualized environment.
     * 
     * @param state
     *        the agent's state
     * @param contactPoint
     *        the location pointed by the user
     */
    protected void addAgent(AgentState state, Vector3f contactPoint)
    {
        // TODO: change simulation and facade to receive custom agent
        logger.debug("Adding agent @ location: " + contactPoint);
        Visualizer3DApplication.getInstance().getSimCommander().createAgent(state, contactPoint);
    }

    /**
     * Adds an environment object to the specified location in the visualized environment.
     * 
     * @param state
     *        the object to be attached
     * @param contactPoint
     *        the location pointed by the user
     */
    protected void addEnvObject(EnvObjectState state, Vector3f contactPoint)
    {
        state.setPosition(contactPoint);
        Visualizer3DApplication.getInstance().getSimCommander().createEnvObject(state);
        logger.info("Adding environment object @ location: " + contactPoint);
    }

    /**
     * Gets the current cursor position for adding VOs to the screen
     * 
     * @return the adding position
     */
    protected Vector3f getAddPosition()
    {
        addPicker.pick();
        Vector3f addPickerPosition = null;
        if(addPicker.getCurrentlySelected() != null)
        {
            addPickerPosition = addPicker.getCurrentlySelected().getContactPoint();
        }
        else
        {
            Vector2f click2d = Visualizer3DApplication.getInstance().getInputManager().getCursorPosition();
            Vector3f click3d = Visualizer3DApplication.getInstance().getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            addPickerPosition = new Vector3f(click3d.getX(), 0f, click3d.getZ());
        }
        return addPickerPosition;
    }

    /**
     * Buffers an agent template to be added to the simulation
     * 
     * @param addAgentSelection
     */
    public void setAddAgentSelection(AgentState addAgentSelection)
    {
        this.agentTemplate = addAgentSelection;
        setMode(InputMode.ADD_AGENT);
    }

    /**
     * Buffers an environment object's template to be added to the simulation
     * 
     * @param addEnvObjSelection
     */
    public void setAddEnvObjSelection(EnvObjectState addEnvObjSelection)
    {
        this.envObjTemplate = addEnvObjSelection;
        setMode(InputMode.ADD_OBJECT);
    }

    /**
     * Buffers an event to be triggered in the simulation
     * 
     * @param triggerEventSelection
     */
    public void setTriggerEventSelection(String triggerEventSelection)
    {
        setMode(getMappedEventInputMode(triggerEventSelection));
    }

    /**
     * Applies the specified agent properties for all agents in the simulation
     * 
     * @param properties
     *        the properties to be applied
     */
    public void applyAgentPropertiesForAll(GeneralAgentProperties properties)
    {
        Collection<AgentVO<?>> agents = VisualSpectator.getPlayGround().findAllAgents();
        for(AgentVO<?> vo : agents)
        {
            applyAgentPropertiesForAgent(vo.getState().getID(), properties);
        }
    }

    /**
     * Applies the specified agent properties for all agents in the specied range in the simulation
     * 
     * @param startID
     *        start ID of the range
     * @param endID
     *        endID of the range
     * @param properties
     *        the properties to be applied
     */
    public void applyAgentPropertiesForRange(int startID, int endID, GeneralAgentProperties properties)
    {
        for(int ID = startID; ID <= endID; ID++)
        {
            applyAgentPropertiesForAgent(ID, properties);
        }
    }

    /**
     * Applies the specified agent properties for an specific agent
     * 
     * @param ID
     *        the agent's ID
     * @param properties
     *        the properties to be applied
     */
    public void applyAgentPropertiesForAgent(int ID, GeneralAgentProperties properties)
    {
        AgentVO<?> agent = VisualSpectator.getPlayGround().findAgent(ID);
        if(agent != null)
        {
            Visualizer3DApplication.getInstance().getSimCommander().sendAgentStateUpdate(agent.getState(), properties);
            agent.setVisionCone(properties.isVisionConeEnabled());
        }
    }

    /**
     * Tracks agents in a specified range in the visualizer
     * 
     * @param startID
     *        start ID of the range
     * @param endID
     *        endID of the range
     */
    public void trackAgentsInRange(int startID, int endID)
    {
        for(int ID = startID; ID <= endID; ID++)
        {
            trackAgent(ID);
        }
    }

    /**
     * Tracks a specific agent in the visualizer
     * 
     * @param ID
     *        the agent's ID
     * @param properties
     *        the properties to be applied
     */
    public void trackAgent(int ID)
    {
        logger.debug("Tracking agent by id {}", ID);
        AgentVO<?> agent = VisualSpectator.getPlayGround().findAgent(ID);
        if(agent != null)
        {
            agent.setTrackingLight(agentTrackingLight);
            agent.setSelected(true);
        }
    }

    /**
     * Remove tracking from all agents in the visualizer
     */
    public void untrackAllAgents()
    {
        for(AgentVO<?> vo : VisualSpectator.getPlayGround().findAllAgents())
        {
            vo.setSelected(false);
        }
    }

    /**
     * Configure user input mappings
     */
    protected void addMappings()
    {
        app.getInputManager().addMapping(InputMapping.MODE_UP.getKey(), new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        app.getInputManager().addListener(modeListener, InputMapping.MODE_UP.getKey());

        app.getInputManager().addMapping(InputMapping.MODE_DOWN.getKey(), new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        app.getInputManager().addListener(modeListener, InputMapping.MODE_DOWN.getKey());

        app.getInputManager().addMapping(InputMapping.SEL.getKey(), new KeyTrigger(KeyInput.KEY_ESCAPE));
        app.getInputManager().addListener(modeListener, InputMapping.SEL.getKey());

        app.getInputManager().addMapping(InputMapping.SHOOT.getKey(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(pickListener, InputMapping.SHOOT.getKey());
        app.getInputManager().addListener(dragListener, InputMapping.SHOOT.getKey());

        app.getInputManager().addMapping(InputMapping.SNAP.getKey(), new KeyTrigger(KeyInput.KEY_LMENU));
        app.getInputManager().addListener(pickListener, InputMapping.SNAP.getKey());

        app.getInputManager().addMapping(InputMapping.COPY.getKey(), new KeyTrigger(KeyInput.KEY_LCONTROL));
        app.getInputManager().addListener(pickListener, InputMapping.COPY.getKey());

        app.getInputManager().addMapping(InputMapping.DELETE.getKey(), new KeyTrigger(KeyInput.KEY_DELETE));
        app.getInputManager().addListener(pickListener, InputMapping.DELETE.getKey());

        app.getInputManager().addMapping(InputMapping.SCALE_X.getKey(), new KeyTrigger(KeyInput.KEY_F2));
        app.getInputManager().addMapping(InputMapping.SCALE_Y.getKey(), new KeyTrigger(KeyInput.KEY_F3));
        app.getInputManager().addMapping(InputMapping.SCALE_Z.getKey(), new KeyTrigger(KeyInput.KEY_F4));
        app.getInputManager().addListener(pickListener, InputMapping.SCALE_X.getKey(), InputMapping.SCALE_Y.getKey(), InputMapping.SCALE_Z.getKey());

        app.getInputManager().addMapping(InputMapping.CONTEXT.getKey(), new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(pickListener, InputMapping.CONTEXT.getKey());

        app.getInputManager().addMapping(InputMapping.MOUSE_LEFT.getKey(), new MouseAxisTrigger(MouseInput.AXIS_X, true));
        app.getInputManager().addMapping(InputMapping.MOUSE_RIGHT.getKey(), new MouseAxisTrigger(MouseInput.AXIS_X, false));
        app.getInputManager().addMapping(InputMapping.MOUSE_UP.getKey(), new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        app.getInputManager().addMapping(InputMapping.MOUSE_DOWN.getKey(), new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        app.getInputManager().addListener(cursorListener, InputMapping.MOUSE_LEFT.getKey(), InputMapping.MOUSE_RIGHT.getKey(), InputMapping.MOUSE_UP.getKey(), InputMapping.MOUSE_DOWN.getKey());
    }

    /**
     * Clean user input mappings
     */
    public void removeMappings()
    {
        app.getInputManager().deleteMapping(InputMapping.MODE_UP.getKey());
        app.getInputManager().deleteMapping(InputMapping.MODE_DOWN.getKey());
        app.getInputManager().deleteMapping(InputMapping.SHOOT.getKey());
        app.getInputManager().deleteMapping(InputMapping.CONTEXT.getKey());
        app.getInputManager().deleteMapping(InputMapping.SEL.getKey());
        app.getInputManager().deleteMapping(InputMapping.SNAP.getKey());
        app.getInputManager().deleteMapping(InputMapping.COPY.getKey());
        app.getInputManager().deleteMapping(InputMapping.DELETE.getKey());
        app.getInputManager().deleteMapping(InputMapping.MOUSE_LEFT.getKey());
        app.getInputManager().deleteMapping(InputMapping.MOUSE_RIGHT.getKey());
        app.getInputManager().deleteMapping(InputMapping.MOUSE_UP.getKey());
        app.getInputManager().deleteMapping(InputMapping.MOUSE_DOWN.getKey());
        app.getInputManager().deleteMapping(InputMapping.SCALE_X.getKey());
        app.getInputManager().deleteMapping(InputMapping.SCALE_Y.getKey());
        app.getInputManager().deleteMapping(InputMapping.SCALE_Z.getKey());

        app.getInputManager().removeListener(modeListener);
        app.getInputManager().removeListener(pickListener);
        app.getInputManager().removeListener(dragListener);
        app.getInputManager().removeListener(cursorListener);
    }

    /**
     * Changes mode of operation of this application state
     * 
     * @param inputMode
     *        the new mode of operation
     */
    public void setMode(InputMode inputMode)
    {
        logger.debug("setting mode: " + inputMode);
        app.displayMessage("Setting mode to " + inputMode);
        mode = inputMode;
        setCursor(mode);
    }

    /**
     * Sets the mouse cursor for the given input mode
     * 
     * @param inputMode the current simulation input mode
     */
    protected void setCursor(InputMode inputMode)
    {
        // change cursors in here (and anything else required when changing modes)
        if(inputMode.equals(InputMode.ADD_AGENT))
        {
            app.setCursor(CursorType.AGENT);
        }
        else if(inputMode.equals(InputMode.ADD_OBJECT))
        {
            app.setCursor(CursorType.OBJECT);
        }
        else if(inputMode.equals(InputMode.SELECTION))
        {
            app.setCursor(CursorType.ARROW);
        }
    }

    /**
     * Gets this application state selection picker
     * 
     * @return the selection picker
     */
    public static SelectionPicker getSelectionPicker()
    {
        return selectionPicker;
    }

    /**
     * Gets this application state context selection picker
     * 
     * @return the context selection picker
     */
    public static ContextSelectionPicker getContextSelectionPicker()
    {
        return contextSelectionPicker;
    }

    /**
     * Toggles the drag and drop state of this application state ON/OFF
     * 
     * @param isDragAndDropOn
     */
    public void toggleDragAndDropOnOff()
    {
        this.isDragAndDropOn = (this.isDragAndDropOn) ? false : true;
        if(!isDragAndDropOn)
            setMode(InputMode.SELECTION);
        app.displayMessage("Drag and Drop is " + ((isDragAndDropOn) ? "ON" : "OFF"));
    }

    /**
     * Notifies context selection observers
     * 
     * @param vo
     *        object to be passed to observers
     */
    protected void notifyContextSelectionObservers(Object vo)
    {
        for(Observer o : contextObservers)
        {
            o.update(null, vo);
        }
    }

    public boolean isEditing()
    {
        return isEditing;
    }
}
