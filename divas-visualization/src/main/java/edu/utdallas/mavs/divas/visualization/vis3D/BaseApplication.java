package edu.utdallas.mavs.divas.visualization.vis3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;
import edu.utdallas.mavs.divas.utils.StatsHelper;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.DivasAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.EnvironmentAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.FreeCameraAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.SimulatingAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;
import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMapping;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.HelpDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.MenuDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.CursorManager;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.HUDMessageArea;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.VisualizerTask;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.PlayGround;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.VisualSpectator;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.CellBoundsVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EventVO;

/**
 * This class describes the JMe3 application, responsible for the 3D visualization of DIVAs Simulations.
 * <p>
 * This class initializes all the main components of the 3D visualizer (renderer, audio, cameras, application states). The simulation updates are enqueued by the visualizer spectator thread and
 * consumed during the visualizer update loop. Adding or removal of Visualized Objects (VOs) are handled by the JMe3 internal working queue. On the other hand, VO updates are handled in parallel by
 * this component, extending the update loop of JMonkey. In addition to processing the <code>updateQueue</code> concurrently, the consumption strategy deployed by this component looks to the
 * simulation time of the update. If there is more than one update for the same VO (each VO can have one and only one update per simulation cycle), the queue consumer discards the older updates. This
 * optimization allows the visualizer to cope with heavier loads of updates from the simulation.
 * <p>
 * The different application states correspond to different modes of operations of the 3d Visualizer.
 * 
 * @param <S>
 *        the simulation application state type
 * @param <E>
 *        the environment application state type
 * @param <CM>
 *        the cursor manager type
 */
public abstract class BaseApplication<S extends SimulatingAppState<?>, E extends EnvironmentAppState> extends SimpleApplication
{
    /**
     * The environment application state
     */
    protected E                                                  envAppState;

    /**
     * The base application state of the visualizer, containing the root nodes of the application
     */
    protected DivasAppState                                      divasAppState;

    /**
     * The free camera application state
     */
    protected FreeCameraAppState                                 freeCameraAppState;

    /**
     * The simulation application state, the default application state of the visualizer
     */
    protected S                                                  simulatingAppState;

    /**
     * The mouse cursor manager of the visualizer, which allows for the mouse cursor image to be changed
     */
    protected CursorManager                                      cursorManager;

    /**
     * The Heads Up Display of the 3D visualizer, to display information messages to the user
     */
    protected HUDMessageArea                                     hudMessageArea;

    /**
     * Camera mode flag, indicating if the visualization is in agent camera mode (<code>true</code>) or free camera
     * mode (<code>false</code>). Default value is <code>false</code>.
     */
    protected boolean                                            agentCameraMode       = false;

    /**
     * Indicates if the application has already been initialized
     */
    protected boolean                                            isInitialized         = false;

    /**
     * Indicates if the application is in debug mode
     */
    protected boolean                                            debugMode             = false;

    /**
     * Thread safe update queue, for concurrent processing of VO state updates during the visualizer update loop
     */
    protected static final ConcurrentLinkedQueue<VisualizerTask> updateQueue           = new ConcurrentLinkedQueue<VisualizerTask>();

    /**
     * Update queue executor
     */
    private static final Multithreader                           multithreader         = new Multithreader("visualizerUpdateQueueThrd", ThreadPoolType.FIXED, 20, false);

    /**
     * Windowed average helper, to estimate max queue updates
     */
    private static final StatsHelper                             maxQueueUpdatesHelper = new StatsHelper(10);

    protected NiftyScreen<?, ?>                                  niftyScreen;

    /**
     * Creates an instance of the simulation application state
     * 
     * @return the newly created simulation application state
     */
    protected abstract S createSimulationAppState();

    /**
     * Creates an instance of the environment application state
     * 
     * @return the newly created environment application state
     */
    protected abstract E createEnvironmentAppState();

    /**
     * Creates an instance of Nifty Screen.
     * 
     * @return the newly created Nifty screen instance.
     */
    protected NiftyScreen<?, ?> createNiftyScreen()
    {
        return new NiftyScreen<MenuDialog, HelpDialog>();
    }

