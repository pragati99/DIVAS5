package edu.utdallas.mavs.divas.visualization.vis2D;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.utdallas.mavs.divas.visualization.vis2D.panels.MapPanel;
import edu.utdallas.mavs.divas.visualization.vis2D.panels.ToolBox;
import edu.utdallas.mavs.divas.visualization.vis2D.spectator.VisualSpectator2D;

/**
 * Creates the <code>JFrame</code> for the 2D visualizer application
 */
public class Visualizer2DFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private MapPanel          mapPanel;
    private ToolBox           toolBox;

    /**
     * Constructs the main frame for the 2D visualizer application
     */
    public Visualizer2DFrame()
    {
        super("Divas 2D Visualizer");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setResizable(false);
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Adds the tool box and the visualization panels to the main frame of the 2D visualizer
     */
    public void start()
    {
        toolBox = new ToolBox();
        mapPanel = new MapPanel(toolBox);
        add(toolBox, BorderLayout.EAST);

        JScrollPane scroller = new JScrollPane(mapPanel);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scroller);

        setPreferredSize(new Dimension(1385, 1030));
        //setPreferredSize(new Dimension(779, 773));
        pack();
        

        if(VisualSpectator2D.getCycles() != -1)
            mapPanel.reloadMap();
    }
}
