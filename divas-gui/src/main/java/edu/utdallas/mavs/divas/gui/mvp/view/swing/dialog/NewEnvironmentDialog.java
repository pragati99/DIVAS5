/*
 * File URL : $HeadURL: https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/gui/dialog/NewEnvironmentDialog.java $
 * Revision : $Rev: 608 $
 * Last modified at: $Date: 2010-10-23 00:49:44 -0500 (Sat, 23 Oct 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.RatioOffsetLayout;
public class NewEnvironmentDialog
{
    private JDialog   dialog;

    // Fields
    JTextField        envName           = null;

    // Buttons
    JButton           agentOkButton     = null;
    JButton           agentAddButton    = null;
    JButton           agentCancelButton = null;

    // private ImageSelector ground;
    // private ImageSelector skybox;

    private JCheckBox waterCB;
    private JCheckBox noTextureCB;

    private boolean   cancelled         = true;

    public NewEnvironmentDialog(Frame owner)
    {
        dialog = new JDialog(owner, "New Environment", true);
        dialog.setSize(new Dimension(405, 380));
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new RatioOffsetLayout());

        // ENVIRONMENT NAME
        envName = new JTextField();
        panel.add("0,0,0,0,5,5,195,30", new JLabel("Environment Name"));
        panel.add("0,0,1,0,200,5,-5,32", envName);

        // DIMENSIONS

        // TODO: need to allow users to set the dimensions (root cell bounds) of
        // the environment

        // WATER

        waterCB = new JCheckBox("", true);

        panel.add("0,0,0,0,5,35,195,60", new JLabel("Water"));
        panel.add("0,0,1,0,200,35,-5,60", waterCB);

        noTextureCB = new JCheckBox("", true);

        panel.add("0,0,0,0,5,65,195,90", new JLabel("No Texture"));
        panel.add("0,0,1,0,200,65,-5,90", noTextureCB);

        // SKYBOX

        // skybox = new ImageSelector("Skybox", "N.jpg");
        // for(File f :
        // ResourceManager.getSubDirs(Config.getFileProperty("SkyboxFolder")))
        // skybox.addComboItem(new FolderItem(f));
        // panel.add("0,0,0,0,5,95,195,315", skybox);
        //
        // // GROUND TEXTURE
        //
        // ground = new ImageSelector("Ground Texture");
        // for(File f :
        // ResourceManager.getFiles(Config.getFileProperty("Backgrounds"),
        // "png", "jpg"))
        // ground.addComboItem(new ImageItem(f));
        // panel.add("0,0,1,0,200,95,-5,315", ground);

        // BUTTONS

        panel.add("0,0,0,0,5,320,195,345", getAgentOkButton());
        panel.add("0,0,1,0,200,320,-5,345", getAgentCancelButton());

        JPanel contentPane = (JPanel) dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);

        dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);
    }

    public String getEnvironmentName()
    {
        return envName.getText();
    }

    //
    // public String getGroundTextureName()
    // {
    // return ground.getSelectedItem().toString();
    // }
    //
    // public String getSkyboxName()
    // {
    // return skybox.getSelectedItem().toString();
    // }

    public boolean getWater()
    {
        return waterCB.isSelected();
    }

    public boolean getNoTexture()
    {
        return noTextureCB.isSelected();
    }

    // ****************************************************
    // Button actions
    // ****************************************************

    private JButton getAgentOkButton()
    {
        if(agentOkButton == null)
        {
            agentOkButton = new JButton("OK");
            agentOkButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    cancelled = false;
                    dialog.dispose();
                }
            });
        }
        return agentOkButton;
    }

    private JButton getAgentCancelButton()
    {
        if(agentCancelButton == null)
        {
            agentCancelButton = new JButton("Cancel");
            agentCancelButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    cancelled = true;
                    dialog.dispose();
                }
            });
        }
        return agentCancelButton;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }
}
