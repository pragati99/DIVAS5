package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class about the header panel in the 2D visualizer tool box.
 * <p>
 * This class initializes the header panel which contains the label that displays the current simulation mode and controlling the zoom factor. It also stores the current simulation mode which is used to determine the behavior of the mouse click in
 * the map panel. And stores the zoom factor for the 2D visualizer.
 */
public class HeaderPanel extends JPanel
{
    private SimulationMode    simMode          = SimulationMode.SELECTION_MODE;
    private static final long serialVersionUID = 1L;
    JLabel                    title            = new JLabel("Options");
    private JLabel            simModeLabel     = new JLabel();
    private JLabel            zoomLabel        = new JLabel("Zoom");
    private double            zoomFactor       = 5;
    private JSlider           zoomSlider       = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 5);

    /**
     * An enumeration of the possible simulation modes.
     */
    public enum SimulationMode
    {
        /**
         * Selection mode. An object will be selected when the mouse is clicked on it.
         */
        SELECTION_MODE,
        /**
         * Add agents mode. An agent will be added when the mouse is clicked on that position.
         */
        ADD_AGENT,
        /**
         * Add explosion mode. An explosion with smoke event will be triggered in the position
         * where the mouse is clicked.
         */
        ADD_EXPLOSION,
        /**
         * Add explosion with out smoke mode. An explosion without smoke event will be triggered in the position
         * where the mouse is clicked.
         */
        ADD_EXPLOSION_NO_SMOKE,
        /**
         * Add fireworks mode. A fireworks event will be triggered in the position when the mouse is clicked.
         */
        ADD_FIREWORK,
        /**
         * Split cell mode. A cell controller will split into two cells when the mouse is clicked on it.
         */
        SPLIT_CELL,
        /**
         * Merge cell mode. A cell controller will be merged with its sibling when the mouse is clicked on it.
         */
        MERGE_CELL,
        /**
         * Add environment object mode. An environment object will be added in the position when the
         * mouse is clicked.
         */
        ADD_ENV_OBJECT,
        /**
         * Edit scale X mode. The scale X of the environment object will be scaled when the mouse is dragged
         * on the selected object.
         */
        EDIT_SCALE_X,
        /**
         * Edit scale Z mode. The scale Z of the environment object will be scaled when the mouse is dragged
         * on the selected object.
         */
        EDIT_SCALE_Z,
        /**
         * Edit environment object location mode. The location of the environment object will be changed when
         * the mouse is dragged on the selected object.
         */
        EDIT_LOCATION,
        /**
         * Edit rotation mode. The rotation of the environment object will be changed when
         * the mouse is dragged on the selected object.
         */
        EDIT_ROTATION, 
        
        /**
         * Change the agent goal to a different point
         */
        CHANGE_AGENT_GOAL
    }

    /**
     * Constructs the header panel for the 2D visualizer tool box.
     * <p>
     * Intitlizes the header panel, and assigns the default values for the simulation mode to be <code>SELECTION_MODE<code> and for the zoom factor to be 5, which represents the 100% zoom
     * factor.
     */
    public HeaderPanel()
    {
        super();
        title.setForeground(Color.BLUE);
        add(title);
        simModeLabel.setForeground(Color.RED);
        add(simModeLabel);
        add(zoomLabel);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setPaintTicks(true);
        zoomSlider.addChangeListener(

        // anonymous inner class to handle JSlider events
        new ChangeListener()
        {
            // handle change in slider value
            @Override
            public void stateChanged(ChangeEvent e)
            {
                zoomFactor = zoomSlider.getValue();
            }
        } // end anonymous inner class

        ); // end call to addChangeListener
        add(zoomSlider);
    }

    /**
     * @return Gets the current simulation mode
     */
    public SimulationMode getSimMode()
    {
        return simMode;
    }

    /**
     * @return The current simulation mode label.
     */
    public JLabel getSimModeLabel()
    {
        return simModeLabel;
    }

    /**
     * @return The zoom factor for the 2D visualizer.
     */
    public double getZoomFactor()
    {
        return zoomFactor;
    }

}
