package edu.utdallas.mavs.divas.core.sim.env;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.msg.Workload;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This class describes a self-organizing cell controller.
 * 
 * @param <E>
 *        the environment's type of this cell controller
 */
public abstract class SelfOrganizingCellController<E extends SelfOrganizingEnvironment<?>> extends AbstractCellController<E>
{
    private static final long   serialVersionUID                  = 1L;

    private static final Logger logger                            = LoggerFactory.getLogger(SelfOrganizingCellController.class);

    /**
     * The timeout before this cell controller can offer resources again
     */
    protected static final int  RESOURCE_OFFER_TIMEOUT            = 25;

    /**
     * The elapsed time since last resource offer (measured in simulation cycles)
     */
    protected int               elapsedTimeSinceLastResourceOffer = 0;

    public SelfOrganizingCellController(CellState rootCellState, CommunicationModule comModule, E environment)
    {
        super(rootCellState, comModule, environment);
        setupCommunicationHandler(comModule);
    }

    private void setupCommunicationHandler(CommunicationModule comModule)
    {
        try
        {
            comModule.addPublicationTopic(DivasTopic.assistanceRequestTopic);
            comModule.addSubscriptionTopic(DivasTopic.c2cMessageTopic, c2cCommunicationHandler());
        }
        catch(MTSException e)
        {
            logger.error("An error has occurred while configuring communication handler in cell controller {}", getCellID(), e);
        }
    }

    @Override
    public void deliberate(List<AgentStateModel> agentStates)
    {
        super.deliberate(agentStates);
        if(environment.getStrategy() == ReorganizationStrategy.Autonomic || environment.getStrategy() == ReorganizationStrategy.Self_Organizing)
            checkWorkLoad();
    }

    /**
     * Checks this cell controller work load and initiate the appropriate course of actions to improve its performance.
     * Bottom-up self-organization.
     * Algorithm 1: AAMAS2013
     */
    protected void checkWorkLoad()
    {
        logger.debug("Cell controller {} worload {}", getCellID(), getWorkload());
        Workload workload = new Workload(getCellID(), getWorkload());
        if(cellState.getAgents().size() > 0 && workload.getWorkload() > SimConfig.getInstance().max_workload_threshold)
        {
            logger.debug("Cell controller {} with workload of {} sending alert workload to its coordinator", getCellID(), workload);
            environment.requestAssistance(workload);
            // sendAssistanceRequestMessage(workload);
        }
        else if(elapsedTimeSinceLastResourceOffer > RESOURCE_OFFER_TIMEOUT && workload.getWorkload() < SimConfig.getInstance().min_workload_threshold)
        {
            logger.debug("Cell controller {} with a workload of {} offering resources to {}", new Object[] { getCellID(), workload, getSiblingID() });
            sendOfferResourcesMessage(workload);
            elapsedTimeSinceLastResourceOffer = 0;
        }
        elapsedTimeSinceLastResourceOffer++;
    }

    @SuppressWarnings("unused")
    private void sendAssistanceRequestMessage(Workload workload)
    {
        sendMessage(new MTSPayload(0, workload), DivasTopic.assistanceRequestTopic);
    }

    private void sendOfferResourcesMessage(Workload workload)
    {
        sendMessage(new MTSPayload(0, workload), DivasTopic.c2cMessageTopic);
    }

    public float getWorkload()
    {
        if(agents.size() == 0)
            return 0;
        return SimConfig.getInstance().agent_weight * getAgentsCount() + SimConfig.getInstance().env_object_weight * getEnvObjectsCount();
    }

    private CellID getSiblingID()
    {
        if(environment.getCellMap().isRoot(getCellID()))
            return null;

        int siblingIndex = (getCellID().getLastPart() + 1) % 2;
        try
        {
            return getCellID().getParentID().createChild(siblingIndex);
        }
        catch(Exception e)
        {
            logger.warn("Attempt to find sibling for {} did not succeed", getCellID());
        }
        return null;
    }

    private boolean isSibling(CellID cellID)
    {
        CellID siblingID = getSiblingID();
        return siblingID != null && siblingID.equals(cellID);
    }

    private Subscriber c2cCommunicationHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                /**
                 * On receiving workload messages from its siblings, the cell controller analyzes its current workload
                 * and take the appropriate course of actions to improve its resource utilization.
                 * Algorithm 1: AAMAS2013
                 */
                if(payload.getData() instanceof Workload)
                {
                    Workload msg = (Workload) payload.getData();
                    // checks if the cell offering resources is a sibling
                    if(!msg.getCellID().equals(getCellID()) && !environment.getCellMap().isRoot(getCellID()) && isSibling(msg.getCellID()))
                    {
                        logger.debug("Cell controller {} received offer of resources from its sibling {}", getCellID(), msg.getCellID());
                        // compute workloads
                        float siblingWorkload = msg.getWorkload();
                        float myWorkload = getWorkload();
                        // if my workload and my sibling's workload are lower than the threshold, merge
                        if((myWorkload + siblingWorkload) < SimConfig.getInstance().max_workload_threshold)
                        {
                            logger.debug("Cell controller {} enqueuing merge with its sibling {}", getCellID(), msg.getCellID());
                            environment.enqueueCellMerge(getCellID());
                        }
                    }
                }
            }
        };
    }
}
