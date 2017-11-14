package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import java.util.ArrayList;
import java.util.List;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class represents a fireworks visualized event
 */
public class Fireworks extends ParticleEmmitterEffect
{
    private final float           DIST        = 6f;
    private float                 speed       = 1f;
    private float                 time        = 0;
    private int                   state       = 1;
    private List<ParticleEmitter> flashes     = new ArrayList<ParticleEmitter>();
    private List<ParticleEmitter> sparks      = new ArrayList<ParticleEmitter>();
    private List<ParticleEmitter> roundsparks = new ArrayList<ParticleEmitter>();

    /**
     * Creates a new fireworks visualized event
     * 
     * @param event the event associated with the fireworks visualized event
     */
    public Fireworks(EnvEvent event)
    {
        super();
        int intensity = Math.round(event.getIntensity());
        float posX = 0;
        float posZ = 0;
        ParticleEmitter flash, spark, roundspark;
        for(int i = 0; i < intensity; i++)
        {
            for(int j = 0; j < intensity; j++)
            {

                posX = i * DIST;
                posZ = j * DIST;

                flash = FireworksFactory.createFlash();
                flash.setLocalTranslation(posX, 0f, posZ);
                flashes.add(flash);

                roundspark = FireworksFactory.createRoundSpark();
                roundspark.setLocalTranslation(posX, 0f, posZ);
                roundsparks.add(roundspark);

                spark = FireworksFactory.createSpark();
                spark.setLocalTranslation(posX, 0f, posZ);
                sparks.add(spark);

                flash.setQueueBucket(Bucket.Translucent);
                spark.setQueueBucket(Bucket.Translucent);
                roundspark.setQueueBucket(Bucket.Translucent);

                attachChild(flash);
                attachChild(spark);
                attachChild(roundspark);
            }
        }
    }

    @Override
    public void update(BaseVO parent, float tpf)
    {
        if(time > 0f && state == 1)
        {
            for(ParticleEmitter flash : flashes)
            {
                flash.emitAllParticles();
            }
            for(ParticleEmitter spark : sparks)
            {
                spark.setEndColor(ColorRGBA.randomColor());
                spark.emitAllParticles();
            }
            time = 0f;
            state++;
        }

        if(time > 0f + .05f / speed && state == 2)
        {
            for(ParticleEmitter roundspark : roundsparks)
            {
                ColorRGBA color = ColorRGBA.randomColor();
                roundspark.setStartColor(color);
                roundspark.setEndColor(color);
                roundspark.emitAllParticles();
            }
            state++;
        }

        if(time > 0f + 2f / speed && state == 3)
        {
            for(ParticleEmitter flash : flashes)
            {
                flash.killAllParticles();
            }
            for(ParticleEmitter spark : sparks)
            {
                spark.killAllParticles();
            }
            for(ParticleEmitter roundspark : roundsparks)
            {
                roundspark.killAllParticles();
            }
            time = 0f;
            state = 0;
            parent.enqueueDetachSpatial();
        }
        time += tpf / speed;
        time %= 3f;
    }
}
