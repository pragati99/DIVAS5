package edu.utdallas.mavs.divas.core.sim.common.state;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;
import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.AudioPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.SmellPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.utils.physics.BoundingShape;
import edu.utdallas.mavs.divas.utils.physics.VisionHelper;

public class HumanAgentState extends AgentState implements Visible, Audible, VisionPerceptor, AudioPerceptor, SmellPerceptor
{
    private static final long  serialVersionUID = 1L;

    private static final float EYES_OFFSET      = 0.15f;

    /**
     * The maximum speed of the agent.
     */
    protected float            maxSpeed;

    /**
     * The desired speed of the agent.
     */
    protected float            desiredSpeed;

    /**
     * The visible distance the agent can perceive using its vision sense.
     */
    protected float            visibleDistance;

    /**
     * The field of vision of the agent.
     */
    protected float            fov;

    /**
     * The minimum audible threshold the agent can perceive using its hearing sense.
     */
    protected float            minAudibleThreshold;

    /**
     * The acoustic emission of sound emitted by the agent.
     */
    protected float            acousticEmission;

    /**
     * The smell sensitivity the agent has when using its smell sense.
     */
    protected float            smellSensitivity;

    /**
     * The vision algorithm the agent is using to perceive with its vision sense.
     */
    protected VisionAlgorithm  visionAlgorithm;

    /**
     * The vision shape of the agent.
     */
    protected VisionShape      visionShape      = VisionShape.CONICAL;

    /**
     * Flag indicating if the agent is alive.
     */
    protected boolean          alive            = true;

    /**
     * Flag indicating if the agent olfactory sense is enabled.
     */
    protected boolean          olfactoryEnabled = true;

    /**
     * Flag indicating if the agent auditory sense is enabled.
     */
    protected boolean          auditoryEnabled  = true;

    /**
     * Enumeration for vision shape of the agent.
     */
    public enum VisionShape
    {
        /**
         * Conical vision shape. This vision has the shape of a cone.
         */
        CONICAL
    }

    public HumanAgentState()
    {
        super();
        boundingShape = BoundingShape.CAPSULE;
    }

    /**
     * Gets the minimum audible threshold the agent can perceive using its hearing sense.
     * 
     * @return the minimum audible threshold of the agent.
     */
    @Override
    public float getMinAudibleThreshold()
    {
        return minAudibleThreshold;
    }

    /**
     * Gets the smell sensitivity the agent has when using its smell sense.
     * 
     * @return the smell sensitivity of the agent.
     */
    public float getSmellSensitivity()
    {
        return smellSensitivity;
    }

    /**
     * Gets the desired speed of the agent.
     * 
     * @return the desired speed of the agent.
     */
    public float getDesiredSpeed()
    {
        return desiredSpeed;
    }

    /**
     * Gets the maximum speed of the agent.
     * 
     * @return the desired speed of the agent.
     */
    public float getMaxSpeed()
    {
        return maxSpeed;
    }

    /**
     * Gets the vision algorithm of the agent. The algorithm the agent uses to perceive with its vision sense.
     * 
     * @return the vision algorithm.
     */
    public VisionAlgorithm getVisionAlgorithm()
    {
        return visionAlgorithm;
    }

    /**
     * Gets the visible distance of the agent. Everything within this distance can be perceived by the agent using its
     * vision sense.
     * 
     * @return The visible distance.
     */
    @Override
    public float getVisibleDistance()
    {
        return visibleDistance;
    }

    @Override
    public float getAcousticEmission()
    {
        return acousticEmission;
    }

    @Override
    public Vector3f getAudiblePosition()
    {
        return position;
    }

    /**
     * Changes the acoustic emission of sound emitted by the agent.
     * 
     * @param acousticEmission
     *        the new acoustic emission for the agent.
     */
    public void setAcousticEmission(float acousticEmission)
    {
        this.acousticEmission = acousticEmission;
    }

    @Override
    public Vector3f getVisiblePosition()
    {
        return position;
    }

    @Override
    public Vector3f getVisibleScale()
    {
        return scale;
    }

    @Override
    public float getCurrentSoundRadius()
    {
        // agents make no sound currently
        return 0;
    }

    /**
     * Changes the minimum audible threshold of the agent.
     * 
     * @param minAudibleThreshold
     *        The new minimum audible threshold.
     */
    public void setMinAudibleThreshold(float minAudibleThreshold)
    {
        this.minAudibleThreshold = minAudibleThreshold;
    }

    /**
     * Changes the smell sensitivity of the agent.
     * 
     * @param smellSensitivity
     *        The new smell sensitivity for the agent.
     */
    public void setSmellSensitivity(float smellSensitivity)
    {
        this.smellSensitivity = smellSensitivity;
    }

    /**
     * Changes the desired speed of the agent.
     * 
     * @param velocity
     *        The new desired speed for the agent.
     */
    public void setDesiredSpeed(float velocity)
    {
        this.desiredSpeed = velocity;
    }

    /**
     * Changes the maximum speed of the agent.
     * 
     * @param maxVelocity
     *        The new maximum speed for the agent.
     */
    public void setMaxSpeed(float maxVelocity)
    {
        this.maxSpeed = maxVelocity;
    }

    /**
     * Changes the vision algorithm of the agent.
     * 
     * @param visionAlgorithmName
     *        The new vision algorithm for the agent.
     */
    @Override
    public void setVisionAlgorithm(VisionAlgorithm visionAlgorithmName)
    {
        this.visionAlgorithm = visionAlgorithmName;
    }

