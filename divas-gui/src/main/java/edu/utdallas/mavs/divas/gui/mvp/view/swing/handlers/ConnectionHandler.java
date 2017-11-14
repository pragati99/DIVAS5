package edu.utdallas.mavs.divas.gui.mvp.view.swing.handlers;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.ClientDetailsFrame;

public class ConnectionHandler
{
    private static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public void connect()
    {

    }

    public void disconnect()
    {

    }

    public void startClient(String host, String port)
    {

    }

    public void connectionDetails(JFrame frame)
    {
        new ClientDetailsFrame(frame);
    }

    public void connectionOptions()
    {
        logger.info("NOT YET IMPLEMENTED: Connection options");
    }
}
