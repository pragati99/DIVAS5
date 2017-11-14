/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.SwingParent;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers.ConnectionHandler;

public class ConnectionMenu extends JMenu
{
    private static final long serialVersionUID = 1L;

    private JLabel            statusLabel;

    private JMenuItem         connectMenuItem;
    private JMenuItem         disconnectMenuItem;
    private JMenuItem         conDetailsMenuItem;

    private ConnectionHandler connectionHandler;

    public ConnectionMenu(final SwingParent<?> parent)
    {
        super("Connection");

        statusLabel = new JLabel();

        // create handlers
        connectionHandler = new ConnectionHandler();

        setMnemonic(KeyEvent.VK_V);

        connectMenuItem = new JMenuItem("Connect...");
        add(connectMenuItem);

        disconnectMenuItem = new JMenuItem("Disconnect");
        add(disconnectMenuItem);
        disconnectMenuItem.setEnabled(false);

        addSeparator();

        conDetailsMenuItem = new JMenuItem("Connection details");
        add(conDetailsMenuItem);
        conDetailsMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                connectionHandler.connectionDetails(parent.getFrame());
            }
        });
    }

    public JLabel getStatusLabel()
    {
        return statusLabel;
    }

    public ConnectionHandler getConnectionHandler()
    {
        return connectionHandler;
    }

    public JMenuItem getConnectMenuItem()
    {
        return connectMenuItem;
    }

    public JMenuItem getDisconnectMenuItem()
    {
        return disconnectMenuItem;
    }

    public void updateMenu(boolean connected)
    {
        if(connected)
        {
            connectMenuItem.setEnabled(false);
            disconnectMenuItem.setEnabled(true);
            conDetailsMenuItem.setEnabled(true);
        }
        else
        {
            connectMenuItem.setEnabled(true);
            disconnectMenuItem.setEnabled(false);
            conDetailsMenuItem.setEnabled(false);
        }
    }
}
*/