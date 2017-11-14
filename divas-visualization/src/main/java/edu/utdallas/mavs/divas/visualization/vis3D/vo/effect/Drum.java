package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import edu.utdallas.mavs.divas.core.sim.common.event.DrumsEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class represents an explosion visualized event
 */
public class Drum extends ParticleEmmitterEffect
{
    ParticleEmitter    shockwave;

    /**
     * Creates a new explosion visualized event
     * 
     * @param event
     *        the event associated with the explosion visualized event
     */
    public Drum(DrumsEvent event)
    {
        super();
        String path = "objects/" + "drum_3" + ".j3o";

        Spatial model = Visualizer3DApplication.getInstance().getAssetManager().loadModel(path);

        model.setLocalScale(.020f);
        model.setLocalTranslation(0, 1.1f, 0);
        attachChild(model);

        AmbientLight drumLight = VisToolbox.createAmbientLight(ColorRGBA.White.mult(2f));
        addLight(drumLight);

        shockwave = new ParticleEmitter("Shockwave", Type.Triangle, 1);
        shockwave.setFaceNormal(Vector3f.UNIT_Y);
        shockwave.setStartColor(new ColorRGBA(.9f, .9f, .9f, .8f));
        shockwave.setEndColor(new ColorRGBA(.9f, .9f, .9f, 0f));
        shockwave.setStartSize(0f);
        shockwave.setEndSize(20f);
        shockwave.setParticlesPerSec(0);
        shockwave.setGravity(new Vector3f(0f, 0f, 0f));
        shockwave.setLowLife(0.7f);
        shockwave.setHighLife(0.7f);
        shockwave.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 0f, 0f));
        shockwave.getParticleInfluencer().setVelocityVariation(0f);
        shockwave.setImagesX(1);
        shockwave.setImagesY(1);
        Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", Visualizer3DApplication.getInstance().getAssetManager().loadTexture("Effects/Explosion/shockwave.png"));
        shockwave.setMaterial(mat);
        shockwave.setLocalTranslation(0, 2.45f, 0);
        attachChild(shockwave);

        // ParticleEmitter fire = new ParticleEmitter("Emitter", Type.Triangle, 50);
        // Material mat_red = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        // mat_red.setTexture("Texture", Visualizer3DApplication.getInstance().getAssetManager().loadTexture("Effects/Explosion/flame.png"));
        // fire.setMaterial(mat_red);
        // fire.setImagesX(2);
        // fire.setImagesY(2); // 2x2 texture animation
        // fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f)); // red
        // fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        // fire.getParticleInfluencer().setInitialVelocity(new Vector3f(.7f, 1.1f, .7f));
        // fire.setStartSize(.55f);
        // fire.setEndSize(0.065f);
        // fire.setGravity(0, 0, 0);
        // fire.setLowLife(.5f);
        // fire.setHighLife(1.2f);
        // fire.getParticleInfluencer().setVelocityVariation(0.3f);
        // fire.setLocalTranslation(0, 1.75f, 0);
        // attachChild(fire);
        //
        // ParticleEmitter smoke = new ParticleEmitter("Emitter", Type.Triangle, 700);
        // Material matSmo = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        // matSmo.setTexture("Texture", Visualizer3DApplication.getInstance().getAssetManager().loadTexture("Effects/Smoke/Smoke.png"));
        // smoke.setMaterial(matSmo);
        // smoke.setImagesX(15);
        // smoke.setImagesY(1);
        // smoke.setEndColor(ColorRGBA.DarkGray);
        // smoke.setStartColor(new ColorRGBA(1f, 1f, 1f, 0.0f));
        // smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 1.8f, 0f));
        // smoke.setStartSize(.55f);
        // smoke.setEndSize(2.065f);
        // smoke.setGravity(0, 0, 0);
        // smoke.setLowLife(8f);
        // smoke.setHighLife(12f);
        // smoke.getParticleInfluencer().setVelocityVariation(0.3f);
        // smoke.setLocalTranslation(0, 2.45f, 0);
        // attachChild(smoke);

        // blackSmoke.setQueueBucket(Bucket.Translucent);
        // System.out.println("Creating SHOCKWAVEEEEF");

    }

    @Override
    public void update(BaseVO parent, float tpf)
    {

        shockwave.emitAllParticles();

    }
}
