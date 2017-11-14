package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.visualization.vis2D.panels.HeaderPanel.SimulationMode;

/**
 * This class is responsible for initializing the simulation mode panel in the 2D visualizer tool box.
 * <p>
 */
public class SimulationModePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JCheckBox         visionCone, agentIdToolTip;
    private JButton           selectionModeButton, addAgentButton, addExplosionButton, addExplosionNoSmokeButton, addFireworkButton, editScaleXButton, editScaleZButton, editLocationButton, editRotationButton, splitCellButton, mergeCellButton,
            changeAgentGoalButton;

    private ToolBox           toolBox;

    /**
     * Constructs the simulation mode panel by adding the button that controls the simulation mode
     * to the panel.
     * 
     * @param toolBox
     *        The main tool box for the 2D visualizer that contains the simulation mode panel.
     */
    public SimulationModePanel(ToolBox toolBox)
    {
        super();
        this.toolBox = toolBox;
        setLayout(new GridLayout(7, 2));

        visionCone = new JCheckBox("Vision Cone Enabled");
        add(visionCone);

        agentIdToolTip = new JCheckBox("Show Agent ID");
        add(agentIdToolTip);

        Icon selectionIcon = new ImageIcon(Config.getDefaultCursorIcon());
        selectionModeButton = new JButton("Selection Mode", selectionIcon);
        add(selectionModeButton);

        Icon addAgentIcon = new ImageIcon(Config.getAgentCursorIcon());
        addAgentButton = new JButton("Add agent", addAgentIcon);
        add(addAgentButton);

        Icon addExplosionIcon = new ImageIcon(Config.getBombCursorIcon());
        addExplosionButton = new JButton("Add explosion", addExplosionIcon);
        add(addExplosionButton);

        Icon addExplosionNoSmokeIcon = new ImageIcon(Config.getBombNoSmokeCursorIcon());
        addExplosionNoSmokeButton = new JButton("Add explosion", addExplosionNoSmokeIcon);
        add(addExplosionNoSmokeButton);

        Icon addFireworkIcon = new ImageIcon(Config.getFireworksCursorIcon());
        addFireworkButton = new JButton("Add fireworks", addFireworkIcon);
        add(addFireworkButton);

        Icon splitCellIcon = new ImageIcon(Config.getSplitCellCursorIcon());
        splitCellButton = new JButton("Split cell controller", splitCellIcon);
        add(splitCellButton);

        Icon mergeCellIcon = new ImageIcon(Config.getMergeCellCursorIcon());
        mergeCellButton = new JButton("Merge cell controller", mergeCellIcon);
        add(mergeCellButton);

        Icon editScaleXIcon = new ImageIcon(Config.getScaleCursorIcon());
        editScaleXButton = new JButton("Edit scale X", editScaleXIcon);
        add(editScaleXButton);

        Icon editScaleY = new ImageIcon(Config.getScaleCursorIcon());
        editScaleZButton = new JButton("Edit scale Z", editScaleY);
        add(editScaleZButton);

        Icon editLocationIcon = new ImageIcon(Config.getTranslateCursorIcon());
        editLocationButton = new JButton("Edit Location", editLocationIcon);
        add(editLocationButton);

        Icon editRotationIcon = new ImageIcon(Config.getRotateCursorIcon());
        editRotationButton = new JButton("Edit Rotation", editRotationIcon);
        add(editRotationButton);

        Icon changeAgentGoalIcon = new ImageIcon(Config.getRotateCursorIcon());
        changeAgentGoalButton = new JButton("Change Agent Goal", changeAgentGoalIcon);
        add(changeAgentGoalButton);

        // register events for JButtons
        ButtonHandler handler = new ButtonHandler();
        selectionModeButton.addActionListener(handler);
        addAgentButton.addActionListener(handler);
        addExplosionButton.addActionListener(handler);
        addExplosionNoSmokeButton.addActionListener(handler);
        addFireworkButton.addActionListener(handler);
        splitCellButton.addActionListener(handler);
        mergeCellButton.addActionListener(handler);
        editScaleXButton.addActionListener(handler);
        editScaleZButton.addActionListener(handler);
        editLocationButton.addActionListener(handler);
        editRotationButton.addActionListener(handler);
        changeAgentGoalButton.addActionListener(handler);

    }

    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource() == selectionModeButton)
            {
                toolBox.setSimMode(SimulationMode.SELECTION_MODE);
            }
            else if(event.getSource() == addAgentButton)
            {
                toolBox.setSimMode(SimulationMode.ADD_AGENT);
            }
            else if(event.getSource() == addExplosionButton)
            {
                toolBox.setSimMode(SimulationMode.ADD_EXPLOSION);
            }
            else if(event.getSource() == addExplosionNoSmokeButton)
            {
                toolBox.setSimMode(SimulationMode.ADD_EXPLOSION_NO_SMOKE);
            }
            else if(event.getSource() == addFireworkButton)
            {
                toolBox.setSimMode(SimulationMode.ADD_FIREWORK);
            }
            else if(event.getSource() == splitCellButton)
            {
                toolBox.setSimMode(SimulationMode.SPLIT_CELL);
            }
            else if(event.getSource() == mergeCellButton)
            {
                toolBox.setSimMode(SimulationMode.MERGE_CELL);
            }
            else if(event.getSource() == editScaleXButton)
            {
                toolBox.setSimMode(SimulationMode.EDIT_SCALE_X);
            }
            else if(event.getSource() == editScaleZButton)
            {
                toolBox.setSimMode(SimulationMode.EDIT_SCALE_Z);
            }
            else if(event.getSource() == editLocationButton)
            {
                toolBox.setSimMode(SimulationMode.EDIT_LOCATION);
            }
            else if(event.getSource() == editRotationButton)
            {
                toolBox.setSimMode(SimulationMode.EDIT_ROTATION);
            }
            else if(event.getSource() == changeAgentGoalButton)
            {
                toolBox.setSimMode(SimulationMode.CHANGE_AGENT_GOAL);
            }

            toolBox.setSimModeLabel("Current Simulation Mode: " + toolBox.getSimMode().toString());
        }
    } // end private inner class ButtonHandler

    /**
     * @return boolean flag that indicates if the vision cone will be shown for the agent in the visualizer.
     */
    public boolean getVisionCone()
    {
        return visionCone.isSelected();
    }

    /**
     * @return boolean flag that indicates if the agent ID will be shown as a tool tip attached to the
     *         agent in the visualizer.
     */
    public boolean getAgentIdToolTip()
    {
        return agentIdToolTip.isSelected();
    }
}
