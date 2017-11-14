package edu.utdallas.mavs.divas.visualization.vis3D.engine;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.texture.Image;

import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;

/**
 * This class describes the hardware cursor manager of the 3D visualizer.
 */
public class CursorManager
{
    private final static Logger       logger        = LoggerFactory.getLogger(CursorManager.class);

    protected HashMap<String, Cursor> loadedCursors = new HashMap<String, Cursor>();
    protected AssetManager            assetManager;
    protected CursorType              currentCursorType;

    /**
     * Creates and initializer a cursor manager
     * 
     * @param assetmanager the applicaiton's asset manager
     * @param url
     *        to imagefile
     * @param xHotspot
     *        from image left
     * @param yHotspot
     *        from image bottom
     */
    public CursorManager(AssetManager assetmanager)
    {
        this.assetManager = assetmanager;
        setCursor(CursorType.ARROW);
    }

    private synchronized void precache(String file, int xHotspot, int yHotspot)
    {
        if(assetManager == null)
        {
            throw new RuntimeException("CursorManager not initialized");
        }

        try
        {
            Image image = assetManager.loadTexture(file).getImage();

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            if(imageWidth % 16 != 0 || imageHeight % 16 != 0)
            {
                throw new RuntimeException("Cursor must be multiple of 16");
            }

            IntBuffer imageDataCopy;

            ByteBuffer imageData = image.getData(0);
            imageDataCopy = ((ByteBuffer) imageData.rewind()).asIntBuffer();
            if(xHotspot < 0 || yHotspot < 0 || xHotspot >= imageWidth || yHotspot >= imageHeight)
            {
                xHotspot = 0;
                yHotspot = imageHeight - 1;
            }

            // handles RGB images
            boolean isRgba = image.getFormat() == Image.Format.RGBA8;
            boolean eightBitAlpha = (Cursor.getCapabilities() & Cursor.CURSOR_8_BIT_ALPHA) != 0;
            for(int y = 0; y < imageHeight; y++)
            {
                for(int x = 0; x < imageWidth; x++)
                {
                    int index = y * imageWidth + x;

                    int r = imageData.get() & 0xff;
                    int g = imageData.get() & 0xff;
                    int b = imageData.get() & 0xff;
                    int a = 0xff;
                    if(isRgba)
                    {
                        a = imageData.get() & 0xff;
                        if(!eightBitAlpha)
                        {
                            if(a < 0x7f)
                            {
                                a = 0x00;
                                // small hack to prevent triggering "reverse screen" on windows.
                                r = g = b = 0;
                            }
                            else
                            {
                                a = 0xff;
                            }
                        }
                    }

                    imageDataCopy.put(index, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }

            Cursor cursor = new Cursor(imageWidth, imageHeight, xHotspot, yHotspot, 1, imageDataCopy, null);
            loadedCursors.put(file, cursor);
        }
        catch(LWJGLException e)
        {
            e.printStackTrace();
        }
        catch(AssetNotFoundException e)
        {
            logger.warn("No asset found for cursor " + file);
        }

    }

    protected synchronized void setHardwareCursor(String file, int xHotspot, int yHotspot)
    {
        Cursor cursor = loadedCursors.get(file);
        if(cursor == null)
        {
            precache(file, xHotspot, yHotspot);
            cursor = loadedCursors.get(file);
        }
        try
        {
            if(cursor != null && !cursor.equals(Mouse.getNativeCursor()))
            {
                Mouse.setNativeCursor(cursor);
            }
        }
        catch(LWJGLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the cursor to display in the 3D visualization. It changes the default cursor to be one of the different
     * customized types defined in the {@link CursorType} enumeration.
     * 
     * @param type the cursor type, to be displayed as the mouse cursor controlled by the user
     */
    public synchronized void setCursor(CursorType type)
    {
        if(currentCursorType != null && currentCursorType.equals(type))
            return;

        currentCursorType = type;
        setCursorType(type);
    }

    protected void setCursorType(CursorType type)
    {
        if(type.equals(CursorType.HAND))
        {
            setHardwareCursor("/cursors/hand_cursor_8.png", -1, -4);
        }
        else if(type.equals(CursorType.AGENT))
        {
            setHardwareCursor("/cursors/arrows/blackman_8.png", -1, -4);
        }
        else if(type.equals(CursorType.OBJECT))
        {
            setHardwareCursor("/cursors/arrows/object_8.png", -1, -4);
        }
        else if(type.equals(CursorType.COPY))
        {
            setHardwareCursor("/cursors/arrows/arrowPlus_8.png", -1, -4);
        }
        else if(type.equals(CursorType.MOVE))
        {
            setHardwareCursor("/cursors/arrows/move_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_X))
        {
            setHardwareCursor("/cursors/arrows/scaleX_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_Y))
        {
            setHardwareCursor("/cursors/arrows/scaleY_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_Z))
        {
            setHardwareCursor("/cursors/arrows/scaleZ_8.png", -1, -4);
        }
        else if(type.equals(CursorType.ARROW))
        {
            setHardwareCursor("/cursors/arrows/arrow_8.png", -1, -4);
        }
        else
        {
            setHardwareCursor("/cursors/arrows/arrow_8.png", -1, -4);
        }
    }
}
