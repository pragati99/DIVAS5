package edu.utdallas.mavs.divas.gui.services.process;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Modified from eclipse platform version.
 * Writes to the input stream of a system process,
 * queueing output if the stream is blocked.
 * The input stream monitor writes to system in via
 * an output stream.
 */
public class InputStreamMonitor
{

    /**
     * The stream which is being written to (connected to system in).
     */
    private OutputStream   stream;

    private PrintWriter    pw;

    /**
     * The queue of output.
     */
    private Vector<String> queue;

    /**
     * The thread which writes to the stream.
     */
    private Thread         thread;

    /**
     * A lock for ensuring that writes to the queue are contiguous
     */
    private Object         lock;

    /**
     * Whether the underlying output stream has been closed
     */
    private boolean        closed = false;

    /**
     * Creates an input stream monitor which writes to system in via the given output stream.
     * 
     * @param stream
     *        output stream
     */
    public InputStreamMonitor(OutputStream stream)
    {
        this.stream = stream;
        pw = new PrintWriter(new OutputStreamWriter(stream), true);

        queue = new Vector<String>();
        lock = new Object();
    }

    /**
     * Appends the given text to the stream, or
     * queues the text to be written at a later time
     * if the stream is blocked.
     * 
     * @param text
     *        text to append
     */
    public void write(String text)
    {
        synchronized(lock)
        {
            queue.add(text);
            lock.notifyAll();
        }
    }

    /**
     * Starts a thread which writes the stream.
     */
    public void startMonitoring()
    {
        if(thread == null)
        {
            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    write();
                }
            }, "InputStreamMonitor");
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Close all communications between this
     * monitor and the underlying stream.
     */
    public void close()
    {
        if(thread != null)
        {
            Thread tempThread = thread;
            thread = null;
            tempThread.interrupt();
        }
    }

    /**
     * Continuously writes to the stream.
     */
    protected void write()
    {
        while(thread != null)
        {
            writeNext();
        }
        if(!closed)
        {
            pw.close();
            try
            {
                stream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the text in the queue to the stream.
     */
    protected void writeNext()
    {
        while(!queue.isEmpty() && !closed)
        {
            String text = queue.firstElement();
            queue.removeElementAt(0);
            pw.println(text);
            pw.flush();
        }
        try
        {
            synchronized(lock)
            {
                lock.wait();
            }
        }
        catch(InterruptedException e)
        {
        }
    }

    /**
     * Closes the output stream attached to the standard input stream of this
     * monitor's process.
     * 
     * @exception IOException
     *            if an exception occurs closing the input stream
     */
    public void closeInputStream() throws IOException
    {
        if(!closed)
        {
            closed = true;
            pw.close();
            stream.close();
        }
        else
        {
            throw new IOException();
        }
    }
}
