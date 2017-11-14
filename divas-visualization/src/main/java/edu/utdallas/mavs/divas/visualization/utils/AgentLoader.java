package edu.utdallas.mavs.divas.visualization.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;

/**
 * This class is used to parse the XML file that contains
 * the agents information, which is used later
 * for adding the agents
 */
public class AgentLoader
{
    private static String       FILE                = Config.getAgentsXML();

    private static Node         rootNode;

    private static List<String> agentTypeIds;
    private static List<String> agentTypeNames;

    // Agent Attributes
    private static String       AGENT          = "agentClass";
    private static String       AGENT_TYPE        = "agentType";
    private static String       AGENT_ID          = "id";
    private static String       AGENT_NAME        = "name";
    private static String       AGENT_MODEL_TYPE  = "type";
    private static String       AGENT_DESCRIPTION = "description";
    private static String       AGENT_SCALE       = "scale";
    private static String       AGENT_POSITION    = "position";
    private static String       AGENT_IMAGE       = "image";
    private static String       AGENT_MODEL_NAME  = "modelName";
    

    private static void setup()
    {
        Document doc = DOMParser.parseDocument(FILE);
        rootNode = doc.getDocumentElement();
        agentTypeIds = DOMParser.getChildElementsAttributesByTag(rootNode, AGENT_TYPE, AGENT_ID);
        agentTypeNames = DOMParser.getChildElementsAttributesByTag(rootNode, AGENT_TYPE, AGENT_NAME);
    }

    /**
     * Gets a list of environment objects states for a specific environment object type
     * 
     * @param selectionId
     *        The agent type selected
     * @return A list of agent states
     */
    public static List<AgentState> getAgentStates(int selectionId)
    {
        if(rootNode == null)
        {
            setup();
        }

        List<AgentState> agentList = new ArrayList<AgentState>();

        // Get the root XML element from the given file
        Node selectedTypeNode = DOMParser.getElementById(rootNode, agentTypeIds.get(selectionId));
        List<Node> childList = DOMParser.getElementsByTag(selectedTypeNode, AGENT);

        for(int i = 0; i < childList.size(); i++)
        {
            Node node = childList.get(i);

            AgentState agentState = new AgentState();
            agentState.setModelName(DOMParser.getAttribute(node, AGENT_MODEL_NAME));
            agentState.setPosition(getVector3f(node, AGENT_POSITION));
            agentState.setScale(getVector3f(node, AGENT_SCALE));
            agentList.add(agentState);
        }

        return agentList;
    }

    /**
     * Gets a list of agents states for a specific environment object type
     * 
     * @return A list of agents states
     */
    public static List<VisResource<AgentState>> getVisResources()
    {
        if(rootNode == null)
        {
            setup();
        }

        List<VisResource<AgentState>> agentList = new ArrayList<>();

        for(String id : agentTypeIds)
        {
            // Get the root XML element from the given file
            Node selectedTypeNode = DOMParser.getElementById(rootNode, id);
            List<Node> childList = DOMParser.getElementsByTag(selectedTypeNode, AGENT);
            for(int i = 0; i < childList.size(); i++)
            {
                Node node = childList.get(i);
                AgentState agentState = new AgentState();
                agentState.setModelName(DOMParser.getAttribute(node, AGENT_MODEL_NAME));
                agentState.setPosition(getVector3f(node, AGENT_POSITION));
                agentState.setScale(getVector3f(node, AGENT_SCALE));
                VisResource<AgentState> resource = new VisResource<AgentState>(DOMParser.getAttribute(node, AGENT_IMAGE), DOMParser.getAttribute(node, AGENT_NAME), DOMParser.getAttribute(node, AGENT_MODEL_TYPE), DOMParser.getAttribute(node, AGENT_DESCRIPTION), agentState );
                agentList.add(resource);
            }
        }
        return agentList;
    }

    /**
     * @return a list of agent types found in the XML file.
     */
    public static List<String> getObjTypeNames()
    {
        if(rootNode == null)
        {
            setup();
        }

        return agentTypeNames;
    }

    private static Vector3f getVector3f(Node node, String attribute)
    {
        List<Node> nodeChild = DOMParser.getElementsByTag(node, attribute); // note nodeChild is a list w/ one element
        float x = getValue(nodeChild.get(0), "x");
        float y = getValue(nodeChild.get(0), "y");
        float z = getValue(nodeChild.get(0), "z");
        return new Vector3f(x, y, z);
    }


    private static Float getValue(Node node, String variable)
    {
        return Float.valueOf(DOMParser.getAttribute(node, variable));
    }
}
