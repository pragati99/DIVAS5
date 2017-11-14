package edu.utdallas.mavs.divas.visualization.vis2D.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * This class contains information about the agents tracking panel.
 * <p>
 * This class initializes the agents tracking panel and the <code>List</code> used
 * to store agents that is being tracked. The tracked agents will be shown distinguish
 * from other agents in the 2D-Visualizer.
 */
public class AgentTrackingPanel extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	private Vector<Integer>		agentTrackList		= new Vector<Integer>();

	/**
	 * Constructs the tracking agents panel.
	 * <p>
	 * Adding the fields and buttons in the agents tracking panel and prepare the <code>List<code> used to store information about tracked agents.
	 */
	public AgentTrackingPanel()
	{
		setLayout(new GridLayout(2, 2));

		final JTextField agentIDText = new JTextField();
		JButton findAgentButton = new JButton("Add to track List");

		add(agentIDText);
		

		add(findAgentButton);

		final JList<Integer> agentList = new JList<Integer>();
		agentList.setVisibleRowCount(5);
		agentList.setFixedCellWidth(100);
		agentList.setFixedCellHeight(15);
		agentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		add(agentList);
		add(new JScrollPane(agentList));

		agentIDText.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(ActionEvent event)
			{
				if(!agentTrackList.contains(Integer.parseInt(agentIDText.getText())))
				{
					agentTrackList.add(Integer.parseInt(agentIDText.getText()));
				}
				agentIDText.setText("");
				agentList.setListData(agentTrackList);
			}

		}

		);
		
		findAgentButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(ActionEvent event)
			{
				if(!agentTrackList.contains(Integer.parseInt(agentIDText.getText())))
				{
					agentTrackList.add(Integer.parseInt(agentIDText.getText()));
				}
				agentIDText.setText("");
				agentList.setListData(agentTrackList);
			}

		}

		);

		JButton removeAgentButton = new JButton("Remove from track List");

		removeAgentButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(ActionEvent event)
			{
				for(Integer aId : agentList.getSelectedValuesList())
					agentTrackList.remove(aId);

				agentList.setListData(agentTrackList);
			}

		}

		);
		add(removeAgentButton);
	}

	/**
	 * Determines if the agent with given ID is in the track list
	 * 
	 * @param agentId
	 *        The agent ID to be tested if its tracked
	 * @return A boolean flag that indicates if the agent is tracked
	 */
	public boolean agentIsTracked(Integer agentId)
	{
		if(agentTrackList.contains(agentId))
			return true;
		return false;
	}

}
