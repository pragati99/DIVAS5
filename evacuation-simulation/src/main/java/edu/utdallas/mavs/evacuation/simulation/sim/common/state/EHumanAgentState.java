package edu.utdallas.mavs.evacuation.simulation.sim.common.state;

import java.util.List;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanProperties.AgeCategory;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanProperties.BodyBuild;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanProperties.Gender;

public class EHumanAgentState extends HumanAgentState
{
    private static final long serialVersionUID = 1L;

    /**
     * The current posture of the agent.
     */
    protected Posture         posture          = Posture.Idle1;

    /**
     * The reachable distance between the agent and another entity in the simulation.
     */
    protected float           reachDistance;

    /**
     * The body build of the agent.
     */
    protected BodyBuild       bodyBuild;

    /**
     * The gender of the agent.
     */
    protected Gender          gender;

    /**
     * The age category the agent belongs to (i.e., child, young, adult)
     */
    protected AgeCategory     ageCategory;

    /**
     * An integer representing the clothing (skin) of the agent.
     */
    protected int             clothing;

    /**
     * Debug information related to planning. To be removed.
     */
    private String            planningDetails;

    /**
     * Agent Plan for display in visualizer
     */
    private List<Vector3f>    agentPath;

    /**
     * Gets the posture of the agent.
     * 
     * @return The agent posture.
     */
    public Posture getPosture()
    {
        return posture;
    }

    /**
     * Changes the posture of the agent.
     * 
     * @param posture
     *        The agent posture.
     */
    public void setPosture(Posture posture)
    {
        this.posture = posture;
    }

    /**
     * The reachable distance between the agent and another entity in the simulation
     * 
     * @return the reachable distance to this agent.
     */
    public float getReachDistance()
    {
        return reachDistance;
    }

    /**
     * Changes the distance to reach this agent.
     * 
     * @param reachDistance
     *        The new reach distance for the agent.
     */
    public void setReachDistance(float reachDistance)
    {
        this.reachDistance = reachDistance;
    }

    /**
     * Gets the body build of the agent.
     * 
     * @return The agent body build.
     */
    public BodyBuild getBodyBuild()
    {
        return bodyBuild;
    }

    /**
     * Changes the body build of the agent.
     * 
     * @param bodyBuild
     *        The new body build for the agent.
     */
    public void setBodyBuild(BodyBuild bodyBuild)
    {
        this.bodyBuild = bodyBuild;
    }

    /**
     * Gets the gender of the agent.
     * 
     * @return the agent gender.
     */
    public Gender getGender()
    {
        return gender;
    }

    /**
     * Sets the agent gender when the agent is first created.
     * 
     * @param gender
     *        the gender of the agent.
     */
    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    /**
     * Gets the age category the agent belongs to.
     * 
     * @return the agent age category.
     */
    public AgeCategory getAgeCategory()
    {
        return ageCategory;
    }

    /**
     * Changes the age category the agent belongs to.
     * 
     * @param ageCategory
     *        The age category of the agent.
     */
    public void setAgeCategory(AgeCategory ageCategory)
    {
        this.ageCategory = ageCategory;
    }

    /**
     * Gets the clothing of the agent.
     * 
     * @return an integer representing the agent clothing.
     */
    public int getClothing()
    {
        return clothing;
    }

    /**
     * Changes the agent clothing.
     * 
     * @param clothing
     *        The new agent clothing.
     */
    public void setClothing(int clothing)
    {
        this.clothing = clothing;
    }

    /**
     * Sets the debug info for planning. To be removed.
     * 
     * @param planningDetails
     */
    public void setPlanningDetails(String planningDetails)
    {
        this.planningDetails = planningDetails;
    }

    /**
     * This method is to be used to debug purposes only. To be removed.
     * 
     * @return planning debug information
     */
    public String getPlanningDetails()
    {
        return planningDetails;
    }

    // FIXME hardcoded!
    public float getRadius()
    {
        return SimConfig.getInstance().agent_Radius;
    }

    public List<Vector3f> getAgentPath()
    {
        return agentPath;
    }

    public void setAgentPath(List<Vector3f> agentPath)
    {
        this.agentPath = agentPath;
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
    public void copyFrom(EHumanAgentState agent)
    {
        super.copyFrom(agent);      
        reachDistance = agent.reachDistance;
        posture = agent.posture;       
        clothing = agent.clothing;
        bodyBuild = agent.bodyBuild;
        ageCategory = agent.ageCategory;
        gender = agent.gender;
    }
}
