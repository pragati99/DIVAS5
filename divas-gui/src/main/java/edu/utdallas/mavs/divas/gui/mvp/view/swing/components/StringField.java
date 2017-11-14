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

public class StringField extends BaseField
{
    private JTextField textField;

    public StringField(Component parent, String title, Field field, Object instance, final String value)
    {
        super(parent, title, field, instance);
        setup(value);
    }

    public StringField(final Component parent, final String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, final String value)
    {
        super(parent, title, e, instance);
        setup(value);
    }

    protected void setup(final String value)
    {
        textField = new JTextField(value);
        textField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                setValue(textField.getText());
            }
        });
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(textField);
        container.add(new JLabel());
    }

    protected void setValue(int value)
    {
        super.setValue(value);
    }
}
