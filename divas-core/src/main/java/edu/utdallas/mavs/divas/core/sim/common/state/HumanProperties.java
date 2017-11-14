package edu.utdallas.mavs.divas.core.sim.common.state;

/**
 * This class contains enumerations for human agents.
 */
public class HumanProperties
{
    /**
     * Enumeration for body build of a human agent.
     */
    public enum BodyBuild
    {
        /**
         * A tall agent.
         */
        TALL,

        /**
         * A fat agent.
         */
        FAT,

        /**
         * A tough (athletic) agent.
         */
        TOUGH
    }

    /**
     * Enumeration for gender of a human agent.
     */
    public enum Gender
    {
        /**
         * A female agent.
         */
        FEMALE,

        /**
         * A male agent.
         */
        MALE
    }

    /**
     * Enumeration for the age category the agent belongs to
     */
    public enum AgeCategory
    {
        /**
         * An adult agent.
         */
        ADULT, 
        
        /**
         * A child agent.
         */
        CHILD, 
        
        /**
         * A youth agent.
         */
        YOUNG, 
        /**
         * An elderly agent.
         */
        ELDER
    }
}
