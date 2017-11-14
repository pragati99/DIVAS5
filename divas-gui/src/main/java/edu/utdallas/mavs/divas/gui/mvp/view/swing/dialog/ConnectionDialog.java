package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.utdallas.mavs.divas.core.config.Config;
public class ConnectionDialog
{
    protected JDialog dialog;
    private boolean   local     = true;
    private boolean   cancelled = true;    
    private String    hostname  = Config.getDefaultHostIP();
    private String    port      = Config.getDefaultHostPort();

    public ConnectionDialog(JFrame frame)
    {
        dialog = new JDialog(new JFrame(), "Connection", true);

        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        dialog.setLayout(new GridBagLayout());

        dialog.add(new JLabel("Select a connection type:"), new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        final JLabel hostLabel = new JLabel("Host:");
        final JLabel portLabel = new JLabel("Port:");
        final JTextField hostField = new JTextField("localhost");
        final JTextField portField = new JTextField(port);

        ButtonGroup group = new ButtonGroup();

        final JRadioButton localRadio = new JRadioButton("Local");
        localRadio.setSelected(true);
        hostLabel.setEnabled(false);
        hostField.setEnabled(false);
        localRadio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(localRadio.isSelected())
                {
                    hostLabel.setEnabled(false);
                    hostField.setEnabled(false);
                }
            }
        });
        group.add(localRadio);
        dialog.add(localRadio, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        final JRadioButton remoteRadio = new JRadioButton("Remote");
        remoteRadio.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(remoteRadio.isSelected())
                {
                    hostLabel.setEnabled(true);
                    hostField.setEnabled(true);
                }
            }
        });
        group.add(remoteRadio);
        dialog.add(remoteRadio, new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        dialog.add(hostLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        dialog.add(hostField, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        dialog.add(portLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        dialog.add(portField, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        JButton connectButton = new JButton("Connect");
        dialog.getRootPane().setDefaultButton(connectButton);
        connectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cancelled = false;
                local = localRadio.isSelected();

                if(local)
                    hostname = "0.0.0.0";
                else
                    hostname = hostField.getText();

                if(!portField.getText().isEmpty())
                    port = portField.getText();

                dialog.dispose();
            }
        });
        dialog.add(connectButton, new GridBagConstraints(0, 5, 2, 1, 0, 0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        dialog.setSize(170, 220);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);
    }

    public void showDialog()
    {
        dialog.setVisible(true);
    }

    public boolean chooseLocal()
    {
        return local;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public String getHostname()
    {
        return hostname;
    }

    public String getPort()
    {
        return port;
    }  
}
