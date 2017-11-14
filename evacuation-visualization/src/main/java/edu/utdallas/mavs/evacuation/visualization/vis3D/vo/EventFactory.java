package edu.utdallas.mavs.evacuation.visualization.vis3D.vo;

import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;

import edu.utdallas.mavs.divas.core.sim.common.event.BombEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.DrumsEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.DynamiteEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.FireworkEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.GrillingFoodEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.SirenEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.SpotlightEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Drum;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Dynamite;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Explosion;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Fireworks;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Grill;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.ShockWave;

/**
 * This class describes a factory for visualized events.
 */
public class EventFactory
{
    /**
     * This is a factory method for visualized events.
     * 
     * @param event
     *        an event to be factored into a visualized event
     * @return a spatial associated with the newly created visualized event
     */
    public static Spatial createEventVO(EnvEvent event)
    {
        Spatial vo = null;
        if(event instanceof FireworkEvent)
        {
            vo = createFireworksVO(event);
        }
        else if(event instanceof BombEvent)
        {
            vo = cretateExplosionVO(event);
        }
        else if(event instanceof GrillingFoodEvent)
        {
            vo = createGrillVO(event);
        }
        else if(event instanceof DrumsEvent)
        {
            vo = createDrumsVO(event);
        }
        else if(event instanceof SpotlightEvent)
        {
            vo = createSpotlight(event);
        }
        else if(event instanceof SirenEvent)
        {
            vo = createSiren(event);
        }
        else if(event instanceof DynamiteEvent)
        {
            vo = createDynamite(event);
        }
        return vo;
    }


    private static Spatial createSpotlight(EnvEvent event)
    {
        Node n = new Node();
        n.setLocalTranslation(event.getOrigin().add(0, 1.1f, 0));
        String path = "objects/" + "light7.mesh" + ".j3o";

        Spatial model = Visualizer3DApplication.getInstance().getAssetManager().loadModel(path);

        model.setLocalScale(.005f);

        n.attachChild(model);

        Sphere box1 = new Sphere(50, 50, .4f);
        Geometry vo = new Geometry("Box", box1);
        vo.setLocalTranslation(0, 6.2f, .8f);
        Material mat1 = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.White);
        mat1.setColor("GlowColor", ColorRGBA.White);
        vo.setMaterial(mat1);
        AmbientLight agentLight = VisToolbox.createAmbientLight(ColorRGBA.White);
        vo.addLight(agentLight);
        n.attachChild(vo);

        Dome cone = new Dome(Vector3f.ZERO, 2, 30, 1f, false);
        Geometry geometry = new Geometry("VisionCone", cone);

        Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.setColor("Color", new ColorRGBA(1.0f, 0.2f, 0, alpha));
        ColorRGBA visionColor = ColorRGBA.White;
        mat.setColor("Color", new ColorRGBA(visionColor.r, visionColor.g, visionColor.b, .1f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);

        geometry.setMaterial(mat);

        geometry.setQueueBucket(Bucket.Translucent);
        geometry.setLocalScale(new Vector3f(25, 50, 25));
        geometry.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_X));
        geometry.setLocalTranslation(0, 6.2f, 50);
        n.attachChild(geometry);

        cone = new Dome(Vector3f.ZERO, 2, 30, 1f, true);
        geometry = new Geometry("VisionCone", cone);

        mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.setColor("Color", new ColorRGBA(1.0f, 0.2f, 0, alpha));
        visionColor = ColorRGBA.White;
        mat.setColor("Color", new ColorRGBA(visionColor.r, visionColor.g, visionColor.b, .1f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);

        geometry.setMaterial(mat);

        geometry.setQueueBucket(Bucket.Translucent);
        geometry.setLocalScale(new Vector3f(25, 50, 25));
        geometry.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_X));
        geometry.setLocalTranslation(0, 6.2f, 50);
        n.attachChild(geometry);

        return n;
    }

    private static Spatial createDrumsVO(EnvEvent event)
    {
        Drum vo = new Drum((DrumsEvent) event);
        return vo;
    }

    private static Spatial createGrillVO(EnvEvent event)
    {
        Grill vo = new Grill((GrillingFoodEvent) event);
        return vo;
    }

    private static Spatial cretateExplosionVO(EnvEvent event)
    {
        Explosion vo = new Explosion((BombEvent) event);
        event.setIntensity(10f);
        return vo;
    }

    private static Spatial createDynamite(EnvEvent event)
    {
        Dynamite vo = new Dynamite((DynamiteEvent) event);
        event.setIntensity(17f);
        return vo;
    }

    private static Spatial createSiren(EnvEvent event)
    {
        ShockWave vo = new ShockWave((SirenEvent) event);
        return vo;
    }

    private static Spatial createFireworksVO(EnvEvent event)
    {
        Fireworks vo = new Fireworks(event);
        event.setIntensity(25f);
        return vo;
    }
}
