/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.SwingParent;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.VisConfigDialog;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers.VisualizerHandler;

public class VisualizerMenu extends JMenu
{
    private static final long serialVersionUID = 1L;

    private JLabel            statusLabel;

    private JMenuItem         start3DMenuItem;
    private JMenuItem         start2DMenuItem;
    private JMenuItem         optionsMenuItem;

    private VisualizerHandler visualizerHandler;

    public VisualizerMenu(final SwingParent<?> parent)
    {
        super("Visualizer");

        statusLabel = new JLabel();

        // create handlers
        visualizerHandler = new VisualizerHandler();

        setMnemonic(KeyEvent.VK_V);

        start3DMenuItem = new JMenuItem("Start 3D visualizer");
        start3DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, ActionEvent.CTRL_MASK));
        start3DMenuItem.setEnabled(true);
        add(start3DMenuItem);
        start3DMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                visualizerHandler.start3DVisualizer();
            }
        });       

        start2DMenuItem = new JMenuItem("Start 2D visualizer");
        start2DMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, ActionEvent.CTRL_MASK));
        start2DMenuItem.setEnabled(true);
        add(start2DMenuItem);
        start2DMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                visualizerHandler.start2DVisualizer();
            }
        });

        addSeparator();

        optionsMenuItem = new JMenuItem("Visualizer options");
        add(optionsMenuItem);
        optionsMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                visualizerHandler.visualizerOptions(parent.getFrame());
            }
        });
    }

    public JLabel getStatusLabel()
    {
        return statusLabel;
    }

    public void updateMenu(boolean connected)
    {
        if(connected)
        {
            start3DMenuItem.setEnabled(true);
            start2DMenuItem.setEnabled(true);
            optionsMenuItem.setEnabled(true);
        }
        else
        {
            start3DMenuItem.setEnabled(false);
            start2DMenuItem.setEnabled(false);
            optionsMenuItem.setEnabled(false);
        }
    }

    public void saveVisConfig()
    {
        VisConfigDialog.saveVisConfig();
    }

    public JButton getSaveVisConfigButton()
    {
        return VisConfigDialog.getSaveConfigurationButton();
    }
}
*/