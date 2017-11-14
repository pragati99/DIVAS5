package edu.utdallas.mavs.evacuation.visualization.vis3D.common;

import edu.utdallas.mavs.divas.visualization.vis3D.common.InputMode;

public class EvacuationInputMode
{
    public static final InputMode ADD_EXPLOSION          = InputMode.create("Add Smoke Explosion");

    /**
     * Adds fireworks to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_FIREWORKS          = InputMode.create("Add Fireworks");

    /**
     * Adds an explosion without smoke to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_EXPLOSION_NO_SMOKE = InputMode.create("Add Explosion");

    /**
     * Adds a treasure to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_TREASURE           = InputMode.create("Add Treasure");

    /**
     * Adds a grill to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_GRILL              = InputMode.create("Add Grill");

    /**
     * Adds drums to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_DRUMS              = InputMode.create("Add Drums");

    /**
     * Adds a spotlight to the location pointed by the user upon a mouse left-button click.
     */
    public static final InputMode ADD_SPOTLIGHT          = InputMode.create("Add Spotlight");
}
