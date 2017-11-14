package edu.utdallas.mavs.divas.core.host;

import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.config.HostConfig;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This class describes the Host configuration manager.
 */
public class HostConfigManager
{
    private final static Logger logger = LoggerFactory.getLogger(HostConfigManager.class);

    private CommunicationModule comModule;

    private HostConfig          hostConfig;

    /**
     * Creates a new instance of configuration manager
     * 
     * @param client the MTS client
     */
    public HostConfigManager(MTSClient client)
    {
        try
        {
            comModule = new CommunicationModule(client);

            comModule.addPublicationTopic(DivasTopic.hostConfigTopic);

            comModule.addSubscriptionTopic(DivasTopic.hostConfigTopic, new Subscriber()
            {
                @Override
                public void messageReceived(String topic, MTSPayload payload)
                {
                    switch(payload.getKey())
                    {
                    case 0:
                        // TODO: all hosts will send the config when only one is necessary
                        publishHostConfig();
                        break;
                    case 1:
                        if(payload.getData() instanceof HostConfig)
                        {
                            try
                            {
                                Host.getHost().getConfigManager().updateHostConfig((HostConfig) payload.getData());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2:
                        // restart/end Simulation
                        break;
                    }
                }
            });
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates the Host configuration
     * 
     * @param hostConfig a new Host configuration
     */
    public void updateHostConfig(HostConfig hostConfig)
    {
        this.hostConfig = hostConfig;
        logger.info("HostConfig updated.");
    }

    /**
     * Notifies that the simulation has been reset or shutdown
     */
    public void sendClearAllMsg()
    {
        try
        {
            logger.debug("Sending CLEAR ALL message.");
            comModule.publishMessage(new MTSPayload(2, null), DivasTopic.hostConfigTopic);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Requests the Host configuration from the other Hosts
     */
    public void requestHostConfig()
    {
        try
        {
            logger.debug("Requesting host configuration.");
            comModule.publishMessage(new MTSPayload(0, null), DivasTopic.hostConfigTopic);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Publishes the Host configuration
     */
    public void publishHostConfig()
    {
        try
        {
            logger.debug("Sending host configuration.");
            comModule.publishMessage(new MTSPayload(1, hostConfig), DivasTopic.hostConfigTopic);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Terminates the {@link ConfigurationManager}
     */
    public void terminate()
    {
        try
        {
            if(comModule != null)
                comModule.disconnect();
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }
}
