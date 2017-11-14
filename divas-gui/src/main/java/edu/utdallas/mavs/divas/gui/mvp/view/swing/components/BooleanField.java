package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;

public class BooleanField extends BaseField
{
    private JCheckBox boolField;

    public BooleanField(final Component parent, final String title, Field field, Object instance, final boolean value)
    {
        super(parent, title, field, instance);
        setup(value);
    }

    public BooleanField(final Component parent, final String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, boolean value)
    {
        super(parent, title, e, instance);
        setup(value);
    }

    protected void setup(final boolean value)
    {
        boolField = new JCheckBox("");
        boolField.setSelected(value);
        boolField.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                setValue(boolField.isSelected());
            }
        });
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(boolField);
        container.add(new JLabel());
    }

    protected void setValue(boolean value)
    {
        super.setValue(value);
    }
}
