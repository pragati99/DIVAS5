package edu.utdallas.mavs.divas.core.sim.agent.knowledge;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;

/**
 * Human Knowledge Module Adds data specific only to humans. (Auditory, vision and olfactory information)
 * 
 * @param <S>
 *        the type of human agent state
 */
public abstract class HumanKnowledgeModule<S extends HumanAgentState> extends AbstractKnowledgeModule<S> implements Serializable
{
    private static final long serialVersionUID       = 1L;

    private float             auditoryTrustConstant  = .7f;
    private float             visionTrustConstant    = .95f;
    private float             olfactoryTrustConstant = .3f;

    /**
     * Base Constructor.
     */
    public HumanKnowledgeModule(S self)
    {
        super(self);
    }

    // /**
    // * Constructor for trust constants.
    // *
    // * @param auditoryTrustConstant
    // * Hearing Trust
    // * @param visionTrustConstant
    // * Vision Trust
    // * @param olfactoryTrustConstant
    // * Smell Trust
    // */
    // public HumanKnowledgeModule(float auditoryTrustConstant, float visionTrustConstant, float olfactoryTrustConstant)
    // {
    // super();
    // this.auditoryTrustConstant = auditoryTrustConstant;
    // this.visionTrustConstant = visionTrustConstant;
    // this.olfactoryTrustConstant = olfactoryTrustConstant;
    // }

    /**
     * Get the hearing trust constant
     * 
     * @return The Trust Constant
     */
    public float getAuditoryTrustConstant()
    {
        return auditoryTrustConstant;
    }

    /**
     * Set the hearing trust constant
     * 
     * @param auditoryTrustConstant
     *        The Trust Constant
     */
    public void setAuditoryTrustConstant(float auditoryTrustConstant)
    {
        this.auditoryTrustConstant = auditoryTrustConstant;
    }

    /**
     * Get the vision trust constant
     * 
     * @return The Trust Constant
     */
    public float getVisionTrustConstant()
    {
        return visionTrustConstant;
    }

    /**
     * Set the vision trust constant
     * 
     * @param visionTrustConstant
     *        The Trust Constant
     */
    public void setVisionTrustConstant(float visionTrustConstant)
    {
        this.visionTrustConstant = visionTrustConstant;
    }

    /**
     * Get the smell trust constant
     * 
     * @return The Trust Constant
     */
    public float getOlfactoryTrustConstant()
    {
        return olfactoryTrustConstant;
    }

    /**
     * Set the smell trust constant
     * 
     * @param olfactoryTrustConstant
     *        The Trust Constant
     */
    public void setOlfactoryTrustConstant(float olfactoryTrustConstant)
    {
        this.olfactoryTrustConstant = olfactoryTrustConstant;
    }
}
