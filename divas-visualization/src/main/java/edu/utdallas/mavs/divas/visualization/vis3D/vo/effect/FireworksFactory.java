package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;

/**
 * This class implements a factory for fireworks effects.
 */
public class FireworksFactory
{
    private static final String  POINT_SPRITE_PARAM = "PointSprite";
    private static final String  TEXTURE_PARAM      = "Texture";

    private static final int     COUNT_FACTOR       = 5;
    private static final float   COUNT_FACTOR_F     = 1f;
    private static final boolean POINT_SPRITE       = true;
    private static final Type    EMITTER_TYPE       = POINT_SPRITE ? Type.Point : Type.Triangle;

    private static AssetManager  assetManager       = Visualizer3DApplication.getInstance().getAssetManager();

    /**
     * Creates a particle emitter for flame according to a given intensity
     * 
     * @param intensity the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createFlash()
    {
        ParticleEmitter flash = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        flash.setSelectRandomImage(true);
        // flash.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1f /
        // COUNT_FACTOR_F)));
        // flash.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        flash.setStartColor(ColorRGBA.White);
        // flash.setEndColor(ColorRGBA.Blue);
        flash.setStartSize(.1f);
        flash.setEndSize(.5f);
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO, .25f));
        flash.setParticlesPerSec(0);
        flash.setGravity(new Vector3f(0f, 0f, 0f));
        flash.setLowLife(.2f);
        flash.setHighLife(.2f);
        flash.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 10f, 0f));
        flash.getParticleInfluencer().setVelocityVariation(1f);
        flash.setImagesX(2);
        flash.setImagesY(2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/flash.png"));
        mat.setBoolean(POINT_SPRITE_PARAM, POINT_SPRITE);
        flash.setMaterial(mat);

        return flash;
    }

    /**
     * Creates a particle emitter for round spark according to a given intensity
     * 
     * @param intensity the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createRoundSpark()
    {
        ParticleEmitter roundspark = new ParticleEmitter("RoundSpark", EMITTER_TYPE, 10 * COUNT_FACTOR);
        roundspark.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, (float) (1.0 / COUNT_FACTOR_F)));
        roundspark.setEndColor(new ColorRGBA(0, 0, 0, 0.5f / COUNT_FACTOR_F));
        // roundspark.setStartColor(ColorRGBA.Blue);
        // roundspark.setEndColor(ColorRGBA.Blue);
        roundspark.setStartSize(.4f);
        roundspark.setEndSize(2.4f);
        roundspark.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        roundspark.setParticlesPerSec(0);
        roundspark.setGravity(new Vector3f(0f, 1f, 0f));
        roundspark.setLowLife(1.5f);
        roundspark.setHighLife(1.8f);
        roundspark.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 3f, 0f));
        roundspark.getParticleInfluencer().setVelocityVariation(0.5f);
        roundspark.setImagesX(1);
        roundspark.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/roundspark.png"));
        mat.setBoolean(POINT_SPRITE_PARAM, POINT_SPRITE);
        roundspark.setMaterial(mat);

        return roundspark;
    }

    /**
     * Creates a particle emitter for spark according to a given intensity
     * 
     * @param intensity the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createSpark()
    {
        ParticleEmitter spark = new ParticleEmitter("Spark", Type.Triangle, 30 * COUNT_FACTOR);
        // spark.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, (float) (1.0f /
        // COUNT_FACTOR_F)));
        // spark.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        spark.setStartColor(ColorRGBA.White);
        spark.setStartSize(.5f);
        spark.setEndSize(.5f);
        // spark.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        spark.setFacingVelocity(true);
        spark.setParticlesPerSec(0);
        spark.setGravity(new Vector3f(0f, 1f, 0f));
        spark.setLowLife(1.1f);
        spark.setHighLife(1.5f);
        spark.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 6f, 0f));
        spark.getParticleInfluencer().setVelocityVariation(1f);
        spark.setImagesX(1);
        spark.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/spark.png"));
        spark.setMaterial(mat);

        return spark;
    }

}
