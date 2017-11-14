package edu.utdallas.mavs.evacuation.simulation;

import org.apache.log4j.PropertyConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import edu.utdallas.mavs.divas.core.spec.env.EnvSpecEnum;
import edu.utdallas.mavs.evacuation.simulation.config.EvacuationVisConfig;
import edu.utdallas.mavs.evacuation.simulation.guice.SimulationModule;
import edu.utdallas.mavs.evacuation.simulation.host.EvacuationHost;

/**
 * Simulation of evacuation scenarios over DIVAs framework.
 */
public class EvacuationMain
{
    /**
     * Simulation entry point.
     * 
     * @param args no arguments defined
     */
    public static void main(String[] args)
    {
        // reads configuration properties for log4j
        PropertyConfigurator.configure("log4j.properties");

        // disables java logging
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.OFF);

        // Register custom configurations
        EvacuationVisConfig.register();
        
        // Starts the simulation
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new SimulationModule());
        injector.getInstance(EvacuationHost.class).start();
    }
}
