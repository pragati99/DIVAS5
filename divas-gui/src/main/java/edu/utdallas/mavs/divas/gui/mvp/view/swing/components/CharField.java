package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JTextField;
public class CharField extends BaseField
{
    private JTextField textField;

    public CharField(Component parent, String title, Field field, Object instance, final Character value)
    {
        super(parent, title, field, instance);

        textField = new JTextField(Integer.toString(value));
        textField.setColumns(1);
        textField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                setValue(Integer.valueOf(textField.getText()).intValue());
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
