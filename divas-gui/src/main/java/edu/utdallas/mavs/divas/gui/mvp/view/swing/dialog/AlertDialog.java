package edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AlertDialog
{
    JDialog alertDialog;

    public AlertDialog(JFrame owner, String message)
    {
        JDialog alertDialog = new JDialog(new JFrame(), "Attention!", true);
        JLabel msg = new JLabel(message);
        alertDialog.add(msg);
        alertDialog.setLocationRelativeTo(owner);
        alertDialog.setSize(300, 100);
        alertDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void showDialog()
    {
        alertDialog.setVisible(true);
    }

}
