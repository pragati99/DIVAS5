package edu.utdallas.mavs.divas.gui.mvp.view.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * RatioOffsetLayout.java -- Layout manager for Java containers
 * This layout manager allows you to specify ratios of left,top, right, bottom
 * plus anOffset for each dimension, characteristics of the components.
 * <p>
 * The main reason for this manager is to fix the size of some items such as label or a input field height, but to allow
 * other dimension to stretch, like a input field width, text areas, etc....
 * <p>
 * Note: This is similiar but takes different inputs from the RatioLayout. RatioLayout takes left,top + width,height not
 * right bottom
 * <p>
 * 
 * <pre>
 * For example,
 * 
 * 	setLayout(new RatioOffsetLayout());
 * 	//  Assume width 300 and height = 400
 * 
 * 	this.add("0,0,0,0,5,10,100,35", new Label("File Name:"));
 * 				// left  = (0 * width) + 5     = 5
 * 				// top   = (0 * height) + 10   = 10
 * 				// right = (0 * width) + 100   = 100  ; component width = 100 - 5 = 95
 * 				// bottom = (0 * height) + 35   = 35  ; component height = 35 - 10 = 25
 * 
 * 	this.add("0,0,1,0,105,10,-10,35", new TextField());
 * 				// left  = (0 * width) + 105   = 105
 * 				// top   = (0 * height) + 10   = 10
 * 				// right = (1 * width) + (-10) = 290 ; component width = 290 - 105 = 185
 * 				// bottom= (0 * height) + 35   = 35  ; component height = 35 - 10 = 25
 * 
 * 	this.add("0,0,0,0,5,40,200,65", new Label("File Contents"));
 * 				// left  = (0 * width) + 5   = 5
 * 				// top   = (0 * height) + 40 = 40
 * 				// right = (0 * width) + 200 = 200 ; component width = 200 - 5 = 195
 * 				// top   = (0 * height) + 65 = 65  ; component height = 65 - 40 = 25
 * 
 * 	this.add("0,0,1,1,10,70,-10,-50", new TextField());
 * 				// left  = (0 * width) + 10  = 10
 * 				// top   = (0 * height) + 70 = 70
 * 				// right = (1 * width) + -10 = 290 ; component width = 290 -10 = 190
 * 				// top   = (1 * height)+ -50 = 350 ; component height = 350 - 70 = 280
 * 
 * 	this.add(".5,1,.5,1,-50,-45,50,-5", new Button("OK"));
 * 				// left  = (.5 * width) + -50  = 100
 * 				// top   = (1 * height) + -45  = 355
 * 				// right = (.5 * width) + 50   = 299 ; component width = 200 -100 = 100
 * 				// top   = (1 * height) + -5   = 395 ; component height = 395 - 355 = 40
 * </pre>
 * 
 * @author Gary M. Leask
 * @version 1.00, 09/3/98
 */

public class RatioOffsetLayout implements LayoutManager
{
    /**
     * track the ratios for each object of form
     * "x1ratio,y1ratio;x2ratio,y2ratio;x1offset,y1offset;x2offset,y2offset"
     */
    private Vector<String>    ratios     = new Vector<String>(10);
    /**
     * track the components also so we can remove associated modifier if necessary.
     */
    private Vector<Component> components = new Vector<Component>(10);

    @Override
    public void addLayoutComponent(String r, Component comp)
    {
        ratios.addElement(r);
        components.addElement(comp);
    }

    @Override
    public void layoutContainer(Container target)
    {
        Insets insets = target.getInsets();
        int ncomponents = target.getComponentCount();
        Dimension d = target.getSize();

        d.width -= insets.left + insets.right;
        d.height -= insets.top + insets.bottom;
        if(ratios.size() > 0)
        {
            for(int i = 0; i < ncomponents; i++)
            {
                Component comp = target.getComponent(i);
                StringTokenizer st = new StringTokenizer(ratios.elementAt(i), ", \t;");
                float rx1 = Float.valueOf(st.nextToken()).floatValue();
                float ry1 = Float.valueOf(st.nextToken()).floatValue();
                float rx2 = 0;
                float ry2 = 0;
                float ox1 = 0;
                float oy1 = 0;
                float ox2 = 0;
                float oy2 = 0;
                int w, h;
                int x = (int) (d.width * rx1);
                int y = (int) (d.height * ry1);

                if(st.hasMoreElements())
                { // get width, height if they exist
                    rx2 = Float.valueOf(st.nextToken()).floatValue();
                    ry2 = Float.valueOf(st.nextToken()).floatValue();
                    if(st.hasMoreElements())
                    {
                        ox1 = Float.valueOf(st.nextToken()).floatValue();
                        oy1 = Float.valueOf(st.nextToken()).floatValue();
                        ox2 = Float.valueOf(st.nextToken()).floatValue();
                        oy2 = Float.valueOf(st.nextToken()).floatValue();
                        x = (int) (d.width * rx1 + ox1);
                        y = (int) (d.height * ry1 + oy1);
                    }
                    w = (int) (d.width * rx2 + ox2 - x);
                    h = (int) (d.height * ry2 + oy2 - y);
                }
                else
                {
                    Dimension compDim = comp.getPreferredSize();

                    w = compDim.width;
                    h = compDim.height;
                }
                comp.setBounds(x + insets.left, y + insets.top, w, h);
            }
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container target)
    {
        return target.getSize();
    }

    @Override
    public Dimension preferredLayoutSize(Container target)
    {
        return target.getSize();
    }

    @Override
    public void removeLayoutComponent(Component comp)
    {
        int i = components.indexOf(comp);

        if(i != -1)
        {
            ratios.removeElementAt(i);
            components.removeElementAt(i);
        }
    }

    @Override
    public String toString()
    {
        return getClass().getName();
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/14/2001 9:49:54 AM)
     * 
     * @param c
     *        java.awt.Component
     * @param newConstraints
     *        java.lang.String
     */
    public void updateConstraints(Component c, String newConstraints)
    {
        int i = components.indexOf(c);

        if(i != -1)
        {
            ratios.setElementAt(newConstraints, i);
        }
    }

}
