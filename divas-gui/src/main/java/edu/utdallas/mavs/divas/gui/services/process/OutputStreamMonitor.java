package edu.utdallas.mavs.divas.gui.services.process;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Modified from eclipse platform version.
 * Monitors the output stream of a system process and notifies listeners of additions to the stream.
 * The output stream monitor reads system out (or err) via and input stream.
 */
public class OutputStreamMonitor
{
    /**
     * The stream being monitored (connected system out or err).
     */
    private InputStream      stream;

    /**
     * A collection of listeners
     */
    private IStreamListener  listener;

    /**
     * The local copy of the stream contents
     */
    private StringBuffer     contents;

    /**
     * The thread which reads from the stream
     */
    private Thread           thread;

    /**
     * The size of the read buffer
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Whether or not this monitor has been killed.
     * When the monitor is killed, it stops reading
     * from the stream immediately.
     */
    private boolean          killed      = false;

    private long             lastSleep;

    /**
     * Creates an output stream monitor on the
     * given stream (connected to system out or err).
     * 
     * @param stream
     *        input stream to read from
     * @param listener
     * @param encoding
     *        stream encoding or <code>null</code> for system default
     */
    public OutputStreamMonitor(InputStream stream, IStreamListener listener)
    {
        this.listener = listener;
        this.stream = new BufferedInputStream(stream, 8192);
        contents = new StringBuffer();
    }

    /**
     * Causes the monitor to close all
     * communications between it and the
     * underlying stream by waiting for the thread to terminate.
     */
    protected void close()
    {
        if(thread != null)
        {
            Thread tempThread = thread;
            thread = null;
            try
            {
                tempThread.join();
            }
            catch(InterruptedException ie)
            {
            }
            listener = null;
        }
    }

    /**
     * Notifies the listeners that text has
     * been appended to the stream.
     */
    private void fireStreamAppended(String text)
    {
        listener.streamAppended(text);
    }

    /**
     * Continually reads from the stream.
     * <p>
     * This method, along with the <code>startReading</code> method is used to allow <code>OutputStreamMonitor</code> to
     * implement <code>Runnable</code> without publicly exposing a <code>run</code> method.
     */
    private void read()
    {
        lastSleep = System.currentTimeMillis();
        long currentTime = lastSleep;
        byte[] bytes = new byte[BUFFER_SIZE];
        int read = 0;
        while(read >= 0)
        {
            try
            {
                if(killed)
                {
                    break;
                }
                read = stream.read(bytes);
                if(read > 0)
                {
                    String text = new String(bytes, 0, read);
                    synchronized(this)
                    {
                        contents.append(text);
                        fireStreamAppended(text);
                    }
                }
            }
            catch(IOException ioe)
            {
                if(!killed)
                {
                    ioe.printStackTrace();
                }
                return;
            }
            catch(NullPointerException e)
            {
                // killing the stream monitor while reading can cause an NPE
                // when reading from the stream
                if(!killed && thread != null)
                {
                    e.printStackTrace();
                }
                return;
            }

            currentTime = System.currentTimeMillis();
            if(currentTime - lastSleep > 1000)
            {
                lastSleep = currentTime;
                try
                {
                    Thread.sleep(1); // just give up CPU to maintain UI responsiveness.
                }
                catch(InterruptedException e)
                {
                }
            }
        }
        try
        {
            stream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void kill()
    {
        killed = true;
    }

    /**
     * Starts a thread which reads from the stream
     */
    protected void startMonitoring()
    {
        if(thread == null)
        {
            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    read();
                }
            }, "OutputStreamMonitor");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }
}
