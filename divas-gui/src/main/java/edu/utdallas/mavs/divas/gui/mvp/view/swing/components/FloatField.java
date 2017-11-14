package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;

public class FloatField extends BaseField
{
    private JTextField floatField;

    private float      value;

    public FloatField(final Component parent, final String title, Field field, Object instance, final Float value)
    {
        super(parent, title, field, instance);
        setup(value);
    }

    public FloatField(final Component parent, final String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, final Float value)
    {
        super(parent, title, e, instance);
        setup(value);
    }

    protected void setup(final Float value)
    {
        this.value = value;
        floatField = new JTextField(Float.toString(value));
        floatField.setHorizontalAlignment(JTextField.RIGHT);
        floatField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                try
                {
                    setValue(Float.valueOf(floatField.getText()));
                }
                catch(NumberFormatException ex)
                {
                    floatField.setText(Float.toString(FloatField.this.value));
                }
            }
        });
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(floatField);
        container.add(new JLabel());
    }

    protected void setValue(float value)
    {
        this.value = value;
        super.setValue(value);
    }
}
