package edu.utdallas.mavs.divas.mts;

import java.util.ArrayList;

import javax.management.ObjectName;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.ConstantPendingMessageLimitStrategy;
import org.apache.activemq.broker.region.policy.NoSubscriptionRecoveryPolicy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the embedded broker for the message transport system.
 * <p>
 * A message broker is an architectural pattern for message validation, message transformation and message routing. It mediates communication amongst applications, minimizing the mutual awareness that applications should have of each other in order
 * to be able to exchange messages, effectively implementing decoupling.
 * <p>
 * The purpose of a broker is to take incoming messages from applications and route the messages to one or more of many destinations subscribed to.
 */
public class MTSBroker
{
    private final static Logger logger        = LoggerFactory.getLogger(MTSBroker.class);

    private BrokerService       brokerService = null;

    private static String       brokerHost    = "0.0.0.0";                               // as of activemq 5.3, 0.0.0.0
                                                                                          // required instead of
                                                                                          // localhost
    private String              brokerPort;

    /**
     * Constructs the <code>MTSBroker</code> instance, by assigning the port number.
     * 
     * @param brokerPort
     *        the server's listening port number.
     */
    public MTSBroker(String brokerPort)
    {
        logger.debug("Creating MTSBroker instance.");

        // Initialize the broker
        this.brokerPort = brokerPort;
    }

    /**
     * Starts the <code>MTSBroker</code> services.
     * 
     * @throws Exception
     */
    public void start() throws Exception
    {
        logger.debug("Starting broker: " + this.toString());

        brokerService = new BrokerService();
        try
        {
            brokerService.setPersistent(false);
            brokerService.getManagementContext().setCreateMBeanServer(false);
            brokerService.setUseJmx(false);
            brokerService.setEnableStatistics(false);
            brokerService.getManagementContext().setCreateMBeanServer(false);
            brokerService.setAdvisorySupport(false);
            brokerService.setDedicatedTaskRunner(false);

            PolicyMap policyMap = new PolicyMap();
            PolicyEntry policyEntry = new PolicyEntry();
            policyEntry.setTopic(">");
            policyEntry.setProducerFlowControl(false);
            // policyEntry.setMemoryLimit(1048576);
            NoSubscriptionRecoveryPolicy nr = new NoSubscriptionRecoveryPolicy();
            policyEntry.setSubscriptionRecoveryPolicy(nr);

            ConstantPendingMessageLimitStrategy constantPendingMessageLimitStrategy = new ConstantPendingMessageLimitStrategy();
            constantPendingMessageLimitStrategy.setLimit(10);
            policyEntry.setPendingMessageLimitStrategy(constantPendingMessageLimitStrategy);

            policyEntry.setGcInactiveDestinations(true);
            // policyEntry.setGcWithNetworkConsumers(true);
            policyEntry.setReduceMemoryFootprint(true);
            policyMap.setDefaultEntry(policyEntry);
            brokerService.setDestinationPolicy(policyMap);

            brokerService.addConnector("tcp://" + brokerHost + ":" + brokerPort);

            brokerService.start();
            logger.info("Broker started: {}. Using JMX: {}.", this.toString(), brokerService.isUseJmx());
        }
        catch(Exception e)
        {
            logger.error("Failed to start broker: {}. Check if there is a simulation already running.", this.toString());
            brokerService = null;
            throw e;
        }
    }

