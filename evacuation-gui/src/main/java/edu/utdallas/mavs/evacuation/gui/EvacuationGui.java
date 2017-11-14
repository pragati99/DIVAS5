package edu.utdallas.mavs.evacuation.gui;

import javafx.application.Application;
import edu.utdallas.mavs.divas.gui.DivasGuiApplication;
import edu.utdallas.mavs.evacuation.visualization.vis2D.EvacuationVisualizer2DMain;
import edu.utdallas.mavs.evacuation.visualization.vis3D.EvacuationVisualizer3DMain;

public class EvacuationGui extends DivasGuiApplication
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Application.launch(EvacuationGui.class, args);
    }

    @Override
    public String getVis2DMainClass()
    {
        return EvacuationVisualizer2DMain.class.getName();
    }

    @Override
    public String getVis3DMainClass()
    {
        return EvacuationVisualizer3DMain.class.getName();
    }

}
