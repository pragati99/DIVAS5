package edu.utdallas.mavs.divas.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a helper class that wraps the Java concurrency API to provide a
 * standard mechanism for developers to handle concurrent programming.
 */
public class Multithreader
{
    private final static Logger logger       = LoggerFactory.getLogger(Multithreader.class);

    private String              name         = "";
    private ExecutorService     executor;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Enumeration for the number of threads used in the multithreader
     */
    public enum ThreadPoolType
    {
        /**
         * FIXED means that the number of threads is used in the multithreader.
         * This number is assigned at the beginning and will never change after
         * that.
         */
        FIXED,
        /**
         * CACHED means that the number of threads is increased at constant
         * number each time the multithreader needs more threads.
         */
        CACHED,
        /**
         * SINGLE means that there is only one single thread for the
         * multithreader.
         */
        SINGLE
    }

    // Create a factory that produces daemon threads with a naming pattern and a
    // priority
    ThreadFactory daemonFactory  = new ThreadFactory()
                                 {
                                     @Override
                                     public Thread newThread(Runnable r)
                                     {
                                         Thread thread = Executors.defaultThreadFactory().newThread(r);
                                         thread.setName(name + "-daemon-" + threadNumber.getAndIncrement());
                                         thread.setDaemon(true);
                                         return thread;
                                     }
                                 };

    // Create a factory that produces custom priority threads with a naming
    // pattern and a priority
    ThreadFactory defaultFactory = new ThreadFactory()
                                 {
                                     @Override
                                     public Thread newThread(Runnable r)
                                     {
                                         Thread thread = Executors.defaultThreadFactory().newThread(r);
                                         thread.setName(name + "-thread-" + threadNumber.getAndIncrement());
                                         return thread;
                                     }
                                 };

    /**
     * Constructs a multithreader with a given name and thread pool type.
     * 
     * @param name
     *        Multithreader name
     * @param poolType
     *        Thread pool type which could be FIXED, CACHED, SINGLE
     */
    public Multithreader(final String name, ThreadPoolType poolType)
    {
        this(name, poolType, 10, false);
    }

    /**
     * Constructs a multithreader with a given name and indicates if it's a
     * daemon thread.
     * 
     * @param name
     *        Multithreader name
     * @param daemon
     *        boolean flag that indicates if the multithreader is daemon.
     */
    public Multithreader(final String name, boolean daemon)
    {
        this(name, ThreadPoolType.CACHED, 50, daemon);
    }

    /**
     * Constructs a multithreader with a given name, thread pool type and
     * indicates if it's a daemon thread.
     * 
     * @param name
     *        Multithreader name
     * @param poolType
     *        Thread pool type which could be FIXED, CACHED, SINGLE
     * @param daemon
     *        boolean flag that indicates if the multithreader is daemon.
     */
    public Multithreader(final String name, ThreadPoolType poolType, boolean daemon)
    {
        this(name, poolType, 50, daemon);
    }

    /**
     * Constructs a multithreader with a given name.
     * 
     * @param name
     *        Multithreader name
     */
    public Multithreader(final String name)
    {
        this.name = name;
        logger.debug("Creating Multithreader instance: " + name);
        executor = Executors.newCachedThreadPool(defaultFactory);
    }

    /**
     * Constructs a multithreader with a given name, thread pool type, thread
     * initial count and indicates if it's a daemon thread.
     * 
     * @param name
     *        Multithreader name.
     * @param poolType
     *        Thread pool type which could be FIXED, CACHED, SINGLE.
     * @param threadCount
     *        thread initial count.
     * @param daemon
     *        boolean flag that indicates if the multithreader is daemon.
     */
    public Multithreader(final String name, ThreadPoolType poolType, int threadCount, boolean daemon)
    {
        this.name = name;
        logger.debug("Creating Multithreader instance: " + name);

        if(poolType.equals(ThreadPoolType.FIXED))
        {
            executor = daemon ? Executors.newFixedThreadPool(threadCount, daemonFactory) : Executors.newFixedThreadPool(threadCount, defaultFactory);
        }
        else if(poolType.equals(ThreadPoolType.CACHED))
        {
            executor = daemon ? Executors.newCachedThreadPool(daemonFactory) : Executors.newCachedThreadPool(defaultFactory);
        }
        else if(poolType.equals(ThreadPoolType.SINGLE))
        {
            executor = daemon ? Executors.newSingleThreadExecutor(daemonFactory) : Executors.newSingleThreadExecutor(defaultFactory);
        }
    }

    /**
     * Checks if the multithreader was terminated.
     * 
     * @return true if the executor was shutdown and false otherwise.
     */
    public boolean isTerminated()
    {
        return executor.isTerminated();
    }

    /**
     * Terminates the multithreader.
     */
    public void terminate()
    {
        executor.shutdown();

        try
        {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch(InterruptedException e)
        {
            logger.error(e.toString());
            System.exit(0);
        }
    }

    /**
     * Executes the given task using the multithreader without waiting.
     * 
     * @param r
     *        Runnable object that represent the task to be executed.
     */
    public void execute(Runnable r)
    {
        executor.execute(r);
    }

    /**
     * Execute a list of given tasks using the multithreader without waiting.
     * 
     * @param r
     *        List of Runnable objects that represent the tasks to be
     *        executed.
     */
    public void execute(List<Runnable> r)
    {
        for(int i = 0; i < r.size(); i++)
        {
            executor.execute(r.get(i));
        }
    }

    /**
     * Executes the given task using the multithreader and wait for the task to
     * finish executing.
     * 
     * @param r
     *        Runnable object that represent the task to be executed.
     */
    public void executeAndWait(Runnable r)
    {
        Collection<Callable<Object>> tasks = new LinkedList<Callable<Object>>();
        tasks.add(Executors.callable(r));
        invokeAllAndWait(tasks);
    }

    /**
     * Execute a list of given tasks using the multithreader and wait for all
     * the tasks to finish executing.
     * 
     * @param r
     *        List of Runnable objects that represent the tasks to be
     *        executed.
     */
    public void executeAndWait(List<Runnable> r)
    {
        Collection<Callable<Object>> tasks = new LinkedList<Callable<Object>>();

        for(Runnable task : r)
        {
            tasks.add(Executors.callable(task));
        }

        invokeAllAndWait(tasks);
    }

    /**
     * Execute the list of given tasks and wait.
     * 
     * @param tasks
     *        list of callable tasks to be executed.
     */
    public <T> void executeAndWait(Collection<Callable<T>> tasks)
    {
        invokeAllAndWait(tasks);
    }

    private <T> void invokeAllAndWait(Collection<Callable<T>> tasks)
    {
        try
        {
            executor.invokeAll(tasks);
        }
        catch(InterruptedException e)
        {
            logger.error(e.toString());
        }
    }

    /**
     * Executes the daemon thread that is responsible for environment
     * reorganization at the specified periodic time.
     * 
     * @param reorganizationTask
     *        A runnable object that represent an environment reorganization
     *        task
     * @param sleepTime
     *        A sleeping time between each executed reorganization task.
     */
    public void executePeriodicTask(final Runnable reorganizationTask, final long sleepTime)
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    executeAndWait(reorganizationTask);
                    sleep(sleepTime);
                }
            }
        });
    }

    private void sleep(long reorganizationSleepTime)
    {
        try
        {
            Thread.sleep(reorganizationSleepTime);
        }
        catch(InterruptedException e)
        {
            logger.error(e.toString());
        }
    }
}
