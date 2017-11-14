package edu.utdallas.mavs.divas.visualization.vis3D.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Modes of operation of the application state. {@code InputMode} represents extensible enums for simulation inputMode
 */
public class InputMode
{
    /**
     * Selection Mode.
     */
    public static final InputMode          SELECTION  = InputMode.create(0, "Selection");

    /**
     * Selection Mode.
     */
    public static final InputMode          ADD_AGENT  = InputMode.create(1, "Add Agent");

    /**
     * Selection Mode.
     */
    public static final InputMode          ADD_OBJECT = InputMode.create(2, "Add Environment Object");

    private static Map<Integer, InputMode> CODES;

    /**
     * This input mode code
     */
    private final int                      code;

    /**
     * This input mode remark
     */
    private final String                   remark;

    /**
     * Creates a new {@code InputMode}
     * 
     * @param remark the InputMode remark
     * @return the input mode
     * @throws NullPointerException if <var>remark</var> is {@code null}
     */
    public synchronized static InputMode create(final String remark)
    {
        return create(CODES.size(), remark);
    }

    private synchronized static InputMode create(final int code, final String remark)
    {
        if(CODES == null)
            CODES = new HashMap<Integer, InputMode>();

        final InputMode mode;
        CODES.put(code, mode = new InputMode(code, remark));
        return mode;
    }

    private InputMode(final int code, final String remark)
    {
        if(null == remark)
            throw new NullPointerException();

        this.code = code;
        this.remark = remark;
    }

    public int getCode()
    {
        return code;
    }

    public String getRemark()
    {
        return remark;
    }

    public static int size()
    {
        return CODES.size();
    }

    public static InputMode getInputMode(int mode)
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
        return remark;
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

        return code == ((InputMode) o).code;
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
