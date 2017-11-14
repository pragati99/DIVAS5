package edu.utdallas.mavs.divas.visualization.vis3D.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Input mappings for mappings inputs for the input manager
 */
public class InputMapping
{
    // Please keep it alphabetically ordered

    // C
    public static final InputMapping          CELLS       = create(0, "Cells");
    public static final InputMapping          CONTEXT     = create(1, "Context");
    public static final InputMapping          COPY        = create(2, "Copy");

    // D
    public static final InputMapping          DELETE      = create(3, "Delete");
    public static final InputMapping          DIVAS       = create(4, "Divas");
    public static final InputMapping          DEBUG       = create(5, "DebugMode");

    // E
    public static final InputMapping          ENV         = create(6, "Env");
    public static final InputMapping          EDITING     = create(7, "Editing");
    public static final InputMapping          ESC         = create(8, "Escape");

    // H
    public static final InputMapping          HELP_PANEL  = create(9, "helpPanel");

    // M
    public static final InputMapping          MENU_NIFTY  = create(10, "menuNiftyGUI");
    public static final InputMapping          MODE_DOWN   = create(11, "ModeDown");
    public static final InputMapping          MODE_UP     = create(12, "ModeUp");
    public static final InputMapping          MOUSE_LEFT  = create(13, "MouseLeft");
    public static final InputMapping          MOUSE_RIGHT = create(14, "MouseRight");
    public static final InputMapping          MOUSE_UP    = create(15, "MouseUp");
    public static final InputMapping          MOUSE_DOWN  = create(16, "MouseDown");

    // S
    public static final InputMapping          SCALE       = create(17, "Scale");
    public static final InputMapping          SCALE_X     = create(18, "ScaleX");
    public static final InputMapping          SCALE_Y     = create(19, "ScaleY");
    public static final InputMapping          SCALE_Z     = create(20, "ScaleZ");
    public static final InputMapping          SHOOT       = create(21, "Shoot");
    public static final InputMapping          SNAP        = create(22, "Snap");
    public static final InputMapping          SEL         = create(23, "Selection");
    public static final InputMapping          SPEED_UP    = create(24, "SpeedUp");
    public static final InputMapping          SPEED_DOWN  = create(25, "SpeedDown");

    private static Map<Integer, InputMapping> CODES;

    /**
     * This input mode code
     */
    private final int                         code;

    /**
     * This input mode remark
     */
    private final String                      key;

    /**
     * Creates a new {@code InputMapping}
     * 
     * @param key the InputMapping key
     * @return the input mapping
     * @throws NullPointerException if <var>remark</var> is {@code null}
     */
    public synchronized static InputMapping create(final String key)
    {
        return create(CODES.size(), key);
    }

    private synchronized static InputMapping create(final int code, final String key)
    {
        if(CODES == null)
            CODES = new HashMap<Integer, InputMapping>();

        final InputMapping status;
        CODES.put(code, status = new InputMapping(code, key));
        return status;
    }

    private InputMapping(final int code, final String key)
    {
        if(null == key)
            throw new NullPointerException();

        this.code = code;
        this.key = key;
    }

    public int getCode()
    {
        return code;
    }

    public String getKey()
    {
        return key;
    }

    public static InputMapping getInputMapping(int mode)
    {
        return CODES.get(mode);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns <em>"(code) remark"</em>.
     */
    @Override
    public String toString()
    {
        return key;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Considers only {@link #code} for equality.
     */
    @Override
    public boolean equals(final Object o)
    {
        if(this == o)
            return true;
        if(null == o)
            return false;
        if(getClass() != o.getClass())
            return false;

        return code == ((InputMapping) o).code;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns {@link #code} as the hash code.
     */
    @Override
    public int hashCode()
    {
        return code;
    }
}
