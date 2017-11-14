package edu.utdallas.mavs.divas.core.config;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.utils.FileIOHelper;

/**
 * This class describes an abstract configuration for divas components.
 */
public abstract class BaseConfig implements Serializable
{
    private static final long                serialVersionUID = 1L;

    private static final Logger              logger           = LoggerFactory.getLogger(BaseConfig.class);

    private static final String              configPath       = System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "config" + System.getProperty("file.separator");

    /**
     * Custom properties, to be used by concrete simulations
     */
    public Map<ConfigKey, ConfigProperty<?>> customProperties = new HashMap<>();

    public void addCustomProperty(ConfigProperty<?> property)
    {
        if(!customProperties.containsKey(property.getKey()))
        {
            customProperties.put(property.getKey(), property);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomProperty(ConfigKey key)
    {
        return (T) customProperties.get(key).getValue();
    }

    /**
     * Saves this configuration to a file in the user home directory.
     * 
     * @param name configuration file name
     */
    protected void save(String name)
    {
        File file = new File(configPath);
        if(!file.exists())
        {
            logger.info("Config directory not found. Creating it in user home.");
            file.mkdirs();
        }
        try
        {
            FileIOHelper.save(configPath + name + ".config", this);
        }
        catch(IOException e)
        {
            logger.error("Error saving {} configuration file.", getClass().getName());
        }
    }

    /**
     * Loads an existing configuration file.
     * 
     * @param name configuration file name
     * @return the loaded configuration
     */
    protected static BaseConfig load(String name)
    {
        try
        {
            BaseConfig config = FileIOHelper.load(configPath + name + ".config");
            if(config != null && config.customProperties == null)
                config.customProperties = new HashMap<>();
            return config;
        }
        catch(ClassNotFoundException | IOException e)
        {
            logger.warn("{} configuration file not found in user home. Using default.", name);
        }
        return null;
    }
}
