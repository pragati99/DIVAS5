package edu.utdallas.mavs.divas.visualization.vis3D.common;

import java.util.HashMap;
import java.util.Map;

/**
 * The specifying type of the mouse cursor.
 */
public class CursorType
{
    /**
     * Default cursor, depicted by an arrow pointing up-left
     */
    public static final CursorType          ARROW   = create(0, "ARROW");
    /**
     * Hand cursor, depicted by a hand
     */
    public static final CursorType          HAND    = create(1, "HAND");
    /**
     * Agent cursor
     */
    public static final CursorType          AGENT   = create(2, "AGENT");
    /**
     * Copy cursor
     */
    public static final CursorType          COPY    = create(3, "COPY");
    /**
     * Drag cursor
     */
    public static final CursorType          MOVE    = create(4, "MOVE");
    /**
     * Environment Object cursor
     */
    public static final CursorType          OBJECT  = create(5, "OBJECT");
    /**
     * Scale X cursor
     */
    public static final CursorType          SCALE_X = create(6, "SCALE_X");
    /**
     * Scale Y cursor
     */
    public static final CursorType          SCALE_Y = create(7, "SCALE_Y");
    /**
     * Scale Z cursor
     */
    public static final CursorType          SCALE_Z = create(8, "SCALE_Z");

    private static Map<Integer, CursorType> CODES;

    /**
     * This input mode code
     */
    private final int                       code;

    /**
     * This input mode remark
     */
    private final String                    type;

    /**
     * Creates a new {@code CursorType}
     * 
     * @param type the CursorType type
     * @return the CursorType
     * @throws NullPointerException if <var>type</var> is {@code null}
     */
    public synchronized static CursorType create(final String type)
    {
        return create(CODES.size(), type);
    }

    private synchronized static CursorType create(final int code, final String type)
    {
        if(CODES == null)
            CODES = new HashMap<Integer, CursorType>();

        final CursorType cursor;
        CODES.put(code, cursor = new CursorType(code, type));
        return cursor;
    }

    private CursorType(final int code, final String type)
    {
        if(null == type)
            throw new NullPointerException();

        this.code = code;
        this.type = type;
    }

    public int getCode()
    {
        return code;
    }

    public String getType()
    {
        return type;
    }

    public static CursorType getCursorType(int mode)
    {
        return CODES.get(mode);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Returns <em>"(code) type"</em>.
     */
    @Override
    public String toString()
    {
        return type;
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

        return code == ((CursorType) o).code;
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
