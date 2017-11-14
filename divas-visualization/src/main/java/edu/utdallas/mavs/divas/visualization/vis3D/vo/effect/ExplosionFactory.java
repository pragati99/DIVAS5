package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;

/**
 * This class implements a factory for explosion effects.
 */
public class ExplosionFactory
{
    private static final String  POINT_SPRITE_PARAM = "PointSprite";
    private static final String  TEXTURE_PARAM      = "Texture";
    private static final int     COUNT_FACTOR       = 1;
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
    public static ParticleEmitter createFlame(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        float duration = ((intensity * (1f / 3f)) < 1f) ? 1f : intensity * 1f / 3f;
        ParticleEmitter flame = new ParticleEmitter("Flame", EMITTER_TYPE, 32 * COUNT_FACTOR);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, 1f / COUNT_FACTOR_F));
        flame.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        flame.setStartSize(0.5f * intensity);
        flame.setEndSize(2f * intensity);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f * intensity));
        flame.setParticlesPerSec(0);
        flame.setGravity(new Vector3f(0f, -5f, 0f));
        flame.setLowLife(.4f * duration);
        flame.setHighLife(.5f * duration);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 7f, 0f));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        // mat.setTexture("m_Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean(POINT_SPRITE_PARAM, POINT_SPRITE);
        flame.setMaterial(mat);
        return flame;
    }

    /**
     * Creates a particle emitter for flame according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createFlameD(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        float duration = ((intensity * (1f / 3f)) < 1f) ? 1f : intensity * 1f / 3f;
        ParticleEmitter flame = new ParticleEmitter("Flame", EMITTER_TYPE, 32 * COUNT_FACTOR);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, 0.7f, 0.35f, 1f / COUNT_FACTOR_F));
        flame.setEndColor(new ColorRGBA(.4f, .32f, .22f, 0f));
        flame.setStartSize(0.5f * intensity);
        flame.setEndSize(2f * intensity);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f * intensity));
        flame.setParticlesPerSec(0);
        flame.setGravity(new Vector3f(0f, -5f, 0f));
        flame.setLowLife(.4f * duration);
        flame.setHighLife(.5f * duration);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 7f, 0f));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        // mat.setTexture("m_Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/flame.png"));
        mat.setBoolean(POINT_SPRITE_PARAM, POINT_SPRITE);
        flame.setMaterial(mat);
        return flame;
    }

    /**
     * Creates a particle emitter for flash according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createFlash(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        ParticleEmitter flash = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        flash.setSelectRandomImage(true);
        flash.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f / COUNT_FACTOR_F));
        flash.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        flash.setStartSize(.1f * intensity);
        flash.setEndSize(3.0f * intensity);
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f * intensity));
        flash.setParticlesPerSec(0);
        flash.setGravity(new Vector3f(0f, 0f, 0f));
        flash.setLowLife(.2f);
        flash.setHighLife(.2f);
        flash.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 5f, 0f));
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
     * Creates a particle emitter for flash according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createFlashD(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        ParticleEmitter flash = new ParticleEmitter("Flash", EMITTER_TYPE, 24 * COUNT_FACTOR);
        flash.setSelectRandomImage(true);
        flash.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f / COUNT_FACTOR_F));
        flash.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        flash.setStartSize(.1f * intensity);
        flash.setEndSize(3.0f * intensity);
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f * intensity));
        flash.setParticlesPerSec(0);
        flash.setGravity(new Vector3f(0f, 0f, 0f));
        flash.setLowLife(.2f);
        flash.setHighLife(.2f);
        flash.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 5f, 0f));
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
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createRoundSpark(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        ParticleEmitter roundspark = new ParticleEmitter("RoundSpark", EMITTER_TYPE, 20 * COUNT_FACTOR);
        roundspark.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, (float) (1.0 / COUNT_FACTOR_F)));
        roundspark.setEndColor(new ColorRGBA(0, 0, 0, 0.5f / COUNT_FACTOR_F));
        roundspark.setStartSize(1.2f);
        roundspark.setEndSize(1.8f);
        // roundspark.setNumParticles(numParticles);
        roundspark.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f * intensity));
        roundspark.setParticlesPerSec(0);
        roundspark.setGravity(new Vector3f(0f, -0.5f, 0f));
        roundspark.setLowLife(1.8f * intensity);
        roundspark.setHighLife(2f * intensity);
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
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createSpark(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        int numParticles = (intensity < 1) ? 1 : (int) intensity * 20;
        float size = (intensity * .5f) <= 1f ? 1f : intensity * .5f;
        float velocity = (intensity <= 1f) ? 1f : intensity * (2f / 3f);
        ParticleEmitter spark = new ParticleEmitter("Spark", Type.Triangle, 30 * COUNT_FACTOR);
        spark.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f / COUNT_FACTOR_F));
        spark.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        spark.setStartSize(.5f * size);
        spark.setEndSize(.5f * size);
        spark.setNumParticles(numParticles);
        spark.setFacingVelocity(true);
        spark.setParticlesPerSec(0);
        spark.setGravity(new Vector3f(0f, 5f, 0f));
        spark.setLowLife(1.1f * intensity);
        spark.setHighLife(1.5f * intensity);
        spark.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 20f * velocity, 0f));
        spark.getParticleInfluencer().setVelocityVariation(0.5f);
        spark.setImagesX(1);
        spark.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/spark.png"));
        spark.setMaterial(mat);
        return spark;
    }

    /**
     * Creates a particle emitter for smoke trail according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createSmokeTrail(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        float velocity = (intensity <= 1f) ? 1f : intensity * (2f / 3f);
        ParticleEmitter smoketrail = new ParticleEmitter("SmokeTrail", Type.Triangle, 22 * COUNT_FACTOR);
        smoketrail.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f / COUNT_FACTOR_F));
        smoketrail.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        smoketrail.setStartSize(.2f * intensity);
        smoketrail.setEndSize(1f * intensity);
        smoketrail.setFacingVelocity(true);
        smoketrail.setParticlesPerSec(0);
        smoketrail.setGravity(new Vector3f(0f, 1f, 0f));
        smoketrail.setLowLife(.4f * intensity);
        smoketrail.setHighLife(.5f * intensity);
        smoketrail.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 12f * velocity, 0f));
        smoketrail.getParticleInfluencer().setVelocityVariation(1f);
        smoketrail.setImagesX(1);
        smoketrail.setImagesY(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/smoketrail.png"));
        smoketrail.setMaterial(mat);
        return smoketrail;
    }

    /**
     * Creates a particle emitter for debris according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createDebris(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        int numParticles = (intensity < 1) ? 1 : (int) intensity * 10;
        float velocity = (intensity <= 1f) ? 1f : intensity * (2f / 3f);
        ParticleEmitter debris = new ParticleEmitter("Debris", Type.Triangle, 15 * COUNT_FACTOR);
        debris.setSelectRandomImage(true);
        debris.setRandomAngle(true);
        debris.setRotateSpeed(FastMath.TWO_PI * 4);
        // debris.setStartColor(new ColorRGBA(1f, 0.59f, 0.28f, (float) (1.0f /
        // COUNT_FACTOR_F)));
        // debris.setEndColor(new ColorRGBA(.5f, 0.5f, 0.5f, 0f));
        debris.setStartColor(ColorRGBA.DarkGray);
        debris.setEndColor(ColorRGBA.DarkGray);
        debris.setStartSize(.2f * intensity);
        debris.setEndSize(.2f * intensity);
        debris.setNumParticles(numParticles);
        debris.setParticlesPerSec(0);
        debris.setGravity(new Vector3f(0f, 12f, 0f));
        debris.setLowLife(1.4f * intensity);
        debris.setHighLife(1.5f * intensity);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 15f * velocity, 0f));
        debris.getParticleInfluencer().setVelocityVariation(.60f);
        debris.setImagesX(3);
        debris.setImagesY(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/Debris.png"));
        // mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        debris.setMaterial(mat);
        return debris;
    }

    /**
     * Creates a particle emitter for debris according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createDebrisD(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        int numParticles = (intensity < 1) ? 1 : (int) intensity * 50;
        ParticleEmitter debris = new ParticleEmitter("Debris", Type.Triangle, 15 * COUNT_FACTOR);
        debris.setSelectRandomImage(true);
        debris.setRandomAngle(true);
        debris.setRotateSpeed(FastMath.TWO_PI * 4);        
        debris.setStartColor(ColorRGBA.Orange);
        debris.setEndColor(ColorRGBA.DarkGray);
        debris.setStartSize(2f);
        debris.setEndSize(2f);
        debris.setNumParticles(numParticles);
        debris.setParticlesPerSec(0);
        debris.setGravity(new Vector3f(0f, 12f, 0f));
        debris.setLowLife(1.4f * 3);
        debris.setHighLife(1.5f * intensity);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 15f * 5, 0f));
        debris.getParticleInfluencer().setVelocityVariation(.60f);
        debris.setImagesX(3);
        debris.setImagesY(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/Debris.png"));
        // mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        debris.setMaterial(mat);
        return debris;
    }

    /**
     * Creates a particle emitter for shockwave according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createShockwave(float intensity)
    {
        intensity = (intensity < 0) ? 1f : intensity;
        ParticleEmitter shockwave = new ParticleEmitter("Shockwave", Type.Triangle, 1 * COUNT_FACTOR);
        shockwave.setFaceNormal(Vector3f.UNIT_Y);
        shockwave.setStartColor(new ColorRGBA(.48f, 0.17f, 0.01f, .8f / COUNT_FACTOR_F));
        shockwave.setEndColor(new ColorRGBA(.48f, 0.17f, 0.01f, 0f));
        shockwave.setStartSize(0f * intensity);
        shockwave.setEndSize(7f * intensity);
        shockwave.setParticlesPerSec(0);
        shockwave.setGravity(new Vector3f(0f, 0f, 0f));
        shockwave.setLowLife(0.5f);
        shockwave.setHighLife(0.5f);
        shockwave.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 0f, 0f));
        shockwave.getParticleInfluencer().setVelocityVariation(0f);
        shockwave.setImagesX(1);
        shockwave.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture(TEXTURE_PARAM, assetManager.loadTexture("Effects/Explosion/shockwave.png"));
        shockwave.setMaterial(mat);
        return shockwave;
    }

    /**
     * Creates a particle emitter for blacksmoke according to a given intensity
     * 
     * @param intensity
     *        the intendisty of the particles
     * @return a particle emitter
     */
    public static ParticleEmitter createBlackSmoke(float intensity)
    {

        ParticleEmitter smoke = new ParticleEmitter("Smoke", Type.Triangle, 500);
        smoke.setStartColor(ColorRGBA.DarkGray);
        smoke.setEndColor(ColorRGBA.Black);
        smoke.setStartSize(2f);
        smoke.setEndSize(2f);
        smoke.setGravity(new Vector3f(0f, 0f, 0f));
        smoke.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(0f, 100f, 0f));
        smoke.getParticleInfluencer().setVelocityVariation(.3f);
        smoke.setImagesX(15);
        smoke.setImagesY(1);
        smoke.setLowLife(5f);
        smoke.setHighLife(10f);
        smoke.setParticlesPerSec(100f);
        smoke.setEnabled(false);

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
