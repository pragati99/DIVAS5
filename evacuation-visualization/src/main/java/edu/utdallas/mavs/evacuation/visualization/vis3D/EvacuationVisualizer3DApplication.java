package edu.utdallas.mavs.evacuation.visualization.vis3D;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.VisualSpectator;

/**
 * This class describes the 3D visualizer singleton class.
 * <p>
 */
public class EvacuationVisualizer3DApplication extends Visualizer3DApplication<EvacuationApplication>
{
    @Inject
    public EvacuationVisualizer3DApplication(SimAdapter simClientAdapter, EvacuationApplication app, VisualSpectator spectator)
    {
        super(simClientAdapter, app, spectator);
    }
}
