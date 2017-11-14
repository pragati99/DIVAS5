/*package edu.utdallas.mavs.divas.gui.mvp.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javafx.scene.Parent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.menus.ConnectionMenu;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.menus.FileMenu;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.menus.ToolsMenu;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.menus.VisualizerMenu;

public class SwingParent<CP extends JPanel> extends Parent
{
    protected JFrame         frame;
    protected JPanel         contentContainer;
    protected CP             contentPanel;
    protected JPanel         statusBar;
    protected FileMenu       fileMenu;
    protected ConnectionMenu connectionMenu;
    protected ToolsMenu      toolsMenu;
    protected VisualizerMenu visualizerMenu;

    public SwingParent(CP contentPanel)
    {
        // initialize components
        this.initialize(contentPanel);

        // hide javafx node
        this.setVisible(false);

        // set the look and feel
        StyleHelper.setNimbusLookAndFeel();
    }

    protected void initialize(CP panel)
    {
        // Create and set up the window.
        frame = new JFrame(StyleHelper.TITLE);
        frame.setIconImage(Config.getFrameIcon());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // tear down
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                System.exit(0);
            }
        });

        // create the menu bar
        createMenuBar();

        // prepare the content panel
        contentContainer = new JPanel(new BorderLayout());
        frame.add(contentContainer, BorderLayout.CENTER);
        setContentPanel(null);

        // create the status bar
        createStatusBar();

        // Display the window.
        frame.setSize(StyleHelper.WIDTH, StyleHelper.HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // sets content panel
        setContentPanel(panel);
    }

    protected void setContentPanel(CP panel)
    {
        // remove the old content panel
        if(contentPanel != null)
        {
            contentContainer.remove(contentPanel);
        }

        // assign the new content panel
        contentPanel = panel;

        // set the new content panel
        if(contentPanel != null)
        {
            contentContainer.add(contentPanel, BorderLayout.CENTER);
        }

        // revalidate the component so the panel will be updated
        contentContainer.revalidate();

        // redraw the frame so the old panel components don't falsely remain
        // visible
        frame.repaint();
    }

    private void createMenuBar()
    {
        // create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // add file menu
        fileMenu = new FileMenu(this);
        menuBar.add(fileMenu);

        // add connection menu
        connectionMenu = new ConnectionMenu(this);
        menuBar.add(connectionMenu);

        // add tools menu
        toolsMenu = new ToolsMenu();
        menuBar.add(toolsMenu);

        // add visualizer menu
        visualizerMenu = new VisualizerMenu(this);
        menuBar.add(visualizerMenu);

        frame.setJMenuBar(menuBar);
    }

    private void createStatusBar()
    {
        statusBar = new JPanel();
        statusBar.setLayout(new GridLayout(1, 0));

        JLabel statusItem;

        statusItem = connectionMenu.getStatusLabel();
        statusItem.setBorder(new LineBorder(Color.GRAY));
        statusBar.add(statusItem);

        statusItem = fileMenu.getStatusLabel();
        statusItem.setBorder(new LineBorder(Color.GRAY));
        statusBar.add(statusItem);

        statusItem = new JLabel();
        statusItem.setBorder(new LineBorder(Color.GRAY));
        statusBar.add(statusItem);

        statusItem = visualizerMenu.getStatusLabel();
        statusItem.setBorder(new LineBorder(Color.GRAY));
        statusBar.add(statusItem);

        frame.add(statusBar, BorderLayout.SOUTH);
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public FileMenu getFileMenu()
    {
        return fileMenu;
    }

    public VisualizerMenu getVisualizerMenu()
    {
        return visualizerMenu;
    }    
}
*/