package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.utdallas.mavs.divas.core.config.SimConfig;

public class SimConfigDialog
{
    static SimConfig savedSimConfig;

    JButton          useButton;
    JDialog          dialog;

    public SimConfigDialog()
    {
        useButton = new JButton("Use Configuration");
    }

    public void showDialog(JFrame parent, SimConfig simConfig)
    {
        savedSimConfig = simConfig;

        dialog = new JDialog(parent, "Configure Simulation", true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setLayout(new GridLayout(0, 3));

        ConfigPopulator.populate(simConfig, dialog);

        dialog.getRootPane().setDefaultButton(useButton);

        dialog.add(new JLabel(""));
        dialog.add(useButton);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public JButton getSaveOptionsButton()
    {
        return useButton;
    }

    public void saveSimConfig()
    {
        savedSimConfig.save();
        // dialog.dispose();
    }
}
