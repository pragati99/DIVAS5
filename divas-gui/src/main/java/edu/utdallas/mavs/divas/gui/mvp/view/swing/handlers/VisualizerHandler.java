/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers;

import javax.swing.JFrame;

import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.gui.DivasGuiApplication;
import edu.utdallas.mavs.divas.gui.guice.CommunicationModuleProvider;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.VisConfigDialog;
import edu.utdallas.mavs.divas.gui.services.process.ProcessLauncher;

public class VisualizerHandler
{
    public void start3DVisualizer()
    {
        ProcessLauncher vis3DLauncher = new ProcessLauncher();
        VisConfig visConfig = VisConfig.getInstance();
        vis3DLauncher.start(DivasGuiApplication.getInstance().getVis3DMainClass(), visConfig.memory, CommunicationModuleProvider.getHostName(), CommunicationModuleProvider.getPort());
    }

    public void start2DVisualizer()
    {
        ProcessLauncher vis2DLauncher = new ProcessLauncher();
        VisConfig visConfig = VisConfig.getInstance();
        vis2DLauncher.start(DivasGuiApplication.getInstance().getVis2DMainClass(), visConfig.memory, CommunicationModuleProvider.getHostName(), CommunicationModuleProvider.getPort());
    }

    public void visualizerOptions(JFrame parent)
    {
        VisConfig visConfig = VisConfig.getInstance();
        VisConfigDialog dialog = new VisConfigDialog();
        dialog.showDialog(parent, visConfig);
    }
}
*/