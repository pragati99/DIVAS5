package edu.utdallas.mavs.divas.gui.services;

import com.jme3.app.Application;
import com.jme3.system.JmeContext;

/**
 * Application to unpack JME3 natives
 */
public class LwjglApplication extends Application
{
    @Override
    public void start()
    {
        start(JmeContext.Type.Headless);
    }

    /**
     * Forces lwjgl natives to be unpacked
     */
    public static void setup()
    {
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.OFF);
        LwjglApplication app = new LwjglApplication();
        app.start();
        app.stop();
        app.destroy();
    }
}