    @Override
    public void simpleInitApp()
    {
        envAppState = createEnvironmentAppState();
        stateManager.attach(envAppState);
        envAppState.setEnabled(true);

        divasAppState = new DivasAppState();
        stateManager.attach(divasAppState);
        divasAppState.setEnabled(true);

        freeCameraAppState = new FreeCameraAppState();
        stateManager.attach(freeCameraAppState);
        freeCameraAppState.setEnabled(true);

        simulatingAppState = createSimulationAppState();
        stateManager.attach(simulatingAppState);
        simulatingAppState.setEnabled(true);

        cursorManager = createCursorManager(assetManager);

        setDisplayFps(false);
        setDisplayStatView(false);
        setupKeys();
        setupHUDMessageArea();
        setPauseOnLostFocus(false);

        isInitialized = true;

        niftyScreen = createNiftyScreen();
    }

    protected CursorManager createCursorManager(AssetManager assetManager)
    {
        return new CursorManager(assetManager);
    }

    /**
     * Indicates if the application has already been initialized
     * 
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }

    @Override
    public void simpleUpdate(float tpf)
    {
        /* Interact with game events in the main loop */
        divasAppState.interpolate(System.currentTimeMillis());
        divasAppState.updateHUD();
        updateEffects(tpf);
        updateCamera();
        updateGUI(tpf);
    }

    /**
     * Runs tasks enqueued via {@link #enqueue(Callable)}
     */
    @Override
    protected void runQueuedTasks()
    {
        super.runQueuedTasks(); // we don't want to manage the core Jme3 nodes
        runUpdateTasks();
    }

    private void runUpdateTasks()
    {
        // calculate an upper bound for the maximum number of update tasks
        maxQueueUpdatesHelper.add(getMaxQueuedUpdates());
        int maxQueuedUpdatesPerCycle = Math.round(maxQueueUpdatesHelper.getAverage());

        // calculate an estimate of the number of updates to be processed during one simulation cycle
        // float estimatedSimUpdatesPerCycle = VisualSpectator.getPeriod() * maxQueuedUpdatesPerCycle / getTpf();

        // process the update queue in parallel
        long currentCycles = VisualSpectator.getCycles();
        Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        VisualizerTask task;
        while(tasks.size() < maxQueuedUpdatesPerCycle && (task = updateQueue.poll()) != null)
        {
            if(task.getCycle() == currentCycles)
            {
                tasks.add(task);
            }
        }
        multithreader.executeAndWait(tasks);
    }

    private float getMaxQueuedUpdates()
    {
        float tpf = getTpf();
        float period = VisualSpectator.getPeriod();
        float voCount = VisualSpectator.getNumberOfObjects();
        float result = ((tpf * voCount) / period) * 1.1f;

        // lower bound for the updates to be processed
        if(result < 10)
            result = 10;

        return result;
    }

    private float getTpf()
    {
        return timer.getTimePerFrame() * 1000 * speed;
    }

    /**
     * Enqueues a task/callable object to execute in the jME3
     * rendering thread.
     * <p>
     * Callables are executed right at the beginning of the main loop. They are executed even if the application is currently paused or out of focus. Note: This method was overridden because
     * 'taskQueue' is private in currently JME3 build. This allows us to process the queue in parallel in the main update loop.
     * 
     * @param task
     *        a task to be enqueued and processed in the visualizer update loop
     */
    public void enqueueTask(VisualizerTask task)
    {
        updateQueue.add(task);
    }

    /**
     * Display an information message in the Heads Up Display of the 3D visualizer
     * 
     * @param message
     *        the information message to be displayed to the user
     */
    public void displayMessage(String message)
    {
        if(hudMessageArea != null)
        {
            hudMessageArea.addMessage(message);
        }
    }

    /**
     * Gets the free camera application state of the visualizer
     * 
     * @return the free camera application state of the visualizer
     */
    public FreeCameraAppState getFreeCameraAppState()
    {
        return freeCameraAppState;
    }

    /**
     * Gets this application simulation state
     * 
     * @return the simulation application state
     */
    public S getSimulatingAppState()
    {
        return simulatingAppState;
    }

    /**
     * Sets this application cursor
     * 
     * @param cursorType
     *        the new cursor type
     */
    public void setCursor(CursorType cursorType)
    {
        cursorManager.setCursor(cursorType);
    }

    /**
     * Gets the camera mode state of the visualizer
     * 
     * @return a boolean indicating if the visualizer is in agent camera mode
     */
    public boolean isInAgentCameraMode()
    {
        return agentCameraMode;
    }

    /**
     * Sets the camera mode state of the visualizer
     * 
     * @param agentCam
     *        a boolean flag indicating the camera state of the visualizer. <code>true</code> for agent camera
     *        mode and <code>false</code> for free camera mode.
     */
    public void setAgentCameraMode(boolean agentCam)
    {
        agentCameraMode = agentCam;
    }

    /**
     * Setup keyboard commands for the application
     */
    protected void setupKeys()
    {
        ActionListener divasListener = new ActionListener()
        {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf)
            {
                if(name.equals(InputMapping.DEBUG.getKey()) && !keyPressed)
                {
                    if(debugMode)
                    {
                        displayMessage("Turning simulation state OFF");
                        debugMode = false;
                    }
                    else
                    {
                        displayMessage("Turning simulation state ON");
                        debugMode = true;
                    }
                }
                if(name.equals(InputMapping.CELLS.getKey()) && !keyPressed)
                {
                    if(CellBoundsVO.isShown())
                    {
                        CellBoundsVO.hide();
                    }
                    else
                    {
                        CellBoundsVO.show();
                    }
                }
                if(name.equals(InputMapping.ENV.getKey()) && !keyPressed)
                {
                    if(envAppState.isEnabled())
                    {
                        displayMessage("Turning environment OFF");
                        niftyScreen.removeAllElements();
                        envAppState.setEnabled(false);
                        stateManager.detach(envAppState);
                    }
                    else
                    {
                        displayMessage("Turning environment ON");
                        stateManager.attach(envAppState);
                        envAppState.setEnabled(true);
                    }
                }
                if(name.equals(InputMapping.DIVAS.getKey()) && !keyPressed)
                {
                    if(divasAppState.isEnabled())
                    {
                        displayMessage("Turning simulation state OFF");
                        niftyScreen.removeAllElements();
                        if(simulatingAppState.isEnabled())
                        {
                            simulatingAppState.removeSelections();
                            simulatingAppState.setEnabled(false);
                            stateManager.detach(simulatingAppState);
                        }
                        divasAppState.setEnabled(false);
                        stateManager.detach(divasAppState);
                    }
                    else
                    {
                        displayMessage("Turning simulation state ON");
                        stateManager.attach(divasAppState);
                        divasAppState.setEnabled(true);
                        if(!simulatingAppState.isEnabled())
                        {
                            stateManager.attach(simulatingAppState);
                            simulatingAppState.setEnabled(true);
                        }
                    }
                }
                if(name.equals(InputMapping.EDITING.getKey()) && !keyPressed)
                {
                    if(simulatingAppState.isEnabled())
                    {
                        simulatingAppState.toggleDragAndDropOnOff();
                    }
                }
                if(name.equals(InputMapping.ESC.getKey()) && !keyPressed)
                {
                    displayMessage("Simulation mode");

                    if(!envAppState.isEnabled())
                    {
                        displayMessage("Turning environment ON");
                        stateManager.attach(envAppState);
                        envAppState.setEnabled(true);
                    }

                    if(!simulatingAppState.isEnabled())
                    {
                        if(!divasAppState.isEnabled())
                        {
                            displayMessage("Turning simulation state ON");
                            stateManager.attach(divasAppState);
                            divasAppState.setEnabled(true);
                        }

                        stateManager.attach(simulatingAppState);
                        simulatingAppState.setEnabled(true);
                    }

                    closeNiftyWindows();
                }

                onNiftyKey(name, keyPressed, tpf);
                onCustomKey(name, keyPressed, tpf);
            }
        };

        inputManager.addMapping(InputMapping.ENV.getKey(), new KeyTrigger(KeyInput.KEY_F12));
        inputManager.addListener(divasListener, InputMapping.ENV.getKey());

        inputManager.addMapping(InputMapping.DIVAS.getKey(), new KeyTrigger(KeyInput.KEY_F11));
        inputManager.addListener(divasListener, InputMapping.DIVAS.getKey());

        inputManager.addMapping(InputMapping.EDITING.getKey(), new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addListener(divasListener, InputMapping.EDITING.getKey());

        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        inputManager.addMapping(InputMapping.ESC.getKey(), new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(divasListener, InputMapping.ESC.getKey());

        inputManager.addMapping(InputMapping.HELP_PANEL.getKey(), new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addListener(divasListener, InputMapping.HELP_PANEL.getKey());

        inputManager.addMapping(InputMapping.MENU_NIFTY.getKey(), new KeyTrigger(KeyInput.KEY_F10));
        inputManager.addListener(divasListener, InputMapping.MENU_NIFTY.getKey());

        inputManager.addMapping(InputMapping.DEBUG.getKey(), new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addListener(divasListener, InputMapping.DEBUG.getKey());

        inputManager.addMapping(InputMapping.CELLS.getKey(), new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addListener(divasListener, InputMapping.CELLS.getKey());

        setupCustomKeys(divasListener);
    }

    /**
     * Handles custom key actions
     * 
     * @param name
     * @param keyPressed
     * @param tpf
     */
    protected void onCustomKey(String name, boolean keyPressed, float tpf)
    {
        // empty
    }

    protected void onNiftyKey(String name, boolean keyPressed, float tpf)
    {
        if(name.equals(InputMapping.HELP_PANEL.getKey()) && !keyPressed)
        {
            niftyScreen.showHide(niftyScreen.getHelpDialog());
        }

        if(name.equals(InputMapping.MENU_NIFTY.getKey()) && !keyPressed)
        {
            niftyScreen.showHide(niftyScreen.getMenuDialog());
        }
    }

    protected void closeNiftyWindows()
    {
        niftyScreen.removeAllContextWindows();
    }

    /**
     * Setup application specific keys
     * 
     * @param divasListener
     */
    protected void setupCustomKeys(ActionListener divasListener)
    {
        // empty
    }

    protected void updateCamera()
    {
        // empty
    }

    /**
     * @return a flag indicating if the visualizer is in debug mode.
     */
    public boolean isDebugMode()
    {
        return debugMode;
    }

    /**
     * Attaches the free camera.
     */
    public void attachFreeCamera()
    {
        if(!freeCameraAppState.isEnabled())
        {
            displayMessage("Free Camera mode");
            stateManager.attach(freeCameraAppState);
            getCamera().setLocation(new Vector3f(30, 30, 30));
            getCamera().lookAt(new Vector3f(-150, 15, -150), Vector3f.UNIT_Y);
            freeCameraAppState.setEnabled(true);
        }
    }

    /**
     * Dettaches the free camera.
     */
    public void dettachFreeCamera()
    {
        if(freeCameraAppState.isEnabled())
        {
            displayMessage("Agent Camera mode");
            stateManager.detach(freeCameraAppState);
            freeCameraAppState.setEnabled(false);
        }
    }

    protected void updateEffects(float tpf)
    {
        PlayGround playGround = VisualSpectator.getPlayGround();

        if(playGround == null)
        {
            return;
        }

        synchronized(playGround.getEvents())
        {
            for(EventVO event : playGround.getEvents())
            {
                event.update(tpf);
            }
        }
    }

    protected void updateGUI(float tpf)
    {
        niftyScreen.updateContextWindows();
    }

    private void setupHUDMessageArea()
    {
        hudMessageArea = new HUDMessageArea(guiFont);
        guiNode.attachChild(hudMessageArea);
    }

    /**
     * Gets this application GUI font
     * 
     * @return the application's GUI font
     */
    public BitmapFont getGuiFont()
    {
        return guiFont;
    }

    @Override
    public void destroy()
    {
        super.destroy();

        // wait for the termination of OpenAL stuff (audio renderer, etc.)
        try
        {
            Thread.sleep(2000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        multithreader.terminate();
        Visualizer3DApplication.getInstance().stop();

        // exit normally
        System.exit(0);
    }
}
