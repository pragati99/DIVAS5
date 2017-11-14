package edu.utdallas.mavs.divas.core.spec.env;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes a environment specification for DIVAs.
 * <p>
 * It should be used to define the initial specification of an environment.
 */
public class EnvSpec implements Serializable
{
    private static final long     serialVersionUID = 1L;
    private final static Logger   logger           = LoggerFactory.getLogger(EnvSpec.class);
    

    /**
     * Environment loader
     */
    protected transient EnvLoader envLoader;

    /**
     * Environment specification file name. To be reassigned by concrete environment specs.
     */
    protected transient String    fileName         = getDefaultEnvironmentSpec() + ".xml";

    /**
     * Environment specification folder.
     */
    protected transient String    envFolder        = System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "environment";

    /**
     * Environment root cell state. Environment starts with exactly one cell, namely the root cell.
     */
    protected CellStateSpec       cellState;

    /**
     * Creates a new environment specification. Uses the default folder location <code>@user.home/Divas/environment</code>. Default environment file name is <code>default.env</code>.
     * 
     * @param envLoader
     *        the loader used for the environment spec.
     */
    public EnvSpec(EnvLoader envLoader)
    {
        this.envLoader = envLoader;
    }

    /**
     * Creates a new environment specification with the given fileName.
     * 
     * @param fileName
     *        the file name
     * @param envLoader
     *        the loader used for this environment spec.
     */
    public EnvSpec(String fileName, EnvLoader envLoader)
    {
        this(envLoader);
        this.fileName = fileName;
    }

    /**
     * Creates a new environment specification.
     * 
     * @param envFolder
     *        the environment folder location
     * @param fileName
     *        the environment file name
     * @param envLoader
     *        the loader used for this environment spec.
     */
    public EnvSpec(String envFolder, String fileName, EnvLoader envLoader)
    {
        this(fileName, envLoader);
        this.envFolder = envFolder;
    }

    /**
     * Gets the root cell state of the environment.
     * 
     * @return the root cell state
     */
    public CellStateSpec getCellState()
    {
        return cellState;
    }

    /**
     * Sets the root cell state of the environment
     * 
     * @param cellState
     *        the new environmnet root cell state
     */
    public void setCellState(CellStateSpec cellState)
    {
        this.cellState = cellState;
    }

    /**
     * Saves environment specification to an XML file.
     * 
     * @param fileNameXML
     */
    public void saveToXML()
    {
        try
        {           
            envLoader.saveEnvironment(getFilePath(), this);
            logger.info("Saved environment spec file to: " + getFilePath());
        }
        catch(Exception e)
        {
            logger.error("An error has occurred while saving environment spec to an XML file {}", fileName);
        }
    }

    /**
     * Loads specification from an XML file
     * 
     * @return the loaded specification
     */
    public EnvSpec loadFromXMLFile()
    {
        try
        {
            return envLoader.loadToEnvSpec(getFilePath());
        }
        catch(IOException ex)
        {
            logger.warn("The environment specification file \"{}\" was not found. Using default file.", fileName);
        }
        catch(ClassNotFoundException ex)
        {
            logger.error("An error has occurred while loading environment specification file {}. Using default file.", fileName);
        }
        return null;
    }

    /**
     * Gets the default environment specification file name
     * 
     * @return the environment specification file
     */
    protected String getDefaultEnvironmentSpec()
    {
        return EnvSpecEnum.Default.name();
    }
    
    protected String getFilePath()
    {
        return String.format("%s%s%s", envFolder, System.getProperty("file.separator"), fileName);
    }
}
