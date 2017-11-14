package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;

/**
 * This class implements a factory for smoke effects.
 */
public class SmokeFactory
{
    private static final String TEXTURE_PARAM = "Texture";

    private static AssetManager assetManager  = Visualizer3DApplication.getInstance().getAssetManager();

    /**
     * Creates a smoke effect accroding to a given intensity
     * 
     * @param intensity the intensity of the smoke
     * @return the particle emitter for the smoke effect
     */
    public static ParticleEmitter createSmoke(float intensity)
    {

        if(intensity > 40f)
        {
            intensity = (float) ((Math.log10(intensity) / Math.log10(1.3)));
        }
        else if(intensity > 20f)
        {
            intensity = (float) ((Math.log10(intensity) / Math.log10(1.5)));
        }
        else if(intensity > 10f)
        {
            intensity = (float) ((Math.log10(intensity) / Math.log10(1.6)));
        }
        else
        {
            intensity = (float) ((Math.log10(intensity) / Math.log10(2)));
        }

        ParticleEmitter smoke = new ParticleEmitter("Smoke", Type.Triangle, 1000);
        smoke.setStartColor(ColorRGBA.DarkGray);
        smoke.setEndColor(ColorRGBA.Black);
        smoke.setStartSize(1.5f * intensity);
        smoke.setEndSize(1.5f * intensity);
        smoke.setGravity(new Vector3f(0f, 0f, 0f));
        smoke.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f * intensity));
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(.5f * intensity, 0f, 0f));
        smoke.getParticleInfluencer().setVelocityVariation(1f);
        smoke.setImagesX(15);
        smoke.setImagesY(1);
        smoke.setLowLife(10f);
        smoke.setHighLife(30f);
        smoke.setParticlesPerSec(0f);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(Vector3f.ZERO.normalizeLocal());
        sun.setColor(ColorRGBA.White);

        smoke.addLight(sun);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Smoke/Smoke.png"));
        smoke.setMaterial(mat);

        return smoke;
    }
}
