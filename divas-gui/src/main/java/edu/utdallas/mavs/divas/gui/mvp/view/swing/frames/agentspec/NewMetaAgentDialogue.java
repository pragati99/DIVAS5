/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import divas.spec.agent.MetaAgentDocument.MetaAgent;

public class NewMetaAgentDialogue
{
    private boolean    cancelled = true;

    private JDialog    NewMetaAgentDialog;

    private JLabel     agentName = new JLabel("Name: ");
    private JTextField newAgentNameField;

    public void showDialog(final MetaAgent newMetaAgent)
    {
        newAgentNameField = new JTextField();
        NewMetaAgentDialog = new JDialog(MetaAgentSpecFrame.getFrame(), "Add Agent Mold", true);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cancelled = false;
                newMetaAgent.setName(newAgentNameField.getText());
                NewMetaAgentDialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                NewMetaAgentDialog.dispose();
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        labelPanel.add(agentName);

        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(newAgentNameField);
        NewMetaAgentDialog.add(labelPanel, BorderLayout.WEST);
        NewMetaAgentDialog.add(fieldPanel, BorderLayout.CENTER);
        NewMetaAgentDialog.add(bottomPanel, BorderLayout.SOUTH);

        NewMetaAgentDialog.setSize(250, 100);
        NewMetaAgentDialog.setLocationRelativeTo(MetaAgentSpecFrame.getFrame());
        NewMetaAgentDialog.setVisible(true);

    }

    public boolean isCancelled()
    {
        return cancelled;
    }

}
*/