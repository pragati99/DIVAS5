package edu.utdallas.mavs.evacuation.visualization.vis3D.appstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.SimulatingAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;
import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMode;
import edu.utdallas.mavs.divas.visualization.vis3D.dialog.NiftyScreen;
import edu.utdallas.mavs.evacuation.visualization.vis3D.EvacuationApplication;
import edu.utdallas.mavs.evacuation.visualization.vis3D.common.EvacuationCursorType;
import edu.utdallas.mavs.evacuation.visualization.vis3D.common.EvacuationInputMode;
import edu.utdallas.mavs.evacuation.visualization.vis3D.dialog.voModification.agentModification.AgentPropertyDialog;

/**
 * This class represents the main application state of the evacuation 3D visualizer.
 * <p>
 * It allows users to interact with the visualized simulation, by selecting visualized objects (VOs) and triggering events to the simulation. It also allows users to add agents to simulation.
 */
public class EvacuationSimulatingAppState extends SimulatingAppState<EvacuationApplication>
{
    private final static Logger logger = LoggerFactory.getLogger(EvacuationSimulatingAppState.class);

    @Override
    protected void triggerEvent(Vector3f position)
    {
        if(mode.equals(EvacuationInputMode.ADD_EXPLOSION))
        {
            logger.debug("Adding explosion @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createExplosion(position, 0.00000003f, true);
        }
        else if(mode.equals(EvacuationInputMode.ADD_EXPLOSION_NO_SMOKE))
        {
            logger.debug("Adding explosion no smoke @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createExplosion(position, 0.00000003f, false);
        }
        else if(mode.equals(EvacuationInputMode.ADD_SPOTLIGHT))
        {
            logger.debug("Adding spotlight @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createSpotlight(position);
        }
        else if(mode.equals(EvacuationInputMode.ADD_DRUMS))
        {
            logger.debug("Adding drums @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createDrums(position);
        }
        else if(mode.equals(EvacuationInputMode.ADD_GRILL))
        {
            logger.debug("Adding grill @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createGrill(position);
        }
        else if(mode.equals(EvacuationInputMode.ADD_FIREWORKS))
        {
            logger.debug("Adding firework @ location: " + position);
            Visualizer3DApplication.getInstance().getSimCommander().createFireworks(position, 1, false);
        }
        else
        {
            logger.debug("No handler for triggering event implemented for {}", mode);
        }
    }

    @Override
    public void setCursor(InputMode inputMode)
    {
        // change cursors in here (and anything else required when changing modes)
        if(inputMode.equals(EvacuationInputMode.ADD_EXPLOSION))
        {
            app.setCursor(EvacuationCursorType.SMOKE_BOMB);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_EXPLOSION_NO_SMOKE))
        {
            app.setCursor(EvacuationCursorType.BOMB);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_SPOTLIGHT))
        {
            app.setCursor(EvacuationCursorType.SPOTLIGHT);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_DRUMS))
        {
            app.setCursor(EvacuationCursorType.DRUMS);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_GRILL))
        {
            app.setCursor(EvacuationCursorType.GRILL);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_FIREWORKS))
        {
            app.setCursor(EvacuationCursorType.FIREWORKS);
        }
        else if(inputMode.equals(EvacuationInputMode.ADD_TREASURE))
        {
            app.setCursor(EvacuationCursorType.TREASURE);
        }
        else if(inputMode.equals(InputMode.ADD_AGENT))
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

    @Override
    protected InputMode getMappedEventInputMode(String inputMode)
    {
        InputMode mappedInputMode = null;

        if(inputMode.equals("ADD_EXPLOSION"))
        {
            mappedInputMode = EvacuationInputMode.ADD_EXPLOSION;
        }
        else if(inputMode.equals("ADD_EXPLOSION_NO_SMOKE"))
        {
            mappedInputMode = EvacuationInputMode.ADD_EXPLOSION_NO_SMOKE;
        }
        else if(inputMode.equals("ADD_FIREWORKS"))
        {
            mappedInputMode = EvacuationInputMode.ADD_FIREWORKS;
        }
        else if(inputMode.equals("ADD_TREASURE"))
        {
            mappedInputMode = EvacuationInputMode.ADD_TREASURE;
        }
        else if(inputMode.equals("ADD_DRUMS"))
        {
            mappedInputMode = EvacuationInputMode.ADD_DRUMS;
        }
        else if(inputMode.equals("ADD_GRILL"))
        {
            mappedInputMode = EvacuationInputMode.ADD_GRILL;
        }
        else if(inputMode.equals("ADD_SPOTLIGHT"))
        {
            mappedInputMode = EvacuationInputMode.ADD_SPOTLIGHT;
        }
        else
        {
            mappedInputMode = EvacuationInputMode.ADD_EXPLOSION;
        }

        return mappedInputMode;
    }

    @Override
    protected void setupContextObservers()
    {
        super.setupContextObservers();
        
        contextObservers.add(new AgentPropertyDialog(NiftyScreen.windowLayerElement));
    }
}
