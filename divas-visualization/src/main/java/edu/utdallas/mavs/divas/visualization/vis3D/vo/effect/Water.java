package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

/**
 * This class describes the water showed in the 3D visualization.
 */
public class Water
{
    private static FilterPostProcessor fpp;

    private static WaterFilter         water = new WaterFilter();

    private Water()
    {}

    /**
     * Unliads water from the 3D visualization
     * 
     * @param app
     *        the 3D visualizer application
     */
    public static void unloadWater(Application app)
    {
        ViewPort viewPort = app.getViewPort();
        viewPort.removeProcessor(fpp);
    }

    /**
     * Loads water to the 3D visualization
     * 
     * @param app
     *        the 3D visualizer application
     */
    public static void loadWater(Application app)
    {
        updateWater(app);
    }

    private static void updateWater(Application app)
    {

        AssetManager assetManager = (app.getAssetManager());
        SimpleApplication simpleApp = (SimpleApplication) app;

        Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);

        WaterFilter water = new WaterFilter(simpleApp.getRootNode(), lightDir);
        water.setWaterHeight(-35f);
        TranslucentBucketFilter tbf = new TranslucentBucketFilter();

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(water);
        fpp.addFilter(tbf);

        simpleApp.getViewPort().addProcessor(fpp);
    }

    /**
     * Use this waterheight method for causing waves.
     * The Default is 0.0f
     * 
     * @param waterHeight
     *        height of the water
     */
    public void setWaterHeight(float waterHeight)
    {
        water.setWaterHeight(waterHeight);
    }

    /**
     * How high the highest waves are.
     * The Default is 1.0f
     * 
     * @param maxAmplitude
     *        max amplitude of the water
     */
    public void setMaxAmplitude(float maxAmplitude)
    {
        water.setMaxAmplitude(maxAmplitude);
    }

    /**
     * Sets the scale factor of the waves height map. The smaller the value, the bigger the waves!
     * The Default is 0.005f
     * 
     * @param WaveScale
     *        wave scale
     */
    public void setWaveScale(float WaveScale)
    {
        water.setWaveScale(WaveScale);
    }

    /**
     * Sets the wind direction, which is the direction where the waves move
     * The Default is Vector2f(0.0f, -1.0f)
     * 
     * @param v
     *        direction of the wind
     */
    public void setWindDirection(Vector2f v)
    {
        water.setWindDirection(v);
    }

    /**
     * How fast the waves move. Set it to 0.0f for still water.
     * The Default is 1.0f
     * 
     * @param Speed
     *        speed of the waves
     */
    public void setSpeed(float Speed)
    {
        water.setSpeed(Speed);
    }

    /**
     * This height map describes the shape of the waves
     * The Default is "Common/MatDefs/Water/Textures/heightmap.jpg"
     * 
     * @param tex
     *        wave shape
     */
    public void setHeightTexture(Texture2D tex)
    {
        water.setHeightTexture(tex);
    }

    /**
     * This normal map describes the shape of the waves
     * The Default is "Common/MatDefs/Water/Textures/gradient_map.jpg"
     * 
     * @param tex
     *        wave shape
     */
    public void setNormalTexture(Texture2D tex)
    {
        water.setNormalTexture(tex);
    }

    /**
     * Switches the ripples effect on or off.
     * The Default is true
     * 
     * @param b
     *        riples effect flag
     */
    public void setUseRipples(boolean b)
    {
        water.setUseRipples(b);
    }

    /**
     * Sets the normal scaling factors to apply to the normal map. The higher the value, the more small ripples will be
     * visible on the waves.
     * The Default is 1.0f
     * 
     * @param scale
     *        scaling factor
     */
    public void setNormalScale(float scale)
    {
        water.setNormalScale(scale);
    }

    /**
     * Usually you set this to the same as the light source's direction. Use this to set the light direction if the sun
     * is moving.
     * The Default is Value given to WaterFilter() constructor.
     * 
     * @param v
     *        light direction
     */
    public void setLightDirection(Vector3f v)
    {
        water.setLightDirection(v);
    }

    /**
     * Usually you set this to the same as the light source's color.
     * The Default is RGBA.White
     * 
     * @param color
     *        light source color
     */
    public void setLightColor(ColorRGBA color)
    {
        water.setLightColor(color);
    }

    /**
     * Sets the main water color.
     * The Default is greenish blue Vector3f(0.0f,0.5f,0.5f,1.0f)
     * 
     * @param color
     *        water color
     */
    public void setWaterColor(ColorRGBA color)
    {
        water.setWaterColor(color);
    }

    /**
     * Sets the deep water color.
     * The Default is dark blue Vector3f(0.0f, 0.0f,0.2f,1.0f)
     * 
     * @param color
     *        deep water color
     */
    public void setDeepWaterColor(ColorRGBA color)
    {
        water.setDeepWaterColor(color);
    }

    /**
     * Sets how fast colors fade out. use this to control how clear (e.g. 0.05f) or muddy (0.2f) water is.
     * The Default is 0.1f
     * 
     * @param f
     *        water transparency
     */
    public void setWaterTransparency(float f)
    {
        water.setWaterTransparency(f);
    }

    /**
     * Sets At what depth the refraction color extincts. The three values are RGB (red, green, blue) in this order. Play
     * with these parameters to "muddy" the water.
     * The Default is Vector3f(5f,20f,30f)
     * 
     * @param v
     *        color extinction depth
     */
    public void setColorExtinction(Vector3f v)
    {
        water.setColorExtinction(v);
    }

    /**
     * Sets how soft the transition between shore and water should be. High values mean a harder transition between
     * shore and water.
     * The Default is 0.1f
     * 
     * @param f
     *        shore hardness
     */
    public void setShoreHardness(float f)
    {
        water.setShoreHardness(f);
    }

    /**
     * Renders shoreline with better quality
     * The Default is true
     * 
     * @param b
     *        HQ shore line flag
     */
    public void setUseHQShoreline(boolean b)
    {
        water.setUseHQShoreline(b);
    }

    /**
     * Switches the white foam on or off
     * The Default is true
     * 
     * @param b
     *        white foam flag
     */
    public void setUseFoam(boolean b)
    {
        water.setUseFoam(b);
    }

    /**
     * Sets how much the foam will blend with the shore to avoid a hard edged water plane.
     * The Default is 1.0f
     * 
     * @param f
     *        foam hardness
     */
    public void setFoamHardness(float f)
    {
        water.setFoamHardness(f);
    }

    /**
     * The three values describe what depth foam starts to fade out, at what depth it is completely invisible, at what
     * height foam for waves appears (+ waterHeight).
     * The Default is Vector3f(0.45f,4.35f,1.0f)
     * 
     * @param v
     *        foam depth
     */
    public void setFoamExistence(Vector3f v)
    {
        water.setFoamExistence(v);
    }

    /**
     * This foam texture will be used with WrapMode.Repeat
     * The Default is "Common/MatDefs/Water/Textures/foam.jpg"
     * 
     * @param tex
     *        foam texture
     */
    public void setFoamTexture(Texture2D tex)
    {
        water.setFoamTexture(tex);
    }

    /**
     * Sets how big the sun should appear in the light's specular effect on the water.
     * The Default is 3.0f
     * 
     * @param f
     *        sun scale
     */
    public void setSunScale(float f)
    {
        water.setSunScale(f);
    }

    /**
     * Switches specular effect on or off
     * The Default is true
     * 
     * @param b
     *        flag indicating if secular should be used
     */
    public void setUseSpecular(boolean b)
    {
        water.setUseSpecular(b);
    }

    /**
     * Sets the shininess of the water reflections
     * The Default is 0.7f
     * 
     * @param f
     *        shininess
     */
    public void setShininess(float f)
    {
        water.setShininess(f);
    }

    /**
     * Switches the refraction effect on or off.
     * The Default is true
     * 
     * @param b
     *        flag indicating if refraction should be used
     */
    public void setUseRefraction(boolean b)
    {
        water.setUseRefraction(b);
    }

    /**
     * The lower the value, the less reflection can be seen on water. This is a constant related to the index of
     * refraction (IOR) used to compute the fresnel term.
     * The Default is 0.3f
     * 
     * @param f
     *        refraction constant
     */
    public void setRefractionConstant(float f)
    {
        water.setRefractionConstant(f);
    }

    /**
     * This value modifies the current Fresnel term. If you want to weaken reflections use bigger value. If you want to
     * empasize them, use a value smaller than 0.
     * The Default is 0.0f
     * 
     * @param f
     *        refraction constant
     */
    public void setRefractionStrength(float f)
    {
        water.setRefractionStrength(f);
    }

    /**
     * Sets the size of the reflection map. The higher, the better the quality, but the slower the effect.
     * The Default is 512
     * 
     * @param i
     *        refplection map size
     */
    public void setReflectionMapSize(int i)
    {
        water.setReflectionMapSize(i);
    }

}
