package edu.utdallas.mavs.divas.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class DisplayResolution implements Comparable<DisplayResolution>, Serializable
{
    private static final long serialVersionUID = 1L;
    public int                width;
    public int                height;
    public int                depth;
    public int                frequency;

    public DisplayResolution(int width, int height, int depth, int frequency)
    {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o)
    {
        if(width == ((DisplayResolution) o).width && height == ((DisplayResolution) o).height)
            return true;
        return false;
    }

    @Override
    public int compareTo(DisplayResolution o)
    {
        int dif = o.width - width;
        if(dif != 0)
            return dif;

        dif = o.height - height;
        if(dif != 0)
            return dif;

        dif = o.depth - depth;
        if(dif != 0)
            return dif;

        return o.frequency - frequency;
    }

    @Override
    public String toString()
    {
        return width + " x " + height + " x " + depth + " @ " + frequency + "Hz";
    }

    public static DisplayResolution[] getBestSupportedResolutions()
    {
        try
        {
            ArrayList<DisplayResolution> resolutions = new ArrayList<DisplayResolution>();
            DisplayMode[] modes = Display.getAvailableDisplayModes();

            for(DisplayMode mode : modes)
            {
                DisplayResolution resolution = new DisplayResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(), mode.getFrequency());

                int i = resolutions.indexOf(resolution);
                if(i < 0)
                    resolutions.add(resolution);
                else
                {
                    int diff = resolutions.get(i).compareTo(resolution);
                    if(diff > 0)
                    {
                        resolutions.remove(i);
                        resolutions.add(resolution);
                    }
                }
            }

            DisplayResolution[] res = new DisplayResolution[resolutions.size()];
            resolutions.toArray(res);
            Arrays.sort(res);
            return res;
        }
        catch(LWJGLException e)
        {
            // e.printStackTrace();
            return null;
        }
    }

    public static DisplayResolution getBestSupportedResolution()
    {
        DisplayResolution[] resolutions = getBestSupportedResolutions();
        if(resolutions != null)
            return resolutions[0];
        return null;
    }

    public static DisplayResolution[] getAllSupportedResolutions()
    {
        try
        {
            ArrayList<DisplayResolution> resolutions = new ArrayList<DisplayResolution>();
            DisplayMode[] modes = Display.getAvailableDisplayModes();

            for(DisplayMode mode : modes)
            {
                DisplayResolution resolution = new DisplayResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(), mode.getFrequency());
                resolutions.add(resolution);
            }

            DisplayResolution[] res = new DisplayResolution[resolutions.size()];
            resolutions.toArray(res);
            Arrays.sort(res);
            return res;
        }
        catch(LWJGLException e)
        {
            // e.printStackTrace();
            return null;
        }
    }
}
