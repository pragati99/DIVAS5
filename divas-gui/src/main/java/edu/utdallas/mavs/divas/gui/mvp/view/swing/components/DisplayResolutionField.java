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
import edu.utdallas.mavs.divas.core.config.DisplayResolution;

public class DisplayResolutionField extends BaseField
{
    private JComboBox<?>     resField;
    static DisplayResolution defaultDisplayResolution = new DisplayResolution(1280, 768, 32, 75);

    public DisplayResolutionField(Component parent, String title, Field field, Object instance, DisplayResolution value)
    {
        super(parent, title, field, ((instance == null) ? defaultDisplayResolution : instance));
        setup((value == null) ? defaultDisplayResolution : value);
    }

    public DisplayResolutionField(final Component parent, final String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, DisplayResolution value)
    {
        super(parent, title, e, instance);
        setup(value);
    }

    protected void setup(DisplayResolution value)
    {
        DisplayResolution[] bestSupportedResolutions = DisplayResolution.getBestSupportedResolutions();
        if(bestSupportedResolutions == null)
        {
            bestSupportedResolutions = new DisplayResolution[] { defaultDisplayResolution };
        }

        resField = new JComboBox<Object>(bestSupportedResolutions);
        resField.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                setValue((DisplayResolution) resField.getSelectedItem());
            }
        });
        for(int i = 0; i < resField.getItemCount(); i++)
            if(resField.getItemAt(i).toString().equals(value.toString()))
                resField.setSelectedIndex(i);
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(resField);
        container.add(new JLabel());
    }

    protected void setValue(DisplayResolution value)
    {
        super.setValue(value);
    }
}
