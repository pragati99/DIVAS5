package edu.utdallas.mavs.divas.gui.services.process;

import java.io.IOException;
import java.util.Scanner;

public class ProcessLauncher
{
    private Process process;

    StreamsProxy    streamsProxy;

    private boolean terminated;

    private Thread  inputScannerThread;

    public void start(Class<?> main, int memory)
    {
        start(main, memory, "");
    }

    public void start(Class<?> mainClass, int memory, String... args)
    {
        start(mainClass.getName(), memory, args);
    }

    public void start(String mainClass, int memory, String... args)
    {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String libpath = System.getProperty("java.library.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        StringBuilder argStr = new StringBuilder();
        for(int i = 0; i < args.length; i++)
        {
            argStr.append(args[i] + " ");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, "-Xmx" + memory + "m", "-Djava.library.path=" + libpath, mainClass, argStr.toString());

        try
        {
            process = processBuilder.start();
            createAndAddShutdownHook(process);
            streamsProxy = new StreamsProxy(process);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        Thread processWaitThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    process.waitFor();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        }, "ProcessLaunchWaiter");
        processWaitThread.start();

        inputScannerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Scanner s = new Scanner(System.in);
                while(s.hasNext())
                {
                    try
                    {
                        streamsProxy.write(s.nextLine());
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                s.close();
            }
        }, "InputScanner");
        inputScannerThread.setDaemon(true);
        inputScannerThread.start();
    }

    private Process createAndAddShutdownHook(Process process)
    {
        Runtime.getRuntime().addShutdownHook(createShutdownHook(process));
        return process;
    }

    private Thread createShutdownHook(final Process process)
    {
        Thread shutdownHook = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                terminate();
            }
        });
        return shutdownHook;
    }

    public void terminate()
    {
        if(!terminated)
        {
            if(inputScannerThread != null && inputScannerThread.isAlive())
            {
                inputScannerThread.interrupt();
            }
            if(process != null)
            {
                process.destroy();
            }
        }
    }

    /**
     * Notification that the system process associated with this process
     * has terminated.
     */
    protected void finish()
    {
        streamsProxy.close();

        synchronized(this)
        {
            terminated = true;
            process = null;
        }
    }

    public void write(byte[] bytes)
    {
        try
        {
            streamsProxy.write(new String(bytes));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
