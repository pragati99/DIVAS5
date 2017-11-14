package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.queue.RenderQueue.Bucket;

import edu.utdallas.mavs.divas.core.sim.common.event.DynamiteEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class represents an explosion visualized event
 */
public class Dynamite extends ParticleEmmitterEffect
{
    private DynamiteEvent event;
    private float         speed = 1f;
    private float         time  = 0;
    private int           state = 1;
    private ParticleEmitter flame, flash, spark, roundspark, smoketrail, debris, shockwave; // , blackSmoke;

    /**
     * Creates a new explosion visualized event
     * 
     * @param event
     *        the event associated with the explosion visualized event
     */
    public Dynamite(DynamiteEvent event)
    {
        super();
        this.event = event;
        flame = ExplosionFactory.createFlameD(event.getVisualizedIntensity() * 5);
        debris = ExplosionFactory.createDebrisD(.1f);
        flash = ExplosionFactory.createFlashD(event.getVisualizedIntensity() * 5);
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
            time = 0f;
            state++;
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
