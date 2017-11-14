package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;

public class JComboBoxField extends BaseField
{
    private JComboBox<?> jComboBoxField;

    public JComboBoxField(Component parent, String title, Field field, Object instance, Class<?> enumType, Object value)
    {
        super(parent, title, field, instance);
        setup(enumType, value);
    }

    public JComboBoxField(Component parent, String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, Class<?> enumType, Object value)
    {
        super(parent, title, e, instance);
        setup(enumType, value);
    }

    protected void setup(Class<?> enumType, Object value)
    {
        jComboBoxField = new JComboBox<>(enumType.getEnumConstants());
        jComboBoxField.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                setValue(jComboBoxField.getSelectedItem());
            }
        });
        for(int i = 0; i < jComboBoxField.getItemCount(); i++)
            if(jComboBoxField.getItemAt(i).toString().equals(value.toString()))
                jComboBoxField.setSelectedIndex(i);
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(jComboBoxField);
        container.add(new JLabel());
    }

    protected <T extends Enum<T>> void setValue(T value)
    {
        super.setValue(value);
    }
}
