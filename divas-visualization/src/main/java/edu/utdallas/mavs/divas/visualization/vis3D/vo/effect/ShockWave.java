package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.queue.RenderQueue.Bucket;

import edu.utdallas.mavs.divas.core.sim.common.event.SirenEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class represents an explosion visualized event
 */
public class ShockWave extends ParticleEmmitterEffect
{
    private float           speed = 1f;
    private float           time  = 0;
    private int             state = 1;
    private ParticleEmitter shockwave; //

    /**
     * Creates a new explosion visualized event
     * 
     * @param event
     *        the event associated with the explosion visualized event
     */
    public ShockWave(SirenEvent event)
    {
        super();
        shockwave = ExplosionFactory.createShockwave(event.getVisualizedIntensity());
        attachChild(shockwave);

        shockwave.setQueueBucket(Bucket.Translucent);
        // blackSmoke.setQueueBucket(Bucket.Translucent);
        // System.out.println("Creating SHOCKWAVEEEEF");

    }

    @Override
    public void update(BaseVO parent, float tpf)
    {
        // System.out.println(time);
        if(time > 0f && state == 1)
        {
            shockwave.emitAllParticles();
            // blackSmoke.setEnabled(true);
            time = 0f;
            state++;
        }

        if(time > 2f)
        {
            shockwave.killAllParticles();
            // blackSmoke.killAllParticles();
            time = 0f;
            state = 0;
            parent.enqueueDetachSpatial();
        }

        time += tpf / speed;

    }
}
