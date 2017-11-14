package edu.utdallas.mavs.divas.core.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ConfigKey implements Serializable
{
    private static final long              serialVersionUID = 1L;

    private static Map<Integer, ConfigKey> CODES;

    /**
     * This input mode code
     */
    private final int                      code;

    /**
     * This input mode remark
     */
    private final String                   key;

    public synchronized static ConfigKey create(final String key)
    {
        if(CODES == null)
            CODES = new HashMap<Integer, ConfigKey>();

        final ConfigKey status;
        CODES.put(key.hashCode(), status = new ConfigKey(key.hashCode(), key));
        return status;
    }

    private ConfigKey(final int code, final String key)
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

    public static ConfigKey getInputMapping(int mode)
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
        return "(" + code + ") " + key;
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

        return code == ((ConfigKey) o).code;
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