    /**
     * Changes the visible distance of the agent.
     * 
     * @param visibleDistance
     *        The new visible distance for the agent.
     */
    @Override
    public void setVisibleDistance(float visibleDistance)
    {
        this.visibleDistance = visibleDistance;
    }

    /**
     * Changes the position of the agent for which other entities can perceive it.
     * 
     * @param visiblePosition
     *        The new visible position for the agent.
     */
    public void setVisiblePosition(Vector3f visiblePosition)
    {
        this.position = visiblePosition;
    }

    /**
     * Gets the field of view of the agent.
     * 
     * @return the field of view for the agent.
     */
    @Override
    public float getFOV()
    {
        return fov;
    }

    /**
     * Changes the field of view of the agent
     * 
     * @param fov
     *        The new field of view for the agent.
     */
    @Override
    public void setFOV(float fov)
    {
        this.fov = fov;
    }

    /**
     * Flag indicating if the agent olfactory sense is enabled.
     * 
     * @return true if the agent olfactory sense is enabled and false otherwise.
     */
    @Override
    public boolean isOlfactoryEnabled()
    {
        return olfactoryEnabled;
    }

    /**
     * Changes the flag indicating whether the agent olfactory sense is enabled or not enabled.
     * 
     * @param olfactoryEnabled
     *        Flag indicating if the agent olfactory sense is enabled.
     */
    @Override
    public void setOlfactoryEnabled(boolean olfactoryEnabled)
    {
        this.olfactoryEnabled = olfactoryEnabled;
    }

    /**
     * Flag indicating if the agent auditory sense is enabled.
     * 
     * @return true if the agent auditory sense is enabled and false otherwise.
     */
    @Override
    public boolean isAuditoryEnabled()
    {
        return auditoryEnabled;
    }

    /**
     * Changes the flag indicating whether the agent auditory sense is enabled or not enabled.
     * 
     * @param auditoryEnabled
     *        Flag indicating if the agent auditory sense is enabled.
     */
    @Override
    public void setAuditoryEnabled(boolean auditoryEnabled)
    {
        this.auditoryEnabled = auditoryEnabled;
    }

    // TODO: optimization, the shape or volume will not change unless the fov or
    // visible distance changes, so we can store it until one of those changes
    /**
     * Calculates the visible region of the agent represented by a spherical cone.
     * 
     * @return the visible region for the agent.
     */
    @Override
    public Shape calculateVisibleRegion()
    {
        float visibleDistance = getVisibleDistance();
        Arc2D arc = new Arc2D.Float((getPosition().getX() - visibleDistance), (getPosition().getZ() - visibleDistance), (visibleDistance * 2), (visibleDistance * 2), 0, 0, Arc2D.PIE);

        Vector3f towards = getPosition().add(getHeading());

        // cheat to find the angle to new goal, this puts the heading at the edge of the arc
        arc.setAngleStart(new Point2D.Float(towards.getX(), towards.getZ()));
        // subtract by half the fov, this puts the heading at the center of the arc
        arc.setAngleStart(arc.getAngleStart() - getFOV() / 2);
        arc.setAngleExtent(getFOV());

        return arc;
    }

    /**
     * Returns the an array of vectors representing the agent's visible region
     * 
     * @return visible region
     */
    public List<Ray> getVisibleRegion()
    {
        List<Ray> visibleRegion = new ArrayList<Ray>();
        if(visionShape.equals(VisionShape.CONICAL))
        {
            visibleRegion = VisionHelper.createVisionCone(position.add(0, getVisionHeight(), 0), heading, fov, visibleDistance, SimConfig.getInstance().vision_Circles_Count,
                    SimConfig.getInstance().vision_Rays_Count);
        }
        return visibleRegion;
    }

    /**
     * Gets the eyes level height of the agent.
     * 
     * @return the agent eyes level height.
     */
    @Override
    public float getVisionHeight()
    {
        return scale.y - EYES_OFFSET;
    }

    /**
     * Gets the eyes position of the agent
     * 
     * @return the agent eyes position
     */
    public Vector3f getEyePosition()
    {
        return position.add(0, getVisionHeight(), 0);
    }

    /**
     * The size of the agent
     * 
     * @return the agent's size
     */
    public float getAgentSize()
    {
        // TODO AGENTS ARE CURRENTLY BOXES
        return 1.45f;
    }

    /**
     * Gets the height of the agent.
     * 
     * @return The agent height.
     */
    public float getHeight()
    {
        return scale.y;
    }

    /**
     * Changes the agent type, control type, heading, max speed, desired speed, visible distance, field of view, min
     * audible threshold, acoustic emission,
     * smell sensitivity, reach distance, posture, vision algorithm, olfactory enabled and auditory enabled of the agent
     * with such properties from the
     * given agent state.
     * 
     * @param agent
     *        The agent state to copy properties from (i.e., agent type, control type, heading) to this agent state.
     */
    public void copyFrom(HumanAgentState agent)
    {
        super.copyFrom(agent);
        maxSpeed = agent.maxSpeed;
        desiredSpeed = agent.desiredSpeed;
        visibleDistance = agent.visibleDistance;
        fov = agent.fov;
        minAudibleThreshold = agent.minAudibleThreshold;
        acousticEmission = agent.acousticEmission;
        smellSensitivity = agent.smellSensitivity;
        visionAlgorithm = agent.visionAlgorithm;
        olfactoryEnabled = agent.olfactoryEnabled;
        auditoryEnabled = agent.auditoryEnabled;
    }
}
