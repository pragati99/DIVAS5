package edu.utdallas.mavs.divas.gui.guice;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.StyleHelper;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.ConnectionDialog;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;

public class CommunicationModuleProvider implements Provider<CommunicationModule>
{
    private static final Logger        logger = LoggerFactory.getLogger(CommunicationModuleProvider.class);

    private static MTSClient           client;
    private static CommunicationModule communicationModule;

    @Override
    public CommunicationModule get()
    {
        if(communicationModule == null)
        {
            client = createMTSClient(null);
            if(client == null)
            {
                System.exit(0);
            }
            communicationModule = new CommunicationModule(client);
        }
        return communicationModule;
    }

    private static MTSClient createMTSClient(JFrame frame)
    {
        boolean connectedOrCancelled = false;

        do
        {
            ConnectionDialog dialog = showConnectionDialog(frame);
            try
            {
                if(!dialog.isCancelled())
                {
                    client = new MTSClient(dialog.getHostname(), dialog.getPort());
                    client.start();
                }
                else
                {
                    client = null;
                }
                connectedOrCancelled = true;
            }
            catch(Exception e)
            {
                logger.warn("Could not connect to Host:{} Port:{}", dialog.getHostname(), dialog.getPort());
            }
        } while(!connectedOrCancelled);

        return client;
    }

    private static ConnectionDialog showConnectionDialog(JFrame owner)
    {
        StyleHelper.setNimbusLookAndFeel();
        ConnectionDialog dialog = new ConnectionDialog(owner);
        dialog.showDialog();
        return dialog;
    }

    public static boolean isConnected()
    {
        return (null != client) && client.isConnected();
    }

    public static String getHostName()
    {
        return communicationModule.getHostName();
    }

    public static String getPort()
    {
        return communicationModule.getPort();
    }

    public static void connect(JFrame frame)
    {
        client = createMTSClient(frame);
        try
        {
            communicationModule.reconnect(client);
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    public static void disconnect()
    {

        communicationModule.forceDisconnect();
        client.terminateQuietly();
    }
}
