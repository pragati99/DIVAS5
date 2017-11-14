package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utdallas.mavs.divas.core.config.ConfigKey;
import edu.utdallas.mavs.divas.core.config.ConfigProperty;

public class ColorField extends BaseField
{
    private ColorPanel colorPanel;
    private JSlider    alphaSlider;

    private Color      value;

    public ColorField(Component parent, final String title, Field field, Object instance, final Color value)
    {
        super(parent, title, field, instance);
        setup(title, value);
    }

    public ColorField(Component parent, String title, Entry<ConfigKey, ConfigProperty<?>> e, Object instance, final Color value)
    {
        super(parent, title, e, instance);
        setup(title, value);
    }

    protected void setup(final String title, final Color value)
    {
        colorPanel = new ColorPanel();
        colorPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Color c = JColorChooser.showDialog(ColorField.this.parent, "Choose " + title, value);

                if(c != null)
                    changeColor(c);
            }
        });

        alphaSlider = new JSlider(0, 255, value.getAlpha());
        alphaSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                changeAlpha(alphaSlider.getValue());
            }
        });

        setValue(value);
    }

    @Override
    public void addTo(Container container)
    {
        super.addTo(container);
        container.add(colorPanel);
        container.add(alphaSlider);
    }

    protected void setValue(Color value)
    {
        this.value = value;
        super.setValue(value);
        colorPanel.setColor(value);
    }

    public void changeColor(Color color)
    {
        setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaSlider.getValue()));
    }

    public void changeAlpha(int alpha)
    {
        setValue(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
    }

    private class ColorPanel extends JLabel
    {
        private static final long serialVersionUID = 1L;

        Color                     c;

        Color                     bg1              = Color.GRAY;
        Color                     bg2              = Color.LIGHT_GRAY;

        int                       PIXELS           = 4;

        public void setColor(Color c)
        {
            this.c = c;
            repaint();
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Insets inset = this.getInsets();
            Rectangle clip = g.getClipBounds();

            int height = getSize().height - inset.bottom;
            int width = getSize().width - inset.right;

            if(clip.y + clip.height > height)
                clip.height = height - clip.y;

            if(clip.x + clip.width > width)
                clip.width = width - clip.x;

            g.setClip(clip);

            int yRepeat = (int) Math.ceil(clip.getHeight() / PIXELS);
            int xRepeat = (int) Math.ceil(clip.getWidth() / PIXELS);

            for(int i = 0; i <= yRepeat; i++)
            {
                if(i % 2 == 0)
                    g.setColor(bg1);
                else
                    g.setColor(bg2);
                for(int j = 0; j <= xRepeat; j++)
                {
                    g.fillRect(j * PIXELS + inset.left, i * PIXELS + inset.top, PIXELS, PIXELS);

                    if(g.getColor().equals(bg1))
                        g.setColor(bg2);
                    else
                        g.setColor(bg1);
                }
            }

            g.setColor(c);
            g.fillRect(0, 0, width, height);
        }
    }
}
