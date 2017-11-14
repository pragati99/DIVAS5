package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.utils.EnvObjectLoader;
import edu.utdallas.mavs.divas.visualization.vis2D.panels.HeaderPanel.SimulationMode;

/**
 * This class contains information about the adding environment objects panel in the 2D visualizer tool box.
 * <p>
 * This class is responsible for initializing the adding environment object panel and populate the lists by reading the envobjects.xml file that stores all the information needed to create the
 * environment objects in the simulation.
 */
public class AddEnvObjPanel extends JPanel
{
    private static final long    serialVersionUID = 1L;

    private JCheckBox            showEnvObjImage;
    private List<EnvObjectState> envObjStateList;    
    private EnvObjectState       candidateEnvObj;
    private Vector<JButton>      lab              = new Vector<JButton>();
    
    /**
     * Constructs the adding environment object panel.
     * 
     * @param toolBox
     *        The main toolBox for the 2D visualizer.
     */
    public AddEnvObjPanel(final ToolBox toolBox)
    {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout());

        final JPanel details = new JPanel();
        details.setLayout(new GridLayout(5, 3));

        List<String> envObjTypeNames = EnvObjectLoader.getObjTypeNames();
        
        final JComboBox<String> envObjType = new JComboBox<String>(envObjTypeNames.toArray(new String[envObjTypeNames.size()]));

        showEnvObjImage = new JCheckBox("Show Environment object image");
        header.add(envObjType);
        header.add(showEnvObjImage);
        add(header);
        add(details);
        envObjType.addItemListener(

        new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent event)
            {
                updatePanel(envObjType.getSelectedIndex());

                for(int i = 0; i < lab.size(); i++)
                {
                    details.remove(lab.elementAt(i));
                }

                lab.clear();

                for(int i = 0; i < envObjStateList.size(); i++)
                {
                    ImageIcon icon = new ImageIcon(Config.getIcon(envObjStateList.get(i).getImage()));
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(65, 65, java.awt.Image.SCALE_SMOOTH);
                    ImageIcon newIcon = new ImageIcon(newimg);
                    lab.add(i, new JButton(envObjStateList.get(i).getName(), newIcon));
                    lab.get(i).setVerticalAlignment(JButton.BOTTOM);
                    lab.get(i).setMaximumSize(new java.awt.Dimension(65, 65));
                    lab.get(i).setMinimumSize(new java.awt.Dimension(65, 65));
                    lab.get(i).setPreferredSize(new java.awt.Dimension(65, 65));
                    lab.get(i).addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            toolBox.setSimMode(SimulationMode.ADD_ENV_OBJECT);
                            toolBox.setSimModeLabel("Current Simulation Mode: " + toolBox.getSimMode().toString());
                            candidateEnvObj = getEnvObjectState(((JButton) e.getSource()).getText());
                        }
                    });

                    details.add(lab.elementAt(i));
                }
                details.revalidate();

            }
        });
    }

    /**
     * Update panel when drop down selection changes
     * 
     * @param selectionId
     *        The type of environment object
     */
    private void updatePanel(int selectionId)
    {
        // Retrieve a list of EnvObjState from the selected env obj type
        envObjStateList = EnvObjectLoader.getEnvObjectStates(selectionId);
    }


    /**
     * Gets the show environment object as image flag
     * 
     * @return Boolean flag that indicates if the environment object should displayed as an image
     *         or as a rectangular shape.
     */
    public boolean getShowEnvObjImage()
    {
        return showEnvObjImage.isSelected();
    }

    /**
     * Gets the created environment object state for the selected object in the panel in order to be
     * sent to the simulation for creation.
     * 
     * @return Environment object state for the selected object in the panel.
     */
    public EnvObjectState getCandidateEnvObj()
    {
        return candidateEnvObj;
    }

    /**
     * Gets an Environment object state given an environment object title
     * 
     * @param envObjectTitle
     *        Environment object title
     * @return Environment object state for the given title.
     */
    public EnvObjectState getEnvObjectState(String envObjectTitle)
    {
        for(EnvObjectState eo : envObjStateList)
        {
            if(eo.getName() == envObjectTitle)
            {
                return eo;
            }
        }
        return null;
    }

}
