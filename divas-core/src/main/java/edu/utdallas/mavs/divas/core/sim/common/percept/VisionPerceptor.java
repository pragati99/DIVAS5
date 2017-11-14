package edu.utdallas.mavs.divas.core.sim.common.percept;

import java.awt.Shape;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;

public interface VisionPerceptor
{
    public Shape calculateVisibleRegion();

    public void setVisibleDistance(float visibleDistance);

    public void setVisionAlgorithm(VisionAlgorithm visionAlgorithmName);

    public void setFOV(float fov);

    public float getVisionHeight();

    public float getVisibleDistance();

    public float getFOV();

}
