package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;
import edu.utdallas.mavs.divas.core.config.annotation.IntConfig;

public class IntField extends BaseField
{
    private JComponent intField;

    public IntField(final Component parent, final String title, Field field, Object instance, final Integer value)
    {
        super(parent, title, field, instance);
        IntConfig config = field.getAnnotation(IntConfig.class);
        setup(value, config);
    }

    public IntField(final Component parent, final String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, final Integer value)
    {
        super(parent, title, e, instance);
        setup(value, null);
    }

    protected void setup(final Integer value, IntConfig config)
    {
        Integer min = Integer.MIN_VALUE;
        Integer max = Integer.MAX_VALUE;

        if(config != null)
        {
            if(config.combo())
            {
                int[] vals = config.values();
                Integer[] oVals = new Integer[vals.length];
                for(int i = 0; i < vals.length; i++)
                    oVals[i] = Integer.valueOf(vals[i]);

                final JComboBox<?> combo = new JComboBox<Object>(oVals);
                combo.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        setValue(combo.getSelectedItem());
                    }
                });

                for(int i = 0; i < combo.getItemCount(); i++)
                {
                    Integer tmp = (Integer) combo.getItemAt(i);
                    if(tmp.intValue() == value.intValue())
                        combo.setSelectedItem(combo.getItemAt(i));
                }

                intField = combo;
            }
            else
            {
                min = config.min();
                max = config.max();
            }
        }

        if(intField == null)
        {
            final JSpinner spinner = new JSpinner(new SpinnerNumberModel(new Integer(value), min, max, new Integer(1)));
            spinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    setValue(((SpinnerNumberModel) spinner.getModel()).getNumber());
                }
            });

            intField = spinner;
        }
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(intField);
        container.add(new JLabel());
    }

    protected void setValue(int value)
    {
        super.setValue(value);
    }
}
