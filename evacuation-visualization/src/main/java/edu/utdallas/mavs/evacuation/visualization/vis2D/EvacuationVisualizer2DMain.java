package edu.utdallas.mavs.evacuation.visualization.vis2D;

import org.apache.log4j.PropertyConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import edu.utdallas.mavs.divas.visualization.guice.CommunicationModuleProvider;
import edu.utdallas.mavs.divas.visualization.guice.VisualizerModule;
import edu.utdallas.mavs.divas.visualization.vis2D.Visualizer2DApplication;
import edu.utdallas.mavs.divas.visualization.vis2D.Visualizer2DFrame;

/**
 * This is the <code>main</code> class for the 2D visualizer.
 * <p>
 * This class initialize the 2D visualizer application and inject the dependencies
 * 
 * @version 4.0
 */
public class EvacuationVisualizer2DMain
{
    /**
     * Starts the 2D visualizer
     * 
     * @param args
     */
    public static void main(String args[])
    {
        // reads configuration properties for log4j
        PropertyConfigurator.configure("log4j.properties");

        // Sets the simulation host IP and port properties
        CommunicationModuleProvider.setConnectionProperties(args[0].split(" ")[0], args[0].split(" ")[1]);

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new VisualizerModule());
        final Visualizer2DApplication visualizerApplication = injector.getInstance(Visualizer2DApplication.class);
        final Visualizer2DFrame visualizerFrame2D = injector.getInstance(Visualizer2DFrame.class);
        visualizerApplication.start();
        visualizerFrame2D.start();
    }
}
