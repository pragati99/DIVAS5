/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.SwingParent;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers.FileMenuHandler;

public class FileMenu extends JMenu
{
    private static final long serialVersionUID = 1L;

    private JLabel            statusLabel;

//    private JMenuItem         newEnvMenuItem;
    private JMenuItem         loadEnvMenuItem;
    private JMenuItem         saveEnvMenuItem;
    private JMenuItem         saveSimulationMenuItem;
    private JMenuItem         loadSimulationMenuItem;
    private JMenuItem         exitMenuItem;

    private FileMenuHandler   fileMenuHandler;

    public FileMenu(final SwingParent<?> parent)
    {
        super("File");

        statusLabel = new JLabel();

        fileMenuHandler = new FileMenuHandler();

        setMnemonic(KeyEvent.VK_F);

        // // New Environment
        // newEnvMenuItem = new JMenuItem("New environment...");
        // newEnvMenuItem.setMnemonic(KeyEvent.VK_N);
        // add(newEnvMenuItem);
        //
        // addSeparator();
        
        // Save Environment
        saveEnvMenuItem = new JMenuItem("Save environment...");
        saveEnvMenuItem.setMnemonic(KeyEvent.VK_W);
        add(saveEnvMenuItem);
        
        // Load Environment
        loadEnvMenuItem = new JMenuItem("Load environment...");
        loadEnvMenuItem.setMnemonic(KeyEvent.VK_O);
        add(loadEnvMenuItem);
        
        addSeparator();
            
        // Save Simulation
        saveSimulationMenuItem = new JMenuItem("Save Simulation...");
        saveSimulationMenuItem.setMnemonic(KeyEvent.VK_S);
        add(saveSimulationMenuItem);

        // Load Simulation
        loadSimulationMenuItem = new JMenuItem("Load Simulation");
        loadSimulationMenuItem.setMnemonic(KeyEvent.VK_L);
        add(loadSimulationMenuItem);

        addSeparator();

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileMenuHandler.exit();
            }
        });
    }

    public FileMenuHandler getFileMenuHandler()
    {
        return fileMenuHandler;
    }

    public JLabel getStatusLabel()
    {
        return statusLabel;
    }

    // public JMenuItem getNewEnvironmentMenuItem()
    // {
    // return newEnvMenuItem;
    // }
    
    public JMenuItem getLoadEnvironmentMenuItem()
    {
        return loadEnvMenuItem;
    }
    
    public JMenuItem getSaveEnvironmentMenuItem()
    {
        return saveEnvMenuItem;
    }

    public JMenuItem getSaveSimulationMenuItem()
    {
        return saveSimulationMenuItem;
    }

    public JMenuItem getLoadSimulationMenuItem()
    {
        return loadSimulationMenuItem;
    }
}
*/