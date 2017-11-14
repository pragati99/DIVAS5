package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.utdallas.mavs.divas.gui.guice.CommunicationModuleProvider;

public class ClientDetailsFrame
{
    private JFrame frame;

    public ClientDetailsFrame(Component parent)
    {
        if(!CommunicationModuleProvider.isConnected())
            return;

        frame = new JFrame("Connection Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.add(new JLabel("<html>Host: " + CommunicationModuleProvider.getHostName() + "<br>Port: " + CommunicationModuleProvider.getPort() + "</html>"));

        frame.setSize(300, 100);
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
    }
}
