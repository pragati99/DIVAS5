package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.utdallas.mavs.divas.core.config.VisConfig;

public class VisConfigDialog
{
    static VisConfig savedvisConfig;

    static JButton   useButton = new JButton("Use Configuration");
    static JDialog   dialog;

    public VisConfigDialog()
    {}

    public void showDialog(JFrame parent, VisConfig visConfig)
    {
        savedvisConfig = visConfig;

        dialog = new JDialog(parent, "Configure Simulation", true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setLayout(new GridLayout(0, 3));

        ConfigPopulator.populate(visConfig, dialog);

        dialog.getRootPane().setDefaultButton(useButton);

        dialog.add(new JLabel(""));
        dialog.add(useButton);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static JButton getSaveConfigurationButton()
    {
        return useButton;
    }

    public static void saveVisConfig()
    {
        savedvisConfig.save();
        //dialog.dispose();
    }
}
