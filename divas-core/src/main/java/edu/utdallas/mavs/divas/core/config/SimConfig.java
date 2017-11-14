package edu.utdallas.mavs.divas.core.config;

import edu.utdallas.mavs.divas.core.config.annotation.HelpText;
import edu.utdallas.mavs.divas.core.config.annotation.IntConfig;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;

/**
 * This class describes the DIVAs simulation configuration.
 */
public class SimConfig extends BaseConfig
{
    private static final long   serialVersionUID = 1L;
    
    private static final String fileName         = "_sim";

    private static SimConfig    instance;

    /**
     * Gets the instance of this configuration settings.
     * 
     * @return the simulation configuration settings instance
     */
    public static SimConfig getInstance()
    {
        if(instance == null)
        {
            instance = load();
        }
        return instance;
    }

    /**
     * Minimum simulation cycle (in milliseconds)
     */
    @HelpText("Simulation cycle (ms)")
    @IntConfig(min = 1)
    public int             min_Cycle_Interval           = 150;

    /**
     * Speed of sound
     */
    public float           speed_Of_Sound               = 70;

    /**
     * Speed of smell
     */
    public float           speed_Of_Smell               = 50;

    /**
     * Max smell distance
     */
    public float           max_Smell_Distance           = 300;

    /**
     * Max sound distance
     */
    public float           max_Sound_Distance           = 350;

    /**
     * Default visible distance
     */
    public float           default_visible_Distance     = 70;

    /**
     * Default FOV
     */
    public float           default_fov                  = 20;

    /**
     * Agent proximity. Below this distance, any object is considered to be in direct contact with the agent. This helps
     * avoiding overlapping of simulation entities.
     */
    public float           agent_Proximity              = 0.5f;

    /**
     * Agent proximity. Below this distance, any object is considered to be in direct contact with the agent. This helps
     * avoiding overlapping of simulation entities.
     */
    public float           agent_Radius                 = 1.0f;

    /**
     * Default vision algorithm
     */
    public VisionAlgorithm default_Vision_Algorithm     = VisionAlgorithm.DivasVision;

    /**
     * Use demographic splitting of cells
     */
    public boolean         use_demographic_splitting    = false;

    public int             cc_capacity                  = 32;

    public float           max_cc_depth                 = 16;

    public float           urgency_threshold            = 50f;

    public float           neighbors_urgency_weight     = 0.05f;

    public float           max_workload_threshold       = 50f;

    public float           min_workload_threshold       = 10f;

    public float           agent_weight                 = 1f;

    public float           env_object_weight            = 0.5f;

    /**
     * Agent Vision Accuracy (Case #3) - How many rays to fire
     */
    @HelpText("Agent Vision Accuracy (Case #3) - How many rays to fire")
    @IntConfig(min = 1)
    public int             vision_Rays_Count            = 10;

    /**
     * Agent Vision Accuracy (Case #3) - How many circle to create the cone with
     */
    @HelpText("Agent Vision Accuracy (Case #3) - How many circle to create the cone with")
    @IntConfig(min = 1)
    public int             vision_Circles_Count         = 6;

    /**
     * Agent Keyboard Control - move speed
     */
    @HelpText("Agent Keyboard Control - move speed")
    public float           keyboard_Motion_Multiplier   = .02f;

    /**
     * Agent Keyboard Control - rotation speed
     */
    @HelpText("Agent Keyboard Control - rotation speed")
    public float           keyboard_Rotation_Multiplier = .2f;

    /**
     * Stores the configuration to a local file.
     */
    public void save()
    {
        save(fileName);
    }

    private static SimConfig load()
    {
        SimConfig config = (SimConfig) load(fileName);
        if(config == null)
        {
            config = new SimConfig();
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
