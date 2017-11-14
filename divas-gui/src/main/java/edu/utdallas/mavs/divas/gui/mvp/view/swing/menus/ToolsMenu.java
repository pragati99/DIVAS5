package edu.utdallas.mavs.divas.gui.mvp.view.swing.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//import edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers.ToolsMenuHandler;

public class ToolsMenu extends JMenu
{
    private static final long serialVersionUID = 1L;

    private JMenuItem         amsMenuItem;
    private JMenuItem         asMenuItem;

//    private ToolsMenuHandler  toolsMenuHandler;

    public ToolsMenu()
    {
        super("Tools");

        // create handlers
//        toolsMenuHandler = new ToolsMenuHandler();

        setMnemonic(KeyEvent.VK_T);

        amsMenuItem = new JMenuItem("Agent Mold Specification");
        amsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        add(amsMenuItem);
        amsMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                toolsMenuHandler.openMetaAgentSpecFrame();
            }
        });

        asMenuItem = new JMenuItem("Agent Specification");
        asMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        add(asMenuItem);
        asMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                toolsMenuHandler.openAgentSpecFrame();
            }
        });
    }
}
