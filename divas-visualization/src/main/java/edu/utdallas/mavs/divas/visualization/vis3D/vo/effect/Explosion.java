package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.queue.RenderQueue.Bucket;

import edu.utdallas.mavs.divas.core.sim.common.event.BombEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class represents an explosion visualized event
 */
public class Explosion extends ParticleEmmitterEffect
{
    private BombEvent event;
    private float     speed = 1f;
    private float     time  = 0;
    private int       state = 1;
    private ParticleEmitter flame, flash, spark, roundspark, smoketrail, debris, shockwave, smoke; // , blackSmoke;

    /**
     * Creates a new explosion visualized event
     * 
     * @param event
     *        the event associated with the explosion visualized event
     */
    public Explosion(BombEvent event)
    {
        super();
        this.event = event;
        flame = ExplosionFactory.createFlame(event.getVisualizedIntensity());
        debris = ExplosionFactory.createDebris(event.getVisualizedIntensity());
        flash = ExplosionFactory.createFlash(event.getVisualizedIntensity());
        roundspark = ExplosionFactory.createRoundSpark(event.getVisualizedIntensity());
        shockwave = ExplosionFactory.createShockwave(event.getVisualizedIntensity());
        smoketrail = ExplosionFactory.createSmokeTrail(event.getVisualizedIntensity());
        spark = ExplosionFactory.createSpark(event.getVisualizedIntensity());
        attachChild(flash);
        attachChild(spark);
        attachChild(smoketrail);
        attachChild(shockwave);
        attachChild(debris);
        attachChild(flame);
        attachChild(roundspark);
        shockwave.setQueueBucket(Bucket.Translucent);
        debris.setQueueBucket(Bucket.Translucent);
        smoketrail.setQueueBucket(Bucket.Translucent);
        flash.setQueueBucket(Bucket.Translucent);
        spark.setQueueBucket(Bucket.Translucent);
        flame.setQueueBucket(Bucket.Translucent);
        roundspark.setQueueBucket(Bucket.Translucent);
        // blackSmoke.setQueueBucket(Bucket.Translucent);

        if(event.isSmoke())
        {
            smoke = SmokeFactory.createSmoke(event.getVisualizedIntensity());
            smoke.setQueueBucket(Bucket.Translucent);
            attachChild(smoke);
        }

    }

    @Override
    public void update(BaseVO parent, float tpf)
    {

        if(time > 0f && state == 1)
        {
            flash.emitAllParticles();
            spark.emitAllParticles();
            smoketrail.emitAllParticles();
            debris.emitAllParticles();
            shockwave.emitAllParticles();
            // blackSmoke.setEnabled(true);
            time = 0f;
            state++;
        }
        if(time > .05f / speed && state == 2)
        {
            flame.emitAllParticles();
            roundspark.emitAllParticles();
            time = 0f;
            state++;
        }

        if(time > .5f / speed && state == 3)
        {
            if(event.isSmoke())
            {
                smoke.emitAllParticles();
            }
            time = 0f;
            state++;
        }

        if((event.isSmoke() && time > 30f * event.getIntensity() / speed || !event.isSmoke() && time > 10f * event.getIntensity() / speed) && state == 4)
        {
            flash.killAllParticles();
            spark.killAllParticles();
            smoketrail.killAllParticles();
            debris.killAllParticles();
            flame.killAllParticles();
            roundspark.killAllParticles();
            shockwave.killAllParticles();
            // blackSmoke.killAllParticles();
            if(event.isSmoke())
            {
                smoke.killAllParticles();
            }
            time = 0f;
            state = 0;
            parent.enqueueDetachSpatial();
        }

        time += tpf / speed;
        if(event.isSmoke())
        {
            time %= 31f * event.getIntensity();
        }
        else
        {
            time %= 11f * event.getIntensity();
        }
    }
}
