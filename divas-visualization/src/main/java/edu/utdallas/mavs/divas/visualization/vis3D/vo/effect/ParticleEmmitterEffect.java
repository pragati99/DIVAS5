package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import com.jme3.scene.Node;

import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;

/**
 * This class describes a particle emmitter effect.
 */
public abstract class ParticleEmmitterEffect extends Node
{

    /**
     * Updates the visualization of the particle emmitter effect as the internal state changes.
     * 
     * @param parent the parent VO of this visualized event
     * @param tpf time per Frame
     */
    public abstract void update(BaseVO parent, float tpf);

}
