package edu.utdallas.mavs.divas.visualization.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import edu.utdallas.mavs.divas.core.config.Config;

/**
 * This class is used to parse the XML file that contains
 * the events information, which is used later
 * for adding the events
 */
public class EventLoader
{
    private static String FILE              = Config.getEventsXML();

    private static Node   rootNode;

    // Event Attributes
    private static String EVENT_TYPE        = "eventType";
    private static String EVENT_NAME        = "name";
    private static String EVENT_MODE        = "mode";
    private static String EVENT_DESCRIPTION = "description";
    private static String ENVEVENT_TYPE     = "type";
    private static String EVENT_IMAGE       = "image";

    private static void setup()
    {
        Document doc = DOMParser.parseDocument(FILE);
        rootNode = doc.getDocumentElement();
    }

    /**
     * Gets a list of Event of event modes
     * 
     * @return A list of event modes
     */
    public static List<VisResource<Event>> getVisResources()
    {
        if(rootNode == null)
        {
            setup();
        }

        List<VisResource<Event>> envEventList = new ArrayList<>();

        // Get the root XML element from the given file
        List<Node> childList = DOMParser.getElementsByTag(rootNode, EVENT_TYPE);

        for(int i = 0; i < childList.size(); i++)
        {
            Node node = childList.get(i);
            Event eventMode = new Event(DOMParser.getAttribute(node, EVENT_MODE));

            VisResource<Event> resource = new VisResource<Event>(DOMParser.getAttribute(node, EVENT_IMAGE), DOMParser.getAttribute(node, EVENT_NAME), DOMParser.getAttribute(node, ENVEVENT_TYPE), DOMParser.getAttribute(node, EVENT_DESCRIPTION), eventMode);
            envEventList.add(resource);
        }

        return envEventList;
    }

    /**
     * This class is for storing the input mode in the simulatingAppState when pressing an event in the 3D visualizer.
     */
    public static class Event
    {
        String inputMode;

        /**
         * Constructs the EventMode object
         * 
         * @param inputMode The inputMode to be set in the object
         */
        public Event(String inputMode)
        {
            this.inputMode = inputMode;
        }

        /**
         * @return The input mode stored in the object
         */
        public String getInputMode()
        {
            return inputMode;
        }
    }
}
