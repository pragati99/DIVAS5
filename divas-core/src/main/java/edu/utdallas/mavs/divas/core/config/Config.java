package edu.utdallas.mavs.divas.core.config;

import java.awt.Color;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import edu.utdallas.mavs.divas.utils.ResourceManager;

/**
 * Configuration settings utilities for DIVAs
 */
public class Config
{
    /**
     * Configuration settings properties
     */
    private Properties           simProps;

    /**
     * Singleton instance of configuration settings
     */
    private static Config        instance;

    /**
     * this stack prevents infinitely looping property references by keeping track of which properties are currently
     * being resolved
     */
    private static Stack<String> expandStack;

    /**
     * the name of the properties file containing configuration properties. This file must be in the java classpath at
     * runtime.
     */
    private static final String  CONFIG_FILE       = "divas.properties";

    /**
     * Default host IP property key
     */
    private static final String  DEFAULT_HOST_IP = "DefaultHostIP";
    
    /**
     * Default host port property key
     */
    private static final String  DEFAULT_HOST_PORT = "DefaultHostPort";

    private Config()
    {
        simProps = new Properties();
        expandStack = new Stack<String>();

        if(!readUserConfig())
        {
            try
            {
                InputStream is = ResourceManager.getResourceStream(CONFIG_FILE);
                if(is != null)
                    simProps.load(is);
                else
                    System.err.println("Couldn't locate configuration file: " + CONFIG_FILE + "\nMake sure configuration file is in the java classpath.");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean readUserConfig()
    {
        FileInputStream istream = null;
        try
        {
            istream = new FileInputStream(CONFIG_FILE);
            simProps.load(istream);
            istream.close();
            return true;
        }
        catch(Exception e)
        {
            if(e instanceof InterruptedIOException || e instanceof InterruptedException)
            {
                Thread.currentThread().interrupt();
            }            
            return false;
        }
        finally
        {
            if(istream != null)
            {
                try
                {
                    istream.close();
                }
                catch(InterruptedIOException ignore)
                {
                    // Thread.currentThread().interrupt();
                }
                catch(Throwable ignore)
                {
                }
            }
        }
    }

    /**
     * Retrieves the value for the specified key in the configuration file. Any keys that are referenced within the
     * specified key's value are expanded, replacing the reference with the referenced key's value. The method returns
     * an empty string if the property is not found. <br>
     * <br>
     * The expected format for key/value pairs in the configuration file is as follows:
     * 
     * <pre>
     * Key=Value
     * SomePath=../here
     * </pre>
     * 
     * To include a reference to another key within a key's value, use the following format:
     * 
     * <pre>
     * Key=some value
     * RefKey=here is ${Key}
     * </pre>
     * 
     * In the above example, the method returns the string <code>"here is some value"</code> for key <code>RefKey</code> . <br>
     * <br>
     * When a circular reference is detected, the reference closing the reference loop is replaced with an empty string. <br>
     * <br>
     * 
     * @param key
     *        - the property key
     * @return the value for the given property key as specified in the configuration file, or the string <code>""</code> if the property key is not found.
     */
    public static String getProperty(String key)
    {
        if(instance == null)
            instance = new Config();

        String value = instance.simProps.getProperty(key);

        if(value == null)
        {
            System.err.println("[Config] Could not retrieve key: " + key);
            return "";
        }

        return expandString(value);
    }

    /**
     * Retrieves the integer value for the specified key in the configuration file. The method displays an error and
     * returns -1 if the property is not found. <br>
     * <br>
     * The expected format for key/value pairs in the configuration file is as follows:
     * 
     * <pre>
     * Key = Value
     * </pre>
     * 
     * @param key
     *        - the property key
     * @return the integer value for the given property key as specified in the configuration file, or -1 if the
     *         property key is not found.
     */
    protected static int getIntProperty(String key)
    {
        int value = -1;

        try
        {
            value = Integer.parseInt(getProperty(key));
        }
        catch(NumberFormatException e)
        {
            System.err.println("[Config] Bad integer value for key: " + key);
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Retrieves the float value for the specified key in the configuration file. The method displays an error and
     * returns -1 if the property is not found. <br>
     * <br>
     * The expected format for key/value pairs in the configuration file is as follows:
     * 
     * <pre>
     * Key = Value
     * </pre>
     * 
     * @param key
     *        - the property key
     * @return the float value for the given property key as specified in the configuration file, or -1 if the
     *         property key is not found.
     */
    protected static float getFloatProperty(String key)
    {
        float value = -1;

        try
        {
            value = Float.parseFloat(getProperty(key));
        }
        catch(NumberFormatException e)
        {
            System.err.println("[Config] Bad float value for key: " + key);
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Retrieves the image icon for the specified key in the configuration file.<br>
     * <br>
     * The expected format for key/value pairs in the configuration file is as follows:
     * 
     * <pre>
     * Key = Value
     * </pre>
     * 
     * @param key
     *        - the property key
     * @return the image icon for the given property key as specified in the configuration file.
     */
    public static Image getIcon(String key)
    {
        URL imageUrl = ResourceManager.getResourceURL(key);
        return new ImageIcon(imageUrl).getImage();
    }

    /**
     * Retrieves the Color value for the specified key in the configuration file. The method returns the color Black if
     * the property is not found or if the color string is incorrectly formatted. <br>
     * <br>
     * The expected format for key/colorValue pairs in the configuration file is as follows:
     * 
     * <pre>
     * Key = 0xRRGGBB
     * </pre>
     * 
     * The color value is the hexadecimal triplet representation of the color.
     * 
     * <pre>
     * SomeColor = 0xFF00FF
     * </pre>
     * 
     * In the example above, the method returns the color Magenta. <br>
     * 
     * @param key
     *        - the property key
     * @return the color value for the given property key as specified in the configuration file, or the color Black if
     *         the property key is not found.
     */
    protected static Color getColorProperty(String key)
    {
        if(instance == null)
            instance = new Config();

        String value = instance.simProps.getProperty(key);

        if(value == null)
        {
            System.err.println("[Config] Could not retrieve key: " + key);
            return Color.BLACK;
        }

        try
        {
            return Color.decode(value);
        }
        catch(NumberFormatException e)
        {
            System.err.println("[Config] Bad color value for key: " + key);
            return Color.BLACK;
        }

    }

    /**
     * Parses property values, substituting references to other keys with the value of those keys. This method maintains
     * a stack of references in the order that they are being resolved to prevent circular references. When a circular
     * reference is detected, this method prints and error and substitutes an empty string for the value of the
     * violating reference. <br>
     * 
     * @param value
     *        - the value of a key which may contain references to other keys, denoted by <code>${KeyName}</code>
     * @return property value with all key references resolved
     */
    private static String expandString(String value)
    {
        StringBuffer strBuff = new StringBuffer();
        Matcher m = Pattern.compile("\\$\\{.*?\\}").matcher(value);
        while(m.find())
        {
            String nextKey = value.substring(m.start() + 2, m.end() - 1);
            if(expandStack.contains(nextKey))
            {
                System.err.println("[Config] Circular reference found in key: " + nextKey);
                m.appendReplacement(strBuff, "");
            }
            else
            {
                expandStack.push(nextKey);
                m.appendReplacement(strBuff, getProperty(nextKey));
                expandStack.pop();
            }
        }
        m.appendTail(strBuff);

        return strBuff.toString();
    }

    public static String getDefaultHostIP()
    {
        return getProperty(DEFAULT_HOST_IP);
    }
    
    public static String getDefaultHostPort()
    {
        return getProperty(DEFAULT_HOST_PORT);
    }    

    public static String getEnvObjectsXML()
    {
        return "envobjects/envobjects.xml";
    }

    public static String getAgentsXML()
    {
        return "agents/agents.xml";
    }

    public static String getEventsXML()
    {
        return "events/events.xml";
    }

    public static Image getDefaultCursorIcon()
    {
        return getIcon("cursors/default.png");
    }

    public static Image getAgentCursorIcon()
    {
        return getIcon("cursors/agent_cursor.png");
    }

    public static Image getBombCursorIcon()
    {
        return getIcon("cursors/bomb_cursor.png");
    }

    public static Image getBombNoSmokeCursorIcon()
    {
        return getIcon("cursors/bomb_nosmoke_cursor.png");
    }

    public static Image getFireworksCursorIcon()
    {
        return getIcon("cursors/fireworks-icon.png");
    }

    public static Image getSplitCellCursorIcon()
    {
        return getIcon("cursors/split_cell.png");
    }

    public static Image getMergeCellCursorIcon()
    {
        return getIcon("cursors/merge_cell.png");
    }

    public static Image getScaleCursorIcon()
    {
        return getIcon("cursors/scale_cursor.png");
    }

    public static Image getTranslateCursorIcon()
    {
        return getIcon("cursors/movement_cursor.png");
    }

    public static Image getRotateCursorIcon()
    {
        return getIcon("cursors/rotation_cursor.png");
    }

    public static Image getFrameIcon()
    {
        return getIcon("images/divas-icon.png");
    }

    public static String getVis3DMainClass()
    {
        return getProperty("Vis3DMainClass");
    }

    public static String getVis2DMainClass()
    {
        return getProperty("Vis2DMainClass");
    }
}
