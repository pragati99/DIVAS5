package edu.utdallas.mavs.divas.core.host;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.sim.env.Environment;
import edu.utdallas.mavs.divas.utils.FileIOHelper;

/**
 * This is a helper class for saving and loading simulations.
 */
public class SimLoader
{
    private final static Logger logger = LoggerFactory.getLogger(SimLoader.class);

    /**
     * Loads the simulation instance from the given file.
     * 
     * @param file
     *        The file with the simulation instance to be loaded from.
     */
    public static void loadSimulation(File file)
    {
        try
        {
            SimSnapshot<?> simSnapshot = FileIOHelper.load(file.getPath());
            Host.getHost().loadSimulation(simSnapshot);
            logger.info("Loading a simulation snapshot from file: {}", file.getPath());
        }
        catch(ClassNotFoundException | IOException e)
        {
            logger.warn("{} simulation snapshot file not found.");
        }
    }

    /**
     * Saves this simulation instance in the given file.
     * 
     * @param <E>
     *        The environment type
     * @param file
     *        The file where the simulation will be saved to.
     */
    public static <E extends Environment<?>> void saveSimulation(File file)
    {
        try
        {
            SimSnapshot<E> simSnapshot = new SimSnapshot<E>(Host.getHost().getHeartbeat(), Host.getHost().getIdManager(), Host.getHost().getSimulation());
            FileIOHelper.save(file.getPath(), simSnapshot);
            logger.info("Saved a simulation snapshot to file: {}", file.getPath());
        }
        catch(IOException e)
        {
            logger.error("Error saving simulation snapshot file", e);
        }
    }
}
