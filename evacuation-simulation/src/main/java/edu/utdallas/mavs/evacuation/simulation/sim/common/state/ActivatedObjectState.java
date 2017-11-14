package edu.utdallas.mavs.evacuation.simulation.sim.common.state;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class represents the current state of a object.
 */
public class ActivatedObjectState extends EnvObjectState
{
    private static final long serialVersionUID = 0;
    private boolean           isOn             = true;

    ActiveObjectType          activeObjectType = ActiveObjectType.SIREN;

    public enum ActiveObjectType
    {
        SIREN
    }

    /**
     * Whether is on or not.
     * 
     * @return True if on, otherwise false.
     */
    public boolean isOn()
    {
        return isOn;
    }

    /**
     * Set whether on or not
     * 
     * @param isOn
     *        True if on, otherwise false.
     */
    public void setOn(boolean isOn)
    {
        this.isOn = isOn;
    }

    public ActiveObjectType getActiveObjectType()
    {
        return activeObjectType;
    }

    public void setActiveObjectType(ActiveObjectType activeObjectType)
    {
        this.activeObjectType = activeObjectType;
    }

    /**
     * Changes the description, type, material, rotation, available, onFire and scale of the environment object with such properties from the
     * given env object state.
     * 
     * @param eo
     *        The environment object state to copy properties from (i.e., description, type, material) to this environment object state.
     */
    @Override
    public void copyFrom(EnvObjectState eo)
    {
        super.copyFrom(eo);
        if(eo instanceof ActivatedObjectState)
        {
            isOn = ((ActivatedObjectState) eo).isOn();
        }
    }

    @Override
    public EnvObjectState copy()
    {
        ActivatedObjectState copy = new ActivatedObjectState();
        copy.setPosition(position.clone());
        copy.setModelName(modelName);
        copy.setMaterial(material);
        copy.setScale(scale.clone());
        copy.setRotation(rotation.clone());
        copy.setType(type);
        copy.setOn(isOn);
        return copy;
    }
}
