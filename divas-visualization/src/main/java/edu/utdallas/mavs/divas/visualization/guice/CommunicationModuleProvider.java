package edu.utdallas.mavs.divas.visualization.guice;

import com.google.inject.Provider;

import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.MTSClient;

/**
 * This class implements a provider for this component's communication module.
 * <p>
 * This project uses dependency injection based on google guice. This provider is used by the dependency injection
 * container to wire the component's communication module at runtime. This provider creates a client for the running
 * activeMQ message broker and initialize a communication module based on TCP.
 */
public class CommunicationModuleProvider implements Provider<CommunicationModule>
{
    private static String hostIP;
    private static String portNumber;

    @Override
    public CommunicationModule get()
    {
        MTSClient client = new MTSClient(hostIP, portNumber);
        client.startQuietly();
        return new CommunicationModule(client);
    }

    /**
     * Updates the connection properties of the communication module provider
     * 
     * @param host the IP of the simulation host
     * @param port the port of the simulation host
     */
    public static void setConnectionProperties(String host, String port)
    {
        hostIP = host;
        portNumber = port;
    }
}
