package edu.utdallas.mavs.evacuation.visualization.vis3D.common;

import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;

/**
 * The specifying type of the mouse cursor.
 */
public class EvacuationCursorType
{
    /**
     * Bomb cursor, depicted by a black bomb
     */
    public static final CursorType BOMB       = CursorType.create("BOMB");
    /**
     * Smoke bomb cursor, depicted by a gray bomb
     */
    public static final CursorType SMOKE_BOMB = CursorType.create("SMOKE_BOMB");
    /**
     * Treasure cursor, depicted by a treasure coffin
     */
    public static final CursorType TREASURE   = CursorType.create("TREASURE");
    /**
     * Fireworks cursor
     */
    public static final CursorType FIREWORKS  = CursorType.create("FIREWORKS");
    /**
     * Drums cursor
     */
    public static final CursorType DRUMS      = CursorType.create("DRUMS");
    /**
     * Grill cursor
     */
    public static final CursorType GRILL      = CursorType.create("GRILL");
    /**
     * Spotlight cursor
     */
    public static final CursorType SPOTLIGHT  = CursorType.create("SPOTLIGHT");
}
