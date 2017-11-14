package edu.utdallas.mavs.divas.core.config;

import java.io.Serializable;


public class ConfigProperty<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    ConfigKey                 key;
    T                         value;
    String                    tooltip          = "";
    private String            label;
    boolean                   hideInGui        = false;

    public ConfigProperty(ConfigKey key, T value, String tooltip, boolean hideInGui)
    {
        this(key, value, tooltip, key.getKey(), hideInGui);
    }

    public ConfigProperty(ConfigKey key, T value, String tooltip, String label, boolean hideInGui)
    {
        this.key = key;
        this.value = value;
        this.tooltip = tooltip;
        this.label = label;
        this.hideInGui = hideInGui;
    }

    public ConfigKey getKey()
    {
        return key;
    }

    public T getValue()
    {
        return value;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value)
    {
        this.value = (T) value;
    }

    public String getTooltip()
    {
        return tooltip;
    }

    public boolean hideInGui()
    {
        return hideInGui;
    }

    public String getLabel()
    {
        return label;
    }
}
