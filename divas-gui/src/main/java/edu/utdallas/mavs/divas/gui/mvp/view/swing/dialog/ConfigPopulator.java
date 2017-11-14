package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JDialog;

import edu.utdallas.mavs.divas.core.config.BaseConfig;
import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;
import edu.utdallas.mavs.divas.core.config.DisplayResolution;
import edu.utdallas.mavs.divas.core.config.annotation.HideInGUI;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.BooleanField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.ColorField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.DisplayResolutionField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.FloatField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.IntField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.JComboBoxField;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.components.StringField;

public class ConfigPopulator
{
    public static void populate(BaseConfig instance, JDialog parent)
    {
        populateFields(instance, parent);
        populateCustomProperties(instance, parent);
    }

    protected static void populateFields(BaseConfig instance, JDialog parent)
    {
        for(Field f : instance.getClass().getFields())
        {
            // make sure the field is not marked as hidden
            if(f.getAnnotation(HideInGUI.class) == null)
            {
                try
                {
                    Class<?> type = f.getType();
                    Object object = f.get(instance);

                    if(type.equals(Color.class))
                    {
                        new ColorField(parent, getTitle(f.getName()), f, instance, (Color) object).addTo(parent);
                    }
                    else if(type.equals(int.class))
                    {
                        new IntField(parent, getTitle(f.getName()), f, instance, (Integer) object).addTo(parent);
                    }
                    else if(type.equals(float.class))
                    {
                        new FloatField(parent, getTitle(f.getName()), f, instance, (Float) object).addTo(parent);
                    }
                    else if(type.equals(String.class))
                    {
                        new StringField(parent, getTitle(f.getName()), f, instance, (String) object).addTo(parent);
                    }
                    else if(type.equals(DisplayResolution.class))
                    {
                        new DisplayResolutionField(parent, getTitle(f.getName()), f, instance, (DisplayResolution) object).addTo(parent);
                    }
                    else if(type.equals(boolean.class))
                    {
                        new BooleanField(parent, getTitle(f.getName()), f, instance, (Boolean) object).addTo(parent);
                    }
                    else if(type.isEnum())
                    {
                        new JComboBoxField(parent, getTitle(f.getName()), f, instance, type, type.cast(object)).addTo(parent);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void populateCustomProperties(BaseConfig instance, JDialog parent)
    {
        if(instance.customProperties.size() > 0)
        {
            for(Entry<ConfigKey, ConfigProperty<?>> f : instance.customProperties.entrySet())
            {
                // make sure the field is not marked as hidden
                if(!f.getValue().hideInGui())
                {
                    try
                    {
                        Class<?> type = f.getValue().getValue().getClass();
                        Object object = f.getValue().getValue();
                        String title = getTitle(f.getValue().getLabel());

                        if(type.equals(Color.class))
                        {
                            new ColorField(parent, title, f, instance, (Color) object).addTo(parent);
                        }
                        else if(type.equals(Integer.class))
                        {
                            new IntField(parent, title, f, instance, (Integer) object).addTo(parent);
                        }
                        else if(type.equals(Float.class))
                        {
                            new FloatField(parent, title, f, instance, (Float) object).addTo(parent);
                        }
                        else if(type.equals(String.class))
                        {
                            new StringField(parent, title, f, instance, (String) object).addTo(parent);
                        }
                        else if(type.equals(DisplayResolution.class))
                        {
                            new DisplayResolutionField(parent, title, f, instance, (DisplayResolution) object).addTo(parent);
                        }
                        else if(type.equals(Boolean.class))
                        {
                            new BooleanField(parent, title, f, instance, (Boolean) object).addTo(parent);
                        }
                        else if(type.isEnum())
                        {
                            new JComboBoxField(parent, title, f, instance, type, type.cast(object)).addTo(parent);
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String getTitle(String orig)
    {
        String result = "";

        boolean capNext = true;

        for(char c : orig.toCharArray())
        {
            if(c == '_')
            {
                result += " ";
                capNext = true;
            }
            else
            {
                if(capNext)
                {
                    result += Character.toUpperCase(c);
                    capNext = false;
                }
                else
                    result += c;
            }
        }

        orig.replace("_", " ");

        return result;
    }
}
