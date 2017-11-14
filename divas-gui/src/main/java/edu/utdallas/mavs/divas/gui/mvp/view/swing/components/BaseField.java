package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JLabel;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;
import edu.utdallas.mavs.divas.core.config.annotation.HelpText;

public class BaseField
{
    private Field                               field;
    private Entry<ConfigKey, ConfigProperty<?>> customEntry;
    protected Object                            instance;

    protected Component                         parent;

    protected JLabel                            titleLabel;

    public BaseField(Component parent, String title, Field field, Object instance)
    {
        this.field = field;
        this.instance = instance;

        this.parent = parent;

        titleLabel = new JLabel(title + ":");
        HelpText helpText = field.getAnnotation(HelpText.class);
        if(helpText != null)
            titleLabel.setToolTipText(helpText.value());
    }

    public BaseField(Component parent, String title, Entry<ConfigKey, ConfigProperty<?>> customEntry, Object instance)
    {
        this.customEntry = customEntry;
        this.instance = instance;

        this.parent = parent;

        titleLabel = new JLabel(title + ":");
        if(field != null)
        {
            HelpText helpText = field.getAnnotation(HelpText.class);
            if(helpText != null)
                titleLabel.setToolTipText(helpText.value());
        }
        if(customEntry != null)
        {
            titleLabel.setToolTipText(customEntry.getValue().getTooltip());
        }
    }

    public void addTo(Container container)
    {
        container.add(titleLabel);
    }

    protected void setValue(Object value)
    {
        try
        {
            if(field != null)
            {
                field.set(instance, value);
            }
            if(customEntry != null)
            {
                customEntry.getValue().setValue(value);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
