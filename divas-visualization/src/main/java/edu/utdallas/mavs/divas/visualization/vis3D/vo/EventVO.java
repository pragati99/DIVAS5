package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.scene.Spatial;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.ParticleEmmitterEffect;

/**
 * This class represents a visualized event
 */
public abstract class EventVO extends BaseVO
{
    private static final Logger logger = LoggerFactory.getLogger(EventVO.class);

    protected EnvEvent          event;
    protected Spatial           eventVO;

    /**
     * Cosntructs a new event VO
     * 
     * @param event the event state associated with this VO
     * @param cycle the simulation cycle number associated with this event
     */
    public EventVO(EnvEvent event, long cycle)
    {
        super();
        this.event = event;
        enqueueAttachSpatial();
    }

    protected abstract Spatial createEventVO(EnvEvent event);

    /**
     * Gets the event associated with this VO
     * 
     * @return the event associated with this VO
     */
    public EnvEvent getEvent()
    {
        return event;
    }

    /**
     * Updates this VO
     * 
     * @param tpf
     *        Time per Frame
     */
    public void update(float tpf)
    {
        if(eventVO instanceof ParticleEmmitterEffect)
        {
            ((ParticleEmmitterEffect) eventVO).update(this, tpf);
        }
    }

    @Override
    protected void attachSpatial()
    {
        super.attachSpatial();
        eventVO = createEventVO(event);

        logger.debug("Attaching event VO id={} type={}", event.getID(), event.getClass().getName());
        attachChild(eventVO);

        if(eventVO instanceof ParticleEmmitterEffect)
            this.setLocalTranslation(event.getOrigin());
    }

    @Override
    protected void updateState()
    {
        // do not update states for this VO
    }
}
