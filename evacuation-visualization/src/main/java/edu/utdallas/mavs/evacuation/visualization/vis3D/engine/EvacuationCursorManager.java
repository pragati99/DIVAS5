package edu.utdallas.mavs.evacuation.visualization.vis3D.engine;

import com.jme3.asset.AssetManager;

import edu.utdallas.mavs.divas.visualization.vis3D.common.CursorType;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.CursorManager;
import edu.utdallas.mavs.evacuation.visualization.vis3D.common.EvacuationCursorType;

/**
 * This class describes the hardware cursor manager of the 3D visualizer.
 */
public class EvacuationCursorManager extends CursorManager
{

    public EvacuationCursorManager(AssetManager assetmanager)
    {
        super(assetmanager);
    }

    @Override
    public synchronized void setCursorType(CursorType type)
    {
        if(type.equals(CursorType.HAND))
        {
            setHardwareCursor("/cursors/hand_cursor_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.BOMB))
        {
            setHardwareCursor("/cursors/bomb_nosmoke_cursor_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.SMOKE_BOMB))
        {
            setHardwareCursor("/cursors/bomb_cursor_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.FIREWORKS))
        {
            setHardwareCursor("/cursors/fireworks3_8.png", -1, -4);
        }
        else if(type.equals(CursorType.AGENT))
        {
            setHardwareCursor("/cursors/arrows/blackman_8.png", -1, -4);
        }
        else if(type.equals(CursorType.OBJECT))
        {
            setHardwareCursor("/cursors/arrows/object_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.TREASURE))
        {
            setHardwareCursor("/cursors/treasure_cursor_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.GRILL))
        {
            setHardwareCursor("/cursors/grill2_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.DRUMS))
        {
            setHardwareCursor("/cursors/drums2_8.png", -1, -4);
        }
        else if(type.equals(EvacuationCursorType.SPOTLIGHT))
        {
            setHardwareCursor("/cursors/spotlight2_8.png", -1, -4);
        }
        else if(type.equals(CursorType.COPY))
        {
            setHardwareCursor("/cursors/arrows/arrowPlus_8.png", -1, -4);
        }
        else if(type.equals(CursorType.MOVE))
        {
            setHardwareCursor("/cursors/arrows/move_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_X))
        {
            setHardwareCursor("/cursors/arrows/scaleX_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_Y))
        {
            setHardwareCursor("/cursors/arrows/scaleY_8.png", -1, -4);
        }
        else if(type.equals(CursorType.SCALE_Z))
        {
            setHardwareCursor("/cursors/arrows/scaleZ_8.png", -1, -4);
        }
        else if(type.equals(CursorType.ARROW))
        {
            setHardwareCursor("/cursors/arrows/arrow_8.png", -1, -4);
        }
        else
        {
            setHardwareCursor("/cursors/arrows/arrow_8.png", -1, -4);
        }
    }
}
