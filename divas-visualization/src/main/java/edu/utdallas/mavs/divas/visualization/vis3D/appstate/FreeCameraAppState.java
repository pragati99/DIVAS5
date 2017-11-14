package edu.utdallas.mavs.divas.visualization.vis3D.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMapping;

/**
 * This class describes the camera state of the 3D visualizer.
 * <p>
 * This application state controls the free camera mode of visualization.
 */
public class FreeCameraAppState extends AbstractAppState
{
    private static SimpleApplication app;

    boolean                          notdone   = true;

    private InputListener            moveListener;

    private float                    moveSpeed = 50;

    @Override
    public void update(float tpf)
    {}

    @Override
    public void stateDetached(AppStateManager stateManager)
    {
        super.stateDetached(stateManager);
        unsetCameraControl();
        removeMappings();
    }

    private void unsetCameraControl()
    {
        app.getFlyByCamera().setEnabled(false);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        FreeCameraAppState.app = (SimpleApplication) app;
        setCameraControl();
    }

    protected void setCameraControl()
    {
        app.getFlyByCamera().setEnabled(true);
        app.getFlyByCamera().setDragToRotate(true);
        app.getFlyByCamera().setMoveSpeed(moveSpeed);
        app.getFlyByCamera().setRotationSpeed(3);

        setup();
        addMappings();

        if(notdone)
        {
            app.getInputManager().addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
            app.getInputManager().deleteTrigger("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

            app.getInputManager().deleteMapping("FLYCAM_ZoomIn");
            app.getInputManager().deleteMapping("FLYCAM_ZoomOut");
            notdone = false;
        }
    }

    protected void setup()
    {
        moveListener = new AnalogListener()
        {

            @Override
            public void onAnalog(String name, float arg1, float arg2)
            {
                if(name.equals(InputMapping.SPEED_DOWN.getKey()))
                {

                    moveSpeed = moveSpeed - 10;
                    if(moveSpeed < 0)
                    {
                        moveSpeed = 0;
                    }
                    app.getFlyByCamera().setMoveSpeed(moveSpeed);
                    Visualizer3DApplication.getInstance().getApp().displayMessage("Setting Move Speed: " + moveSpeed);

                }
                if(name.equals(InputMapping.SPEED_UP.getKey()))
                {

                    moveSpeed = moveSpeed + 10;
                    if(moveSpeed < 0)
                    {
                        moveSpeed = 0;
                    }
                    app.getFlyByCamera().setMoveSpeed(moveSpeed);
                    Visualizer3DApplication.getInstance().getApp().displayMessage("Setting Move Speed: " + moveSpeed);

                }
            }

        };
    }

    protected void removeMappings()
    {

    }

    protected void addMappings()
    {
        app.getInputManager().addMapping(InputMapping.SPEED_UP.getKey(), new KeyTrigger(KeyInput.KEY_ADD));
        app.getInputManager().addMapping(InputMapping.SPEED_DOWN.getKey(), new KeyTrigger(KeyInput.KEY_SUBTRACT));
        app.getInputManager().addListener(moveListener, InputMapping.SPEED_UP.getKey(), InputMapping.SPEED_DOWN.getKey());
    }

    /**
     * Gets the free camera application state of the 3D visualizer.
     * 
     * @return the free camera application state
     */
    public FreeCameraAppState getAppState()
    {
        return this;
    }

}
