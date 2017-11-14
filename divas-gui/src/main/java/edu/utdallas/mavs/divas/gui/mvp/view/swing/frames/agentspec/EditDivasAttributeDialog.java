/*package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.xmlbeans.XmlOptions;

import divas.spec.agent.DivasAttribute;
import divas.spec.agent.DivasAttribute.Category;
import divas.spec.agent.DivasAttribute.Constraint;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec.MetaAgentSpecFrame.DivasAttributeType;

public class EditDivasAttributeDialog
{
    private boolean        cancelled = true;
    private JDialog        DivasAttributeDialog;

    private JTextField     defaultColor;
    // private JComboBox defaultCombo;
    private JRadioButton   defaultRadio;

    private JTextComponent defaults[];

    private JLabel         unitLabel;
    private JLabel         conMinLabel;
    private JLabel         conMaxLabel;
    private JLabel         conPackageLabel;

    private JTextField     nameField;
    private JRadioButton   physicalCategoryButton;
    private JRadioButton   mentalCategoryButton;
    private JComboBox<?>      typeField;
    private JTextField     unitField;
    private JTextField     conMinField;
    private JTextField     conMaxField;
    private JTextField     conPackageField;

    JTextField             defaultX;
    JTextField             defaultY;
    JTextField             defaultZ;

    public void showDialog(final DivasAttribute a)
    {
        DivasAttributeDialog = new JDialog(MetaAgentSpecFrame.getFrame(), "Edit Attribute", true);
        DivasAttributeDialog.setLayout(new BorderLayout());

        defaults = new JTextComponent[DivasAttributeType.values().length];

        nameField = new JTextField(a.getName());

        physicalCategoryButton = new JRadioButton("Physical");
        mentalCategoryButton = new JRadioButton("Mental");

        if(a.getCategory().intValue() == 1)
        {
            physicalCategoryButton.setSelected(true);
        }
        else
        {
            mentalCategoryButton.setSelected(true);
        }

        ButtonGroup categoryGroup = new ButtonGroup();
        categoryGroup.add(physicalCategoryButton);
        categoryGroup.add(mentalCategoryButton);
        JPanel categoryPanel = new JPanel(new GridLayout(1, 0));
        categoryPanel.add(physicalCategoryButton);
        categoryPanel.add(mentalCategoryButton);

        final JPanel typePanel = new JPanel(new CardLayout());

        typeField = new JComboBox<Object>(DivasAttributeType.values());
        typeField.setSelectedItem(DivasAttributeType.valueOf(a.getType()));
        typeField.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent evt)
            {
                CardLayout cl = (CardLayout) (typePanel.getLayout());
                cl.show(typePanel, evt.getItem().toString());

                unitField.setText("");
                unitField.setEnabled(false);
                unitLabel.setEnabled(false);
                conMinField.setEnabled(false);
                conMinLabel.setEnabled(false);
                conMaxField.setEnabled(false);
                conMaxLabel.setEnabled(false);
                conPackageLabel.setEnabled(false);
                conPackageField.setEnabled(false);

                switch((DivasAttributeType) evt.getItem())
                {
                case Number:
                case DecimalNumber:
                    unitField.setEnabled(true);
                    unitLabel.setEnabled(true);
                    conMinField.setEnabled(true);
                    conMinLabel.setEnabled(true);
                    conMaxField.setEnabled(true);
                    conMaxLabel.setEnabled(true);
                    break;
                case Percentage:
                    unitField.setText("%");
                    conMinField.setEnabled(true);
                    conMinLabel.setEnabled(true);
                    conMaxField.setEnabled(true);
                    conMaxLabel.setEnabled(true);
                    break;
                default:
                    break;
                }
            }
        });

        unitLabel = new JLabel("Unit: ");
        conMinLabel = new JLabel("Min: ");
        conMaxLabel = new JLabel("Max: ");
        conPackageLabel = new JLabel("Package: ");

        unitField = new JTextField(a.getUnit());
        conMinField = new JTextField();
        conMaxField = new JTextField();
        conPackageField = new JTextField();

        if(a.getConstraintArray().length == 2)
        {
            conMinField.setText(a.getConstraintArray(0).getValue());
            conMaxField.setText(a.getConstraintArray(1).getValue());
        }

        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        labelPanel.add(new JLabel("Name: "));
        labelPanel.add(new JLabel("Category: "));
        labelPanel.add(new JLabel("Type: "));
        labelPanel.add(unitLabel);
        labelPanel.add(conMinLabel);
        labelPanel.add(conMaxLabel);
        labelPanel.add(conPackageLabel);

        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(nameField);
        fieldPanel.add(categoryPanel);
        fieldPanel.add(typeField);
        fieldPanel.add(unitField);
        fieldPanel.add(conMinField);
        fieldPanel.add(conMaxField);
        fieldPanel.add(conPackageField);

        conPackageLabel.setEnabled(false);
        conPackageField.setEnabled(false);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        for(DivasAttributeType aType : DivasAttributeType.values())
        {
            JPanel card = getTypePanel(aType, a);
            typePanel.add(card, aType.toString());
        }

        CardLayout cl = (CardLayout) (typePanel.getLayout());
        cl.show(typePanel, a.getType());

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                // DivasAttribute DivasAttribute = ma.addNewDivasAttribute();

                DivasAttribute da = DivasAttribute.Factory.newInstance();

                da.setName(nameField.getText());
                da.setCategory(physicalCategoryButton.isSelected() ? Category.PHYSICAL : Category.MENTAL);
                da.setType(typeField.getSelectedItem().toString());

                DivasAttributeType type = DivasAttributeType.valueOf(typeField.getSelectedItem().toString());

                Constraint con;
                String defaultValue = "";
                switch(type)
                {
                case Vector:
                    da.setDefaultx(defaultX.getText());
                    da.setDefaulty(defaultY.getText());
                    da.setDefaultz(defaultZ.getText());
                    da.setDefault(defaultValue);
                    break;
                case EnvObject:
                case Color:
                    defaultValue = defaultColor.getText();
                case Words:
                    defaultValue = defaults[type.ordinal()].getText();
                    da.setDefault(defaultValue);
                    break;
                case TrueFalse:
                    defaultValue = String.valueOf(defaultRadio.isSelected());
                    da.setDefault(defaultValue);
                    break;
                default:
                    defaultValue = defaults[type.ordinal()].getText();
                    da.setUnit(unitField.getText());
                    con = da.addNewConstraint();
                    con.setName("min");
                    con.setValue(conMinField.getText());
                    con = da.addNewConstraint();
                    con.setName("max");
                    con.setValue(conMaxField.getText());
                    da.setDefault(defaultValue);
                    break;
                }

                cancelled = false;

                String attrName = da.getName();
                // ma.setName(maName.getText());
                // ma.setParent(maParentName.getText());

                File savefile = new File(MetaAgentSpecFrame.getAttrFolder() + "\\" + attrName + ".attribute");

                try
                {
                    da.save(savefile, new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4));
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                }
                da.xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(4));

                // Refill Attri List HERE

                DivasAttributeDialog.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DivasAttributeDialog.dispose();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(typePanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        DivasAttributeDialog.add(labelPanel, BorderLayout.WEST);
        DivasAttributeDialog.add(fieldPanel, BorderLayout.CENTER);
        DivasAttributeDialog.add(bottomPanel, BorderLayout.SOUTH);

        DivasAttributeDialog.setSize(250, 300);
        DivasAttributeDialog.setLocationRelativeTo(MetaAgentSpecFrame.getFrame());
        DivasAttributeDialog.setVisible(true);
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    private JPanel getTypePanel(DivasAttributeType type, DivasAttribute a)
    {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));

        switch(type)
        {
        case Vector:
            defaultX = new JTextField(a.getDefaultx());
            defaultY = new JTextField(a.getDefaulty());
            defaultZ = new JTextField(a.getDefaultz());

            labelPanel.add(new JLabel("Default x: "));
            fieldPanel.add(defaultX);
            labelPanel.add(new JLabel("Default y: "));
            fieldPanel.add(defaultY);
            labelPanel.add(new JLabel("Default z: "));
            fieldPanel.add(defaultZ);

            break;
        case Color:
            labelPanel.add(new JLabel("Default: "));
            defaultColor = new JTextField("Black");
            defaultColor.setText(a.getDefault());
            fieldPanel.add(defaultColor);
            break;

        case TrueFalse:
            labelPanel.add(new JLabel("Default: "));
            defaultRadio = new JRadioButton("True");
            JRadioButton otherButton = new JRadioButton("False");
            ButtonGroup group = new ButtonGroup();
            group.add(defaultRadio);
            group.add(otherButton);
            JPanel radioPanel = new JPanel(new GridLayout(1, 0));
            radioPanel.add(defaultRadio);
            radioPanel.add(otherButton);
            fieldPanel.add(radioPanel);

            if(a.getDefault() != null)
            {
                if(a.getDefault().equals("true"))
                {
                    defaultRadio.setSelected(true);
                }
                else
                {
                    otherButton.setSelected(true);
                }
            }

            break;
        case EnvObject:
        default:
            labelPanel.add(new JLabel("Default: "));
            defaults[type.ordinal()] = new JTextField("");
            defaults[type.ordinal()].setText(a.getDefault());
            fieldPanel.add(defaults[type.ordinal()]);
            break;
        }

        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(fieldPanel, BorderLayout.CENTER);

        return panel;
    }
}
*/