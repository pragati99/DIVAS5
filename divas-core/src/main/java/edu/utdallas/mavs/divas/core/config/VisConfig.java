package edu.utdallas.mavs.divas.core.config;

import java.awt.Color;

import edu.utdallas.mavs.divas.core.config.annotation.HelpText;
import edu.utdallas.mavs.divas.core.config.annotation.IntConfig;

/**
 * This class describes the user configuration for the visualizer.
 */
public class VisConfig extends BaseConfig
{
    private static final long     serialVersionUID        = 1L;

    private static final String   fileName                = "_vis";

    private static VisConfig      instance;

    public static final ConfigKey visualizer2D_main_class = ConfigKey.create("visualizer2D_main_class");

    public static final ConfigKey visualizer3D_main_class = ConfigKey.create("visualizer2D_main_class");

    /**
     * Gets the instance of this configuration settings.
     * 
     * @return the visualizer configuration settings instance
     */
    public static VisConfig getInstance()
    {
        if(instance == null)
        {
            instance = load();
        }
        return instance;
    }

    public DisplayResolution display_resolution = new DisplayResolution(1280, 768, 32, 75);

    @HelpText("Run the visualizer in fullscreen or windowed mode.")
    public boolean           fullscreen         = false;

    @HelpText("Run the visualizer with reduced quality models.")
    public boolean           lowQualityModels   = false;

    @HelpText("Run the visualizer with animated models.")
    public boolean           animatedModels     = true;

    @IntConfig(combo = true, values = { 0, 1, 2, 4, 8, 16 })
    public int               antialiasing       = 0;

    @IntConfig(combo = true, values = { 256, 512, 1024, 2048 })
    public int               memory             = 1024;                                    // in MB

    public Color             vision_Color       = new Color(1.0f, 0.2f, 0, 0.1f);

    public void save()
    {
        save(fileName);
    }

    public static VisConfig load()
    {
        VisConfig config = (VisConfig) load(fileName);
        if(config == null)
        {
            config = new VisConfig();
        }
        return config;
    }

    /**
     * Reloads the configuration file from disk
     */
    public static void refresh()
    {
        instance = load();
    }
}
