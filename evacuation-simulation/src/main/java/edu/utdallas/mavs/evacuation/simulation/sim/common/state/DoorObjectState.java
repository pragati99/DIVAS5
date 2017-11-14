package edu.utdallas.mavs.evacuation.simulation.sim.common.state;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class represents the current state of a door
 */
public class DoorObjectState extends EnvObjectState
{
    private static final long serialVersionUID = -2950808581541129266L;
    private boolean           isOpen           = true;

    /**
     * Whether the door is open or not.
     * 
     * @return True if open, otherwise false.
     */
    public boolean isOpen()
    {
        return isOpen;
    }

    /**
     * Set whether a door is open or not
     * 
     * @param isOpen
     *        True if open, otherwise false.
     */
    public void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;
    }

    @Override
    public EnvObjectState copy()
    {
        DoorObjectState copy = new DoorObjectState();
        copy.setPosition(position.clone());
        copy.setModelName(modelName);
        copy.setMaterial(material);
        copy.setScale(scale.clone());
        copy.setRotation(rotation.clone());
        copy.setType(type);
        copy.setOpen(isOpen);
        return copy;
    }

}