    // /**
    // * Starts the <code>MTSBroker</code> services.
    // *
    // * @throws Exception
    // */
    // public void start() throws Exception
    // {
    // logger.debug("Starting broker: " + this.toString());
    //
    // brokerService = new BrokerService();
    // try
    // {
    // brokerService.setPersistent(false);
    //
    // PolicyMap policyMap = new PolicyMap();
    // PolicyEntry policyEntry = new PolicyEntry();
    // policyEntry.setTopic(">");
    // policyEntry.setProducerFlowControl(false);
    // policyEntry.setMemoryLimit(1048576);
    // ConstantPendingMessageLimitStrategy constantPendingMessageLimitStrategy = new ConstantPendingMessageLimitStrategy();
    // constantPendingMessageLimitStrategy.setLimit(10);
    // policyEntry.setPendingMessageLimitStrategy(constantPendingMessageLimitStrategy);
    // policyEntry.setPendingSubscriberPolicy(new VMPendingSubscriberMessageStoragePolicy());
    // policyEntry.setPendingDurableSubscriberPolicy(new VMPendingDurableSubscriberMessageStoragePolicy());
    // policyMap.setDefaultEntry(policyEntry);
    // brokerService.setDestinationPolicy(policyMap);
    //
    // // brokerService.getSystemUsage().getMemoryUsage().setLimit(1073741824);
    // // brokerService.getSystemUsage().getTempUsage().setLimit(1073741824);
    //
    // // brokerService.addConnector("tcp://" + brokerHost + ":" + brokerPort + "?async=false");
    // brokerService.addConnector("tcp://" + brokerHost + ":" + brokerPort + "?socketBufferSize=131072&ioBufferSize=16384");
    // // brokerService.addConnector("tcp://" + brokerHost + ":" + brokerPort + "?jms.useAsyncSend=true");
    // brokerService.start();
    // logger.info("Broker started: " + this.toString());
    // }
    // catch(Exception e)
    // {
    // logger.error("Failed to start broker: {}. Check if there is a simulation already running.", this.toString());
    // brokerService = null;
    // throw e;
    // }
    // }

    /**
     * @return boolean flag that indicates if the <code>MTSBroker</code> is running.
     */
    public boolean isRunning()
    {
        if(brokerService != null)
            return brokerService.isStarted();

        return false;
    }

    /**
     * Terminates the current <code>MTSBroker</code> services.
     */
    public void terminate()
    {
        if(brokerService != null)
        {
            try
            {
                logger.debug("Stopping broker: " + this.toString());
                brokerService.stop();
                logger.info("Broker stopped: " + this.toString());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            brokerService = null;
        }
    }

    /**
     * Gets the name or IP address of the server running the JMS broker.
     * 
     * @return The <code>Host</code> of the <code>MTSBroker</code>.
     */
    public String getBrokerHost()
    {
        return brokerHost;
    }

    /**
     * Gets the server's listening port number.
     * 
     * @return The port number of the <code>MTSBroker</code>.
     */
    public String getBrokerPort()
    {
        return brokerPort;
    }

    /**
     * @return String that represents a list of topics that the <code>MTSBroker</code> knows.
     */
    public String getTopicList()
    {
        try
        {
            ArrayList<String> topicList = new ArrayList<String>();

            ObjectName[] topics = brokerService.getAdminView().getTopics();
            for(ObjectName fullName : topics)
            {
                String topicName = fullName.toString().replaceAll(".*[=\\.](.*)$", "$1");
                if(!topicList.contains(topicName))
                    topicList.add(topicName);
            }
            String names = "";
            for(String name : topicList)
                names += name + ",";
            return names;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * @return A string that gives a detailed information and statistics about the <code>MTSBroker</code>.
     */
    public String getStatsString()
    {
        try
        {
            return "Broker Name: " + brokerService.getAdminView().getBrokerName() + "\nTotal Message Count: " + brokerService.getAdminView().getTotalMessageCount() + "\nTotal Messages Cached: " + brokerService.getAdminView().getTotalMessagesCached()
                    + "\nStore Limit: " + brokerService.getAdminView().getStoreLimit() + "\nStore Percent Usage: " + brokerService.getAdminView().getStorePercentUsage() + "\nMemory Limit: " + brokerService.getAdminView().getMemoryLimit()
                    + "\nMemory Percent Usage: " + brokerService.getAdminView().getMemoryPercentUsage() + "\nTemp Limit: " + brokerService.getAdminView().getTempLimit() + "\nTemp Percent Usage: " + brokerService.getAdminView().getTempPercentUsage();
        }
        catch(Exception e)
        {
            return "";
        }
    }

    @Override
    public String toString()
    {
        return brokerHost + ":" + brokerPort;
    }
}
