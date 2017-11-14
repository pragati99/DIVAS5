package edu.utdallas.mavs.divas.visualization.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class is used to parse the XML file that contains
 * the environment objects information, which is used later
 * for adding the environment objects
 */
public class EnvObjectLoader
{
    private static String       FILE                = Config.getEnvObjectsXML();

    private static Node         rootNode;

    private static List<String> envObjTypeIds;
    private static List<String> envObjTypeNames;

    // Environment Object Attributes
    private static String       ENV_OBJECT          = "envObject";
    private static String       ENV_OBJ_TYPE        = "envObjectType";
    private static String       ENV_OBJ_ID          = "id";
    private static String       ENV_OBJ_NAME        = "name";
    private static String       ENV_OBJ_MODEL_TYPE  = "type";
    private static String       ENV_OBJ_DESCRIPTION = "description";
    private static String       ENV_OBJ_SCALE       = "scale";
    private static String       ENV_OBJ_ROTATION    = "rotation";
    private static String       ENV_OBJ_POSITION    = "position";
    private static String       ENV_OBJ_IMAGE       = "image";
    private static String       ENV_OBJ_MATERIAL    = "material";
    private static String       ENV_OBJ_MODEL_NAME  = "modelName";
    private static String       ENV_OBJ_COLLIDABLE  = "collidable";

    private static void setup()
    {
        Document doc = DOMParser.parseDocument(FILE);
        rootNode = doc.getDocumentElement();
        envObjTypeIds = DOMParser.getChildElementsAttributesByTag(rootNode, ENV_OBJ_TYPE, ENV_OBJ_ID);
        envObjTypeNames = DOMParser.getChildElementsAttributesByTag(rootNode, ENV_OBJ_TYPE, ENV_OBJ_NAME);
    }

    /**
     * Gets a list of environment objects states for a specific environment object type
     * 
     * @param selectionId
     *        The id of the env object type selected
     * @param typeId
     *        The main environment object type
     * @param rootNode
     *        The root node for the XML file
     * @return A list of environment object states
     */
    public static List<EnvObjectState> getEnvObjectStates(int selectionId)
    {
        if(rootNode == null)
        {
            setup();
        }

        List<EnvObjectState> envObjList = new ArrayList<EnvObjectState>();

        // Get the root XML element from the given file
        Node selectedTypeNode = DOMParser.getElementById(rootNode, envObjTypeIds.get(selectionId));
        List<Node> childList = DOMParser.getElementsByTag(selectedTypeNode, ENV_OBJECT);

        for(int i = 0; i < childList.size(); i++)
        {
            Node node = childList.get(i);

            EnvObjectState envObjectState = new EnvObjectState();
            envObjectState.setType(DOMParser.getAttribute(node, ENV_OBJ_MODEL_TYPE));
            envObjectState.setDescription(DOMParser.getAttribute(node, ENV_OBJ_DESCRIPTION));
            envObjectState.setModelName(DOMParser.getAttribute(node, ENV_OBJ_MODEL_NAME));
            envObjectState.setMaterial(DOMParser.getAttribute(node, ENV_OBJ_MATERIAL));
            envObjectState.setName(DOMParser.getAttribute(node, ENV_OBJ_NAME));
            envObjectState.setImage(DOMParser.getAttribute(node, ENV_OBJ_IMAGE));
            envObjectState.setPosition(getVector3f(node, ENV_OBJ_POSITION));
            envObjectState.setRotation(getQuaternion(node, ENV_OBJ_ROTATION));
            envObjectState.setScale(getVector3f(node, ENV_OBJ_SCALE));
            envObjectState.setCollidable(Boolean.parseBoolean(DOMParser.getAttribute(node, ENV_OBJ_COLLIDABLE)));

            envObjList.add(envObjectState);
        }

        return envObjList;
    }

    /**
     * Gets a list of environment objects states for a specific environment object type
     * 
     * @param selectionId
     *        The id of the env object type selected
     * @param typeId
     *        The main environment object type
     * @param rootNode
     *        The root node for the XML file
     * @return A list of environment object states
     */
    public static List<VisResource<EnvObjectState>> getVisResources()
    {
        if(rootNode == null)
        {
            setup();
        }

        List<VisResource<EnvObjectState>> envObjList = new ArrayList<>();

        for(String id : envObjTypeIds)
        {
            // Get the root XML element from the given file
            Node selectedTypeNode = DOMParser.getElementById(rootNode, id);
            List<Node> childList = DOMParser.getElementsByTag(selectedTypeNode, ENV_OBJECT);
            for(int i = 0; i < childList.size(); i++)
            {
                Node node = childList.get(i);
                EnvObjectState envObjectState = new EnvObjectState();
                envObjectState.setType(DOMParser.getAttribute(node, ENV_OBJ_MODEL_TYPE));
                envObjectState.setDescription(DOMParser.getAttribute(node, ENV_OBJ_DESCRIPTION));
                envObjectState.setModelName(DOMParser.getAttribute(node, ENV_OBJ_MODEL_NAME));
                envObjectState.setMaterial(DOMParser.getAttribute(node, ENV_OBJ_MATERIAL));
                envObjectState.setName(DOMParser.getAttribute(node, ENV_OBJ_NAME));
                envObjectState.setImage(DOMParser.getAttribute(node, ENV_OBJ_IMAGE));
                envObjectState.setPosition(getVector3f(node, ENV_OBJ_POSITION));
                envObjectState.setRotation(getQuaternion(node, ENV_OBJ_ROTATION));
                envObjectState.setScale(getVector3f(node, ENV_OBJ_SCALE));
                envObjectState.setCollidable(Boolean.parseBoolean(DOMParser.getAttribute(node, ENV_OBJ_COLLIDABLE)));
                VisResource<EnvObjectState> resource = new VisResource<EnvObjectState>(DOMParser.getAttribute(node, ENV_OBJ_IMAGE), envObjectState.getName(), envObjectState.getType(), envObjectState.getDescription(), envObjectState);
                envObjList.add(resource);
            }
        }
        return envObjList;
    }

    /**
     * @return a list of environment object types found in the XML file.
     */
    public static List<String> getObjTypeNames()
    {
        if(rootNode == null)
        {
            setup();
        }

        return envObjTypeNames;
    }

    private static Vector3f getVector3f(Node node, String attribute)
    {
        List<Node> nodeChild = DOMParser.getElementsByTag(node, attribute); // note nodeChild is a list w/ one element
        float x = getValue(nodeChild.get(0), "x");
        float y = getValue(nodeChild.get(0), "y");
        float z = getValue(nodeChild.get(0), "z");
        return new Vector3f(x, y, z);
    }

    private static Quaternion getQuaternion(Node node, String attribute)
    {
        List<Node> nodeChild = DOMParser.getElementsByTag(node, attribute); // note nodeChild is a list w/ one element
        float x = getValue(nodeChild.get(0), "x");
        float y = getValue(nodeChild.get(0), "y");
        float z = getValue(nodeChild.get(0), "z");
        float w = getValue(nodeChild.get(0), "w");
        return new Quaternion(x, y, z, w);
    }

    private static Float getValue(Node node, String variable)
    {
        return Float.valueOf(DOMParser.getAttribute(node, variable));
    }
}
