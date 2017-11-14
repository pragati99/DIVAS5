/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import divas.spec.agent.DivasAttribute;
import divas.spec.agent.MetaAgentDocument.MetaAgent;

public class NamesDialog
{
    private boolean      cancelled = true;
    private JDialog      nameDialog;

    private JTextField   nameField;
    private JRadioButton physicalCategoryButton;
    private JRadioButton mentalCategoryButton;

    // private JComboBox typeField;
    // private JTextField unitField;
    // private JTextField conMinField;
    // private JTextField conMaxField;

    public void showDialog(final MetaAgent ma)
    {
        nameDialog = new JDialog(MetaAgentSpecFrame.getFrame(), "Add Attribute to Agent Mold", true);
        nameDialog.setLayout(new BorderLayout());

        nameField = new JTextField();

        physicalCategoryButton = new JRadioButton("Physical", true);
        mentalCategoryButton = new JRadioButton("Mental");
        ButtonGroup categoryGroup = new ButtonGroup();
        categoryGroup.add(physicalCategoryButton);
        categoryGroup.add(mentalCategoryButton);
        JPanel categoryPanel = new JPanel(new GridLayout(1, 0));
        categoryPanel.add(physicalCategoryButton);
        categoryPanel.add(mentalCategoryButton);

        final JPanel typePanel = new JPanel(new CardLayout());

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DivasAttribute attribute = DivasAttribute.Factory.newInstance();

                attribute.setName(nameField.getText());

                cancelled = false;
                nameDialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                nameDialog.dispose();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(typePanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        nameDialog.add(bottomPanel, BorderLayout.SOUTH);

        nameDialog.setSize(250, 100);
        nameDialog.setLocationRelativeTo(MetaAgentSpecFrame.getFrame());
        nameDialog.setVisible(true);
    }

    public boolean isCancelled()
    {
        return cancelled;
    }
}
*/