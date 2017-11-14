package edu.utdallas.mavs.evacuation.visualization.vis3D;

import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;
import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.SimulatingAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.customControls.panel.HelpDialog;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.CursorManager;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.PlayGround;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.VisualSpectator;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.ActivatedObjectState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.ActivatedObjectState.ActiveObjectType;
import edu.utdallas.mavs.evacuation.visualization.vis3D.appstate.EvacuationEnvironmentAppState;
import edu.utdallas.mavs.evacuation.visualization.vis3D.appstate.EvacuationSimulatingAppState;
import edu.utdallas.mavs.evacuation.visualization.vis3D.common.EvacuationInputMapping;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.EvacuationNiftyScreen;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.panel.EvacuationMenuDialog;
import edu.utdallas.mavs.evacuation.visualization.vis3D.engine.EvacuationCursorManager;

/**
 * This class describes the visualizer application for the evacuation simulation
 */
public class EvacuationApplication extends BaseApplication<EvacuationSimulatingAppState, EvacuationEnvironmentAppState>
{
    private boolean sirenMode = true;

    @Override
    protected EvacuationSimulatingAppState createSimulationAppState()
    {
        return new EvacuationSimulatingAppState();
    }

    @Override
    protected EvacuationEnvironmentAppState createEnvironmentAppState()
    {
        return new EvacuationEnvironmentAppState();
    }

    @Override
    protected CursorManager createCursorManager(AssetManager assetManager)
    {
        return new EvacuationCursorManager(assetManager);
    }

    @Override
    protected NiftyScreen<EvacuationMenuDialog, HelpDialog> createNiftyScreen()
    {
        return new EvacuationNiftyScreen();
    }

    @Override
    protected void setupCustomKeys(ActionListener divasListener)
    {
        inputManager.addMapping(EvacuationInputMapping.SIREN.getKey(), new KeyTrigger(KeyInput.KEY_F8));
        inputManager.addListener(divasListener, EvacuationInputMapping.SIREN.getKey());
    }
    
    @Override
    protected void closeNiftyWindows()
    {
        super.closeNiftyWindows();

        AgentVO<?> selectedAgent = SimulatingAppState.getContextSelectionPicker().getSelectedAgent();
        
        if(selectedAgent != null && selectedAgent.isCamModeOn())
        {
            selectedAgent.setCamMode(false);
            attachFreeCamera();
        }
    }

    @Override
    protected void updateCamera()
    {
        AgentVO<?> selectedAgent = SimulatingAppState.getContextSelectionPicker().getSelectedAgent();

        if(selectedAgent != null && selectedAgent.isCamModeOn())
        {
            getStateManager().getState(SimulatingAppState.class);
            if(selectedAgent.getState() instanceof HumanAgentState)
            {
                HumanAgentState agent = (HumanAgentState) selectedAgent.getState();
                cam.setLocation(selectedAgent.getLocalTranslation().add(0, agent.getVisionHeight(), 0));
                cam.lookAtDirection(agent.getHeading(), Vector3f.UNIT_Y);
            }
        }
    }

    @Override
    protected void onCustomKey(String name, boolean keyPressed, float tpf)
    {
        if(name.equals(EvacuationInputMapping.SIREN.getKey()) && !keyPressed)
        {
            PlayGround playGround = VisualSpectator.getPlayGround();

            if(playGround != null)
            {
                if(sirenMode)
                {
                    for(EnvObjectVO obj : playGround.getEnvObjects())
                    {
                        EnvObjectState state = obj.getState();
                        if(state instanceof ActivatedObjectState)
                        {
                            if(((ActivatedObjectState) state).getActiveObjectType().equals(ActiveObjectType.SIREN))
                            {
                                ((ActivatedObjectState) state).setOn(sirenMode);
                                Visualizer3DApplication.getInstance().getSimCommander().sendStateUpdate(state);
                            }
                        }
                    }
                    displayMessage("Turning all sirens ON");
                    sirenMode = false;
                }
                else
                {
                    for(EnvObjectVO obj : playGround.getEnvObjects())
                    {
                        EnvObjectState state = obj.getState();
                        if(state instanceof ActivatedObjectState)
                        {
                            if(((ActivatedObjectState) state).getActiveObjectType().equals(ActiveObjectType.SIREN))
                            {
                                ((ActivatedObjectState) state).setOn(sirenMode);
                                Visualizer3DApplication.getInstance().getSimCommander().sendStateUpdate(state);
                            }
                        }
                    }
                    displayMessage("Turning all sirens OFF");
                    sirenMode = true;
                }
            }
        }
    }
}
