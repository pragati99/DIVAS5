package edu.utdallas.mavs.divas.core.sim.env;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.msg.CellStructureMsg;
import edu.utdallas.mavs.divas.core.msg.CellStructureMsg.Operation;
import edu.utdallas.mavs.divas.core.msg.Workload;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * This class describes a self organizing environment.
 * <p>
 * A self organizing environment continuously dynamically changes the environment configuration in response to changes in the state of the simulation.
 * 
 * @param <CC>
 *        the cell controller's type
 */
public abstract class SelfOrganizingEnvironment<CC extends SelfOrganizingCellController<?>> extends AbstractEnvironment<CC> implements Serializable
{
    private static final long                           serialVersionUID = 1L;

    private final static Logger                         logger           = LoggerFactory.getLogger(SelfOrganizingEnvironment.class);

    /**
     * A queue to store the reorganization requests.
     */
    protected transient ConcurrentLinkedQueue<Runnable> reorganizationQueue;

    /**
     * The current reorganization strategy in the environment.
     */
    protected ReorganizationStrategy                    reorganizationStrategy;

    /**
     * Self-organizing environment coordinator
     */
    protected Coordinator<CC>                           coordinator;

    /**
     * Constructs the <code>SelfOrganizedEnvironment</code> object by setting the given <code>MTSClient</code>,
     * loading the available reorganization strategies and starting a daemon thread service for executing
     * the environment reorganization requests.
     * 
     * @param client
     *        The <code>MTSClient</code> object which represents a client connection to a JMS broker server
     */
    public SelfOrganizingEnvironment(MTSClient client)
    {
        super(client);
        reorganizationQueue = new ConcurrentLinkedQueue<Runnable>();
        reorganizationStrategy = ReorganizationStrategy.No_Reorganization;
        coordinator = new Coordinator<>(this);
    }

    @Override
    protected void setupSubscriptions() throws MTSException
    {
        super.setupSubscriptions();
        comModule.addSubscriptionTopic(DivasTopic.cellStructureTopic, cellStructureMsgHandler());
        comModule.addSubscriptionTopic(DivasTopic.reorganizationTopic, reorganizationHandler());
        comModule.addSubscriptionTopic(DivasTopic.assistanceRequestTopic, assistanceRequestHandler());
    }

    @Override
    protected void setupPublications() throws MTSException
    {
        super.setupPublications();
        comModule.addPublicationTopic(DivasTopic.c2cMessageTopic);
    }

    @Override
    protected Multithreader createMultithreader()
    {
        return new Multithreader("EnvironmentThrd", ThreadPoolType.FIXED, 5, false);
    }

    public void requestAssistance(Workload workload)
    {
        coordinator.requestAssistance(workload);
    }

    @Override
    protected void processReorganizationQueue()
    {
        Runnable task;
        while((task = reorganizationQueue.poll()) != null)
        {
            multithreader.executeAndWait(task);
        }
    }

    /**
     * Enqueue a cell controller split request to the reorganization requests queue.
     * 
     * @param cellID
     *        The <code>CellID</code> for the cell controller to be split.
     */
    public void enqueueCellSplit(final CellID cellID)
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                splitCell(cellID);
            }
        };
        reorganizationQueue.add(task);
    }

    /**
     * Enqueue a cell controller merge request to the reorganization requests queue.
     * 
     * @param cellID
     *        The <code>CellID</code> for the cell controller to be merged.
     */
    public void enqueueCellMerge(final CellID cellID)
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                mergeCell(cellID);
            }
        };
        reorganizationQueue.add(task);
    }

    /**
     * Sets the current reorganization strategy of the environment to the given strategy.
     * 
     * @param strategy
     *        The <code>ReorganizationStrategy</code> to be set.
     */
    public void setStrategy(ReorganizationStrategy strategy)
    {
        synchronized(reorganizationStrategy)
        {
            reorganizationStrategy = strategy;
            logger.info("Setting strategy to: {}", strategy.toString());
        }
    }

    /**
     * @return The current reorganization strategy in the environment.
     */
    public ReorganizationStrategy getStrategy()
    {
        return reorganizationStrategy;
    }

    private void splitCell(CellID id)
    {
        CC cc = getCellController(id);

        if(cc == null || !cellMap.isLeaf(id))
            return;

        // block external stimuli while the environment is restructuring
        synchronized(restructureLock)
        {
            List<CC> children = cc.split();

            // add the children to the list of cell controllers
            for(CC child : children)
            {
                cells.put(child.getCellID(), child);
            }

            // remove the parent from the list of cell controllers
            cells.remove(cc.getCellID());

            // update the cell map
            cellMap.split(cc.getCellID(), children.get(0).getCellState(), children.get(1).getCellState());
        }
    }

    private void mergeCell(CellID id)
    {
        CC cc = getCellController(id);

        // can't merge the root cell
        if(cc == null || cc.getCellID().equals(CellID.rootID()))
            return;

        // get the child index for my sibling
        int siblingIndex = (cc.getCellID().getLastPart() + 1) % 2;

        CellID siblingID = cc.getCellID().getParentID().createChild(siblingIndex);

        if(!cellMap.isLeaf(siblingID))
        {
            logger.warn("Merge failed, sibling is still split: " + siblingID);
            return;
        }

        CC sibling = getCellController(siblingID);

        if(sibling == null)
        {
            logger.warn("Merge failed, not controlling sibling: " + siblingID);
            return;
        }

        // block external stimuli while the environment is restructuring
        synchronized(restructureLock)
        {
            cells.remove(cc.getCellID());
            cc.merge(sibling);
            cells.put(cc.getCellID(), cc);

            logger.debug("Dismissing cell controller: " + sibling.getCellID());

            // remove the parent from the list of cell controllers
            cells.remove(siblingID);
        }

        cellMap.merge(cc.getCellID());
    }

    private Subscriber cellStructureMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                CellStructureMsg msg = (CellStructureMsg) payload.getData();
                if(msg.getOperation() == Operation.SPLIT)
                {
                    enqueueCellSplit(msg.getCellID());
                }
                else if(msg.getOperation() == Operation.MERGE)
                {
                    enqueueCellMerge(msg.getCellID());
                }
            }
        };
    }

    private Subscriber reorganizationHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                ReorganizationStrategy str = (ReorganizationStrategy) payload.getData();
                setStrategy(str);
            }
        };
    }

    private Subscriber assistanceRequestHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                Workload w = (Workload) payload.getData();
                coordinator.requestAssistance(w);
            }
        };
    }
}
