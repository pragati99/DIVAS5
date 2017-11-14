package edu.utdallas.mavs.divas.core.client.dto;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;

/**
 * DTO to be used to set global agent properties
 */
public class GeneralAgentProperties
{
    VisionAlgorithm visionAlgorithm;
    float           visibleDistance;
    float           fov;
    boolean         isVisionConeEnabled;
    boolean         isAuditoryEnabled;
    boolean         isOlfactoryEnabled;

    public GeneralAgentProperties(VisionAlgorithm visionAlgorithm, float visibleDistance, float fov, boolean isVisionConeEnabled, boolean isAuditoryEnabled, boolean isOlfactoryEnabled)
    {
        this.visionAlgorithm = visionAlgorithm;
        this.visibleDistance = visibleDistance;
        this.fov = fov;
        this.isVisionConeEnabled = isVisionConeEnabled;
        this.isAuditoryEnabled = isAuditoryEnabled;
        this.isOlfactoryEnabled = isOlfactoryEnabled;
    }

    public VisionAlgorithm getVisionAlgorithm()
    {
        return visionAlgorithm;
    }

    public float getVisibleDistance()
    {
        return visibleDistance;
    }

    public float getFov()
    {
        return fov;
    }

    public boolean isVisionConeEnabled()
    {
        return isVisionConeEnabled;
    }

    public boolean isAuditoryEnabled()
    {
        return isAuditoryEnabled;
    }

    public boolean isOlfactoryEnabled()
    {
        return isOlfactoryEnabled;
    }
}
