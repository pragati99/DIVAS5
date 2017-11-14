package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Inputbox
{

    JLabel     label;
    JTextField input;
    JLabel     unit;
    String     name;

    public Inputbox(String name, String defaultvalue, String unitname, JPanel parent)
    {
        label = new JLabel(name + ": ");
        input = new JTextField(defaultvalue);
        unit = new JLabel("  " + unitname);
        this.name = name;
        parent.add(label);
        parent.add(input);
        parent.add(unit);

    }

}
