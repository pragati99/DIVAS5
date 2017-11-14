package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * ConsoleFrame redirects system out and error streams to a {@link javax.swing.JFrame JFrame}. Console messages
 * are organized into tabs based on the thread that generate the messages. ConsoleFrame
 * maintains a list of threads that have generated output and monitors them to determine
 * when they have stopped. Alternately, thread may invoke the {@link #threadEnding(Thread)} message
 * to proactively notify the ConsoleFrame of their completion.
 * <p>
 * When a thread completes, the ConsoleFrame puts the thread's name in (parenthesis) to indicate that the thread is no
 * longer active. A thread's tab can only be closed once it has completed.
 * <p>
 * ConsoleFrame provides buttons in each thread's tab to copy, save, or purge that thread's console.
 * <p>
 * ConsoleFrame is a singleton which can be accessed statically using the {@link #getInstance()} method.
 */
@SuppressWarnings("javadoc")
public class ConsoleFrame extends JFrame
{
    private static final long                serialVersionUID = 1L;

    /**
     * the singleton instance of ConsoleFrame
     */
    protected static ConsoleFrame            instance;

    /**
     * a {@link java.util.HashMap HashMap} maintaining the console area for each known thread.
     * The hash key is the name of the thread (via {@link Thread#getName()}).
     */
    protected HashMap<String, ThreadConsole> consoleMap;

    /**
     * the list of threads to be monitored by the daemon thread. Threads are added
     * to this list the first time they generate console messages. Threads are removed
     * from this list under two circumstances: if they have notified the ConsoleFrame via
     * the {@link #threadEnding(Thread)} method, or if the thread monitor has identified
     * that the thread has stopped.
     */
    protected List<Thread>                   threadList;

    /**
     * the ConsoleFrame's tab pane
     */
    protected JTabbedPane                    tabs;

    /**
     * the text style for System.out messages
     */
    protected AttributeSet                   outStyle;

    /**
     * the text style for System.err messages
     */
    protected AttributeSet                   errStyle;

    /**
     * the original System.out stream
     */
    protected PrintStream                    systemOut;

    /**
     * the original System.err stream
     */
    protected PrintStream                    systemErr;

    /**
     * Gets the singleton instance of ConsoleFrame. If the ConsoleFrame has not been created, this method will create it
     * and return
     * the newly-created ConsoleFrame. When the ConsoleFrame is constructed, it hijacks all system out and error
     * streams.
     * 
     * @return
     */
    public static ConsoleFrame getInstance()
    {
        if(instance == null)
            instance = new ConsoleFrame();

        return instance;
    }

    /**
     * Notifies the ConsoleFrame that the thread is ending. This method adds parenthesis
     * around the thread's name in the tab title and enables the thread's tab close
     * button. This method does not remove the thread from the monitored thread list
     * since the daemon thread will handle this task.
     * 
     * @param thread
     *        - the Thread that is ending
     */
    public void threadEnding(Thread thread)
    {
        ThreadConsole console = instance.consoleMap.get(thread.getName());
        if(console != null)
        {
            console.setCloseable(true);

            for(int i = 0; i < instance.tabs.getTabCount(); i++)
            {
                if(instance.tabs.getTitleAt(i).equals(thread.getName()))
                {
                    instance.tabs.setTitleAt(i, "(" + thread.getName() + ")");

                    // can't do this due to a java bug (http://bugs.sun.com/view_bug.do?bug_id=6670274)
                    // tabs.setTitleAt(i, "<html><strike>" + thread.getName() + "</strike></html>");
                }
            }
        }
    }

    protected ConsoleFrame()
    {
        super("Console");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        setSize(700, 300);
        setLocationRelativeTo(null);

        consoleMap = new HashMap<String, ThreadConsole>();
        threadList = Collections.synchronizedList(new ArrayList<Thread>());

        outStyle = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        errStyle = StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED);

        // save the system's original system output streams
        systemOut = System.out;
        systemErr = System.err;

        // System.setOut(new PrintStream(new ConsoleStream(outStyle), true));
        // System.setErr(new PrintStream(new ConsoleStream(errStyle), true));

        tabs = new JTabbedPane();
        add(tabs);

        // crate a daemon thread to monitor the known threads and remove the ones that have stopped
        Thread daemon = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        synchronized(threadList)
                        {
                            Iterator<Thread> iter = threadList.iterator();

                            while(iter.hasNext())
                            {
                                Thread thread = iter.next();

                                if(!thread.isAlive())
                                {
                                    threadEnding(thread);

                                    // remove this thread from the watched thread list
                                    iter.remove();
                                }
                            }
                        }
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }, "ThreadWatcher");
        daemon.setDaemon(true);
        daemon.start();
    }

    /**
     * an {@link java.io.OutputStream OutputStream} implementation that forwards messages to the ConsoleFrame via the
     * {@link ConsoleFrame#updateConsole(String, AttributeSet)} method.
     */
    protected class ConsoleStream extends OutputStream
    {
        private AttributeSet style;

        /**
         * @param style
         *        - the text style applied to messages on this stream
         */
        public ConsoleStream(AttributeSet style)
        {
            this.style = style;
        }

        @Override
        public void write(final int b) throws IOException
        {
            updateConsole(String.valueOf((char) b), style);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            updateConsole(new String(b, off, len), style);
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            write(b, 0, b.length);
        }
    }

    /**
     * A ThreadConsole is the console area dedicated to a specific thread. Each
     * TheadConsole contains a panel that is displayed in a dedicated tab in the
     * ConsoleFrame. ThreadConsoles are created when new threads generate messages
     * and can only be removed if the associated thread has stopped and the user
     * has clicked the "Close" button for the thread's tab.
     * 
     * @author Travis Steel
     */
    private class ThreadConsole
    {
        JTextPane console;
        JButton   closeButton;

        public ThreadConsole(final String title)
        {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            console = new JTextPane();
            console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            console.setEditable(false);
            JPanel noLineWrapPanel = new JPanel(new BorderLayout());
            noLineWrapPanel.add(console);
            panel.add(new JScrollPane(noLineWrapPanel), BorderLayout.CENTER);

            // add the button bar for the console tab

            JPanel buttonBar = new JPanel();
            buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.LINE_AXIS));

            JButton button;

            button = new JButton("Copy");
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        StringSelection ss = new StringSelection(console.getDocument().getText(0, console.getDocument().getLength()));
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
                    }
                    catch(BadLocationException e1)
                    {
                        e1.printStackTrace();
                    }

                }
            });
            buttonBar.add(button);

            button = new JButton("Save As...");
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JFileChooser fileChooser = new JFileChooser();
                    if(fileChooser.showSaveDialog(console) == JFileChooser.APPROVE_OPTION)
                    {
                        try
                        {
                            FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile());
                            fos.write(console.getDocument().getText(0, console.getDocument().getLength()).getBytes());
                            fos.close();
                        }
                        catch(BadLocationException e1)
                        {
                            e1.printStackTrace();
                        }
                        catch(IOException e2)
                        {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            buttonBar.add(button);

            button = new JButton("Purge");
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        console.getDocument().remove(0, console.getDocument().getLength());
                    }
                    catch(BadLocationException e1)
                    {
                        e1.printStackTrace();
                    }

                }
            });
            buttonBar.add(button);

            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    removeConsole(title);
                }
            });
            closeButton.setEnabled(false);
            buttonBar.add(closeButton);

            panel.add(buttonBar, BorderLayout.SOUTH);

            // add the tab to the tabbed pane
            tabs.addTab(title, panel);
        }

        /**
         * Updates the text pane contents for this thread's console output.
         * 
         * @param text
         *        - the text to be added to the text pane
         * @param style
         *        - the font style for the text to be added
         */
        public void updateConsole(final String text, final AttributeSet style)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    Document doc = console.getDocument();
                    try
                    {
                        doc.insertString(doc.getLength(), text, style);
                    }
                    catch(BadLocationException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        /**
         * enables or disables the close button that removes this thread's tab.
         * A thread's tab should not be closeable until it the thread has completed.
         * 
         * @param closeable
         */
        public void setCloseable(boolean closeable)
        {
            closeButton.setEnabled(closeable);
        }
    }

    /**
     * Writes <code>text</code> to the current thread's (determined by {@link Thread#currentThread()}) console area.
     * If the thread does not yet have an associated {@link #ThreadConsole}, this method creates one first.
     * 
     * @param text
     * @param style
     */
    protected void updateConsole(final String text, final AttributeSet style)
    {
        String threadName = Thread.currentThread().getName();
        ThreadConsole consoleArea = consoleMap.get(threadName);

        if(consoleArea == null)
        {
            // create a ThreadConsole for this thread
            consoleArea = new ThreadConsole(threadName);

            // add the ThreadConsole to the console hashmap
            consoleMap.put(threadName, consoleArea);

            // add the thread to the watched thread list
            synchronized(threadList)
            {
                threadList.add(Thread.currentThread());
            }
        }

        // send the text and style to the thread's console area
        consoleArea.updateConsole(text, style);
    }

    protected void removeConsole(String threadName)
    {
        // remove the console tab associated with this thread
        for(int i = 0; i < tabs.getTabCount(); i++)
        {
            if(tabs.getTitleAt(i).contains(threadName))
            {
                tabs.removeTabAt(i);
                break;
            }
        }

        // remove the console associated with this thread
        consoleMap.remove(threadName);
    }

    public void terminate()
    {
        // restore the system's original output streams
        System.setOut(systemOut);
        System.setErr(systemErr);
    }
}
