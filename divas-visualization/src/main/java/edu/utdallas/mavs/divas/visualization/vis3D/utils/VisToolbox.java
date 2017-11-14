package edu.utdallas.mavs.divas.visualization.vis3D.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;

import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;

/**
 * This class implements a miscellaneous helper for the 3D visualizer.
 */
public class VisToolbox
{
    /**
     * 90 degrees rotation along X axis
     */
    private static final Quaternion yaw90 = new Quaternion().fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X);

    /**
     * Creates a new {@link ColorRGBA} object according to the given {@link Color}
     * 
     * @param awtColor
     *        - the {@link Color} to copy
     * @return the newly-created {@link ColorRGBA} object with values similar to those of the given {@link Color}
     */
    public static ColorRGBA getColor(Color awtColor)
    {
        float[] rgba = new float[4];
        awtColor.getRGBComponents(rgba);
        return new ColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    /**
     * Creates a pseudo-random {@link ColorRGBA} object according to the specified alpha
     * 
     * @param alpha
     *        the alpha component of the created color
     * @return the newly-created {@link ColorRGBA} object
     */
    public static ColorRGBA getRandomColor(float alpha)
    {
        return new ColorRGBA(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), alpha);
    }

    /**
     * Creates a transparent/translucent vision cone for the agent
     * 
     * @param position
     *        the position of the cone (the tip of the cone)
     * @param visibleDistance
     *        the agent's visible distance
     * @param fov
     *        the agent's field of view
     * @param insideView
     *        specifies if the cone can be seen from inside or outside only (these are mutual exclusive properties)
     * @param raysCount
     *        the number of samples of the cones (>30 gives good aspect)
     * @param alpha
     *        the alpha component of the cone's color
     * @return a geometry representing a vision cone
     */
    public static Geometry createVisionCone(Vector3f position, float visibleDistance, float fov, int raysCount, boolean insideView, float alpha)
    {
        // a cone planes=2 radial, samples >=4
        Dome cone = new Dome(Vector3f.ZERO, 2, raysCount, 1f, insideView);
        Geometry geometry = new Geometry("VisionCone", cone);

        Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.setColor("Color", new ColorRGBA(1.0f, 0.2f, 0, alpha));
        ColorRGBA visionColor = getColor(VisConfig.getInstance().vision_Color);
        mat.setColor("Color", new ColorRGBA(visionColor.r, visionColor.g, visionColor.b, visionColor.a + alpha));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);

        geometry.setMaterial(mat);

        geometry.setQueueBucket(Bucket.Translucent);
        // geometry.setLocalScale(new Vector3f(fov, visibleDistance, fov));
        // geometry.setLocalTranslation(position.add(0, 0, -visibleDistance));
        geometry.setLocalScale(new Vector3f(visibleDistance * FastMath.sin(fov * FastMath.DEG_TO_RAD * .5f), visibleDistance * FastMath.cos(fov * FastMath.DEG_TO_RAD * .5f), visibleDistance * FastMath.sin(fov * FastMath.DEG_TO_RAD * .5f)));
        geometry.setLocalTranslation(position.add(0, 0, -visibleDistance * FastMath.cos(fov * FastMath.DEG_TO_RAD * .5f)));
        geometry.rotate(yaw90);

        return geometry;
    }

    /**
     * Creates a transparent/translucent vision cone for the agent
     * 
     * @param position
     *        the position of the cone (the tip of the cone)
     * @param visibleDistance
     *        the agent's visible distance
     * @param fov
     *        the agent's field of view
     * @param circlesCount
     *        number of circles to be drawn
     * @param raysCount
     *        the number of samples of the cones (>30 gives good aspect)
     * @param insideView
     *        specifies if the cone can be seen from inside or outside only (these are mutual exclusive properties)
     * @return a geometry representing a vision cone
     */
    public static List<Geometry> createVisionCone(Vector3f position, float visibleDistance, float fov, int circlesCount, int raysCount)
    {
        List<Geometry> cones = new ArrayList<>();

        float circlesOffset = fov / circlesCount;
        float alpha = 0.1f;

        for(int c = circlesCount; c > 0; c--)
        {
            cones.add(createVisionCone(position, visibleDistance, c * circlesOffset, raysCount, false, alpha));
            cones.add(createVisionCone(position, visibleDistance, c * circlesOffset, raysCount, true, alpha));
            alpha += 0.03f;
        }

        return cones;
    }

    /**
     * Creates a HUD text on the screen
     * 
     * @param id
     *        the id of the component
     * @param text
     *        the text to be displayed
     * @param colorRGBA
     *        color
     * @param screenPosition
     *        screen position
     * @return a new bitmap text
     */
    public static BitmapText createHUDText(String id, String text, ColorRGBA colorRGBA, Vector3f screenPosition)
    {
        BitmapFont guiFont = Visualizer3DApplication.getInstance().getApp().getGuiFont();
        BitmapText selectionId = new BitmapText(guiFont, false);
        selectionId.setName(id);
        selectionId.setText(text);
        selectionId.setSize(guiFont.getCharSet().getRenderedSize() - 2f);
        selectionId.setCullHint(Spatial.CullHint.Never);
        selectionId.setLocalScale(1f);
        selectionId.setColor(colorRGBA.clone());
        selectionId.setLocalTranslation(screenPosition);
        selectionId.setQueueBucket(Bucket.Gui);
        return selectionId;
    }

    /**
     * Creates an ambient light to be used in the visualization
     * 
     * @param color
     * @return a new ambient light
     */
    public static AmbientLight createAmbientLight(ColorRGBA color)
    {
        AmbientLight selectionLight = new AmbientLight();
        selectionLight.setColor(color);
        selectionLight.setName("selection_light_" + color.toString());
        return selectionLight;
    }

    public static List<Geometry> createAgentPath(Vector3f position, List<Vector3f> list, ColorRGBA color)
    {
        List<Geometry> paths = new ArrayList<>();

        // for(int i = 0; i < 100; i++)
        // {
        // Sphere path = new Sphere(30, 30, 1);
        // Geometry geometry = new Geometry("AgentPathF", path);
        // Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        // mat.setColor("Color", color);
        // geometry.setMaterial(mat);
        //
        // geometry.setQueueBucket(Bucket.Translucent);
        //
        // geometry.setLocalTranslation(position.add(Vector3f.UNIT_Y.mult(i)));
        // paths.add(geometry);
        // }

        int lastI = -1;
        for(int i = 0; i < list.size(); i++)
        {

            Vector3f vec = list.get(i);

            if(vec != null)
            {
                Vector3f last;
                if(lastI == -1)
                {
                    last = position;
                }
                else
                {
                    last = list.get(lastI);
                }

                Cylinder path = new Cylinder(30, 30, 1, last.distance(vec), true);
                // Sphere path = new Sphere(30, 30, 3);
                Geometry geometry = new Geometry("AgentPath" + i, path);
                Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                mat.setColor("Color", color);
                geometry.setMaterial(mat);

                geometry.setQueueBucket(Bucket.Translucent);

                geometry.setLocalTranslation(vec.add(last.subtract(vec).mult(.5f)).add(Vector3f.UNIT_Y.mult(2)));
                geometry.lookAt(vec.add(Vector3f.UNIT_Y.mult(2)), Vector3f.UNIT_Y);

                lastI = i;
                paths.add(geometry);
            }
        }

        if(list.size() > 0)
        {
            Sphere path = new Sphere(30, 30, 3);
            Geometry geometry = new Geometry("AgentPathF", path);
            Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.setColor("Color", color);
            geometry.setMaterial(mat);

            geometry.setQueueBucket(Bucket.Translucent);

            geometry.setLocalTranslation(list.get(list.size() - 1));
            paths.add(geometry);
        }
        return paths;
    }
}
