package edu.utdallas.mavs.divas.visualization.vis3D.dialog;

import java.io.InputStream;
import java.net.URL;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.InputSystemJme;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.niftygui.RenderDeviceJme;
import com.jme3.niftygui.SoundDeviceJme;
import com.jme3.renderer.ViewPort;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import de.lessvoid.nifty.tools.resourceloader.ResourceLocation;

/**
 * Hack to integrate JME with the update versions of the nifty gui. To be removed after the JME project fixes this issue.
 */
@SuppressWarnings("javadoc")
public class NiftyDisplay extends NiftyJmeDisplay
{
    protected ResourceLocationJme resourceLocation;

    protected class ResourceLocationJme implements ResourceLocation
    {

        @Override
        public InputStream getResourceAsStream(String path)
        {
            AssetKey<Object> key = new AssetKey<Object>(path);
            AssetInfo info = assetManager.locateAsset(key);
            if(info != null)
            {
                return info.openStream();
            }
            throw new AssetNotFoundException(path);
        }

        @Override
        public URL getResource(String path)
        {
            throw new UnsupportedOperationException();
        }
    }

    // Empty constructor needed for jMP to create replacement input system
    public NiftyDisplay()
    {}

    public NiftyDisplay(AssetManager assetManager, InputManager inputManager, AudioRenderer audioRenderer, ViewPort vp)
    {
        this.assetManager = assetManager;

        w = vp.getCamera().getWidth();
        h = vp.getCamera().getHeight();

        soundDev = new SoundDeviceJme(assetManager, audioRenderer);
        renderDev = new RenderDeviceJme(this);
        inputSys = new InputSystemJme(inputManager);
        if(inputManager != null)
        {
            inputManager.addRawInputListener(inputSys);
        }

        nifty = new Nifty(renderDev, soundDev, inputSys, new AccurateTimeProvider());
        inputSys.setNifty(nifty);

        resourceLocation = new ResourceLocationJme();
        nifty.getResourceLoader().removeAllResourceLocations();
        nifty.getResourceLoader().addResourceLocation(resourceLocation);
    }
}
