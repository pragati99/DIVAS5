package edu.utdallas.mavs.divas.gui.services.process;

import java.io.IOException;

/**
 * Modified from eclipse platform version.
 */
public class StreamsProxy
{
    private OutputStreamMonitor outputMonitor;
    private OutputStreamMonitor errorMonitor;
    private InputStreamMonitor  inputMonitor;

    private boolean             closed;

    public StreamsProxy(Process process)
    {
        if(process == null)
            return;

        outputMonitor = new OutputStreamMonitor(process.getInputStream(), new IStreamListener()
        {
            @Override
            public void streamAppended(String text)
            {
                System.out.print(text);
            }
        });
        errorMonitor = new OutputStreamMonitor(process.getErrorStream(), new IStreamListener()
        {
            @Override
            public void streamAppended(String text)
            {
                System.err.print(text);
            }
        });
        inputMonitor = new InputStreamMonitor(process.getOutputStream());

        outputMonitor.startMonitoring();
        errorMonitor.startMonitoring();
        inputMonitor.startMonitoring();
    }

    /**
     * Causes the proxy to close all
     * communications between it and the
     * underlying streams after all remaining data
     * in the streams is read.
     */
    public void close()
    {
        if(!isClosed(true))
        {
            outputMonitor.close();
            errorMonitor.close();
            inputMonitor.close();
        }
    }

    /**
     * Returns whether the proxy is currently closed. This method
     * synchronizes access to the <code>fClosed</code> flag.
     * 
     * @param setClosed
     *        If <code>true</code> this method will also set the <code>fClosed</code> flag to true. Otherwise, the <code>fClosed</code> flag is not modified.
     * @return Returns whether the stream proxy was already closed.
     */
    private synchronized boolean isClosed(boolean setClosed)
    {
        boolean wasClosed = closed;
        if(setClosed)
        {
            closed = true;
        }
        return wasClosed;
    }

    /**
     * Causes the proxy to close all
     * communications between it and the
     * underlying streams immediately.
     * Data remaining in the streams is lost.
     */
    public void kill()
    {
        synchronized(this)
        {
            closed = true;
        }
        outputMonitor.kill();
        errorMonitor.kill();
        inputMonitor.close();
    }

    public void write(String input) throws IOException
    {
        if(!isClosed(false))
        {
            inputMonitor.write(input);
        }
        else
        {
            throw new IOException();
        }
    }

    public void closeInputStream() throws IOException
    {
        if(!isClosed(false))
        {
            inputMonitor.closeInputStream();
        }
        else
        {
            throw new IOException();
        }
    }
}
