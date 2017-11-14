package edu.utdallas.mavs.divas.core.client;

import java.io.File;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.msg.AssignEventIDMsg;
import edu.utdallas.mavs.divas.core.msg.AssignStateIDMsg;
import edu.utdallas.mavs.divas.core.msg.CellStructureMsg;
import edu.utdallas.mavs.divas.core.msg.RemoveStateMsg;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg.RuntimeAgentCommand;
import edu.utdallas.mavs.divas.core.msg.SimControlMsg;
import edu.utdallas.mavs.divas.core.msg.SimControlMsg.SimControlCommand;
import edu.utdallas.mavs.divas.core.msg.SimLoaderMsg;
import edu.utdallas.mavs.divas.core.msg.SimLoaderMsg.SimLoaderCommand;
import edu.utdallas.mavs.divas.core.msg.StateUpdateMsg;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This class implements a facade for clients to interact with the simulation.
 */
public class SimCommander implements SimFacade
{
    private final static Logger logger = LoggerFactory.getLogger(SimCommander.class);

    private CommunicationModule comModule;

    /**
     * Constructs a simulation commander. Injects the necessary dependencies.
     * 
     * @param comModule
     *        the communication module, which allows interactions with the simulation host
     */
    @Inject
    public SimCommander(CommunicationModule comModule)
    {
        this.comModule = comModule;
        setupPublications();
    }

    @Override
    public void setupPublications()
    {
        try
        {
            comModule.addPublicationTopic(DivasTopic.externalStimulusTopic);
            comModule.addPublicationTopic(DivasTopic.runtimeAgentCommandTopic);
            comModule.addPublicationTopic(DivasTopic.simulationControlTopic);
            comModule.addPublicationTopic(DivasTopic.reorganizationTopic);
            comModule.addPublicationTopic(DivasTopic.cellStructureTopic);
            comModule.addPublicationTopic(DivasTopic.simulationPropertiesTopic);
            comModule.addPublicationTopic(DivasTopic.simSnapshotTopic);
        }
        catch(MTSException e)
        {
            logger.error("Publications could not be added to the broker session.");
        }
    }

    @Override
    public void sendRuntimeAgentCommand(int agentID, RuntimeAgentCommand command, Object arg)
    {
        sendMessage(DivasTopic.runtimeAgentCommandTopic, -1, new RuntimeAgentCommandMsg(command, agentID, arg));
    }

    @Override
    public void createAgent(AgentState agent)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new AssignStateIDMsg(agent));
    }

    @Override
    public void createEnvObject(EnvObjectState envObject)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new AssignStateIDMsg(envObject));
    }

    @Override
    public void createEvent(EnvEvent event)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new AssignEventIDMsg(event));
    }

    @Override
    public void sendStateUpdate(VirtualState state)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new StateUpdateMsg(state));
    }

    @Override
    public void startSimulation()
    {
        sendMessage(DivasTopic.simulationControlTopic, -1, new SimControlMsg(SimControlCommand.START));
    }

    @Override
    public void pauseSimulation()
    {
        sendMessage(DivasTopic.simulationControlTopic, -1, new SimControlMsg(SimControlCommand.PAUSE));
    }

    @Override
    public void saveSimulationSnapshot(File file)
    {
        sendMessage(DivasTopic.simSnapshotTopic, -1, new SimLoaderMsg(file, SimLoaderCommand.SAVE_SIMULATION));
    }

    @Override
    public void loadSimulationSnapshot(File file)
    {
        sendMessage(DivasTopic.simSnapshotTopic, -1, new SimLoaderMsg(file, SimLoaderCommand.LOAD_SIMULATION));
    }

//    @Override
//    public void loadEnvironment(File file)
//    {
//        sendMessage(DivasTopic.simSnapshotTopic, -1, new SimLoaderMsg(file, SimLoaderCommand.LOAD_ENVIRONMENT));
//    }

    @Override
    public void loadEnvironmentXML(File file)
    {
        sendMessage(DivasTopic.simSnapshotTopic, -1, new SimLoaderMsg(file, SimLoaderCommand.LOAD_ENVIRONMENT_XML));
    }

    @Override
    public void saveEnvironment(File file)
    {
        sendMessage(DivasTopic.simSnapshotTopic, -1, new SimLoaderMsg(file, SimLoaderCommand.SAVE_ENVIRONMENT));
    }

    @Override
    public void splitCell(CellID cellID)
    {
        sendMessage(DivasTopic.cellStructureTopic, -1, CellStructureMsg.getSplitMsg(cellID));

    }

    @Override
    public void mergeCell(CellID cellID)
    {
        sendMessage(DivasTopic.cellStructureTopic, -1, CellStructureMsg.getMergeMsg(cellID));

    }

    @Override
    public void notifySimPropertiesChange()
    {
        sendMessage(DivasTopic.simulationPropertiesTopic, -1, null);
    }

    @Override
    public void addSubscription(String topic, Subscriber subscriber)
    {
        try
        {
            comModule.addSubscriptionTopic(topic, subscriber);
        }
        catch(MTSException e)
        {
            logger.error("Subscription could not be added for topic {}.", topic);
        }
    }

    @Override
    public void setReorganizationStrategy(ReorganizationStrategy str)
    {
        sendMessage(DivasTopic.reorganizationTopic, -1, str);
    }

    @Override
    public void destroyAgent(AgentState agentState)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new RemoveStateMsg(agentState));
    }

    @Override
    public void destroyEnvObject(EnvObjectState envObjState)
    {
        sendMessage(DivasTopic.externalStimulusTopic, -1, new RemoveStateMsg(envObjState));
    }

    private void sendMessage(String topic, int key, Serializable obj)
    {
        try
        {
            comModule.publishMessage(new MTSPayload(key, obj), topic);
        }
        catch(MTSException e)
        {
            logger.error("Message could not be published for topic {}.", topic);
        }
    }

    @Override
    public String toString()
    {
        if(comModule != null)
            return "SimCommander[" + comModule.getHostName() + ":" + comModule.getPort() + "]";
        return "SimCommander[disconnected]";
    }

    /**
     * Disposes the resources of the commander
     */
    public void close()
    {
        logger.debug("Closing SimCommander.");

        if(comModule != null)
        {
            comModule.forceDisconnect();
        }
    }

}
