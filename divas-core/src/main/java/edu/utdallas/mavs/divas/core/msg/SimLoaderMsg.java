package edu.utdallas.mavs.divas.core.msg;

import java.io.File;
import java.io.Serializable;

/**
 * This class describes the simulation snapshot message sent to the Host.
 */
public class SimLoaderMsg implements Serializable
{
    private static final long  serialVersionUID = 1L;

    private File               file;

    private SimLoaderCommand simSnapshotCommand;

    /**
     * Creates a {@code SimSnapshotMsg} with a given file and simulation snapshot command.
     * 
     * @param file
     *        The file containing a simulation snapshot or an empty file to save a simulation snapshot.
     * @param simSnapshotCommand
     *        The simulation snapshot command.
     */
    public SimLoaderMsg(File file, SimLoaderCommand simSnapshotCommand)
    {
        super();
        this.file = file;
        this.simSnapshotCommand = simSnapshotCommand;

    }

    /**
     * Enumeration of the simulation snapshot commands
     */
    public enum SimLoaderCommand
    {
        /**
         * Save simulation snapshot command
         */
        SAVE_SIMULATION,
        /**
         * Load simulation snapshot command
         */
        LOAD_SIMULATION,
        /**
         * Load environment command
         */
        LOAD_ENVIRONMENT,
        /**
         * Load environment from XML file command
         */
        LOAD_ENVIRONMENT_XML,
        /**
         * Save environment command
         */
        SAVE_ENVIRONMENT,
        
    }

    /**
     * @return the simulation snapshot command stored in this message
     */
    public SimLoaderCommand getSimSnapshotCommand()
    {
        return simSnapshotCommand;
    }

    /**
     * The file containing a simulation snapshot when loading a simulation snapshot otherwise an empty file
     * where the simulation snapshot will be saved to when saving a simulation snapshot.
     * 
     * @return the file stored in this message.
     */
    public File getFile()
    {
        return file;
    }
}
