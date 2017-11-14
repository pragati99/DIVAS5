package edu.utdallas.mavs.divas.gui.services.process;

/**
 * Modified from eclipse platform version.
 */
public interface IStreamListener
{
    /**
     * Notifies this listener that text has been appended to
     * the given stream monitor.
     * 
     * @param text
     *        the appended text
     */
    public void streamAppended(String text);
}
