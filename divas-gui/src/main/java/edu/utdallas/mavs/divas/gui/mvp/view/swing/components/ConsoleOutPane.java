/*
 * File URL : $HeadURL: https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/gui/components/ConsoleOutPane.java $
 * Revision : $Rev: 345 $
 * Last modified at: $Date: 2010-05-07 13:26:19 -0500 (Fri, 07 May 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.gui.mvp.view.swing.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
public class ConsoleOutPane extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    JTextArea                 outTextArea;
    JTextArea                 errTextArea;

    public ConsoleOutPane()
    {
        super();
        setPreferredSize(new Dimension(300, 200));

        outTextArea = new JTextArea();
        outTextArea.setEditable(false);
        outTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        errTextArea = new JTextArea();
        errTextArea.setEditable(false);
        errTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        errTextArea.setForeground(Color.RED);

        addTab("out", new JScrollPane(outTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        addTab("err", new JScrollPane(errTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        redirectSystemStreams();
    }

    private void updateTextArea(final JTextArea textArea, final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                textArea.append(text);
            }
        });
    }

    private void redirectSystemStreams()
    {
        // System out output stream
        OutputStream out = new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                updateTextArea(outTextArea, String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                updateTextArea(outTextArea, new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException
            {
                write(b, 0, b.length);
            }
        };

        // System err output stream
        OutputStream err = new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                updateTextArea(errTextArea, String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                updateTextArea(errTextArea, new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException
            {
                write(b, 0, b.length);
            }
        };

        // Redirect system out to out
        System.setOut(new PrintStream(out, true));
        // Redirect system err to err
        System.setErr(new PrintStream(err, true));
    }
}
