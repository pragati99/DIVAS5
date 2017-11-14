package edu.utdallas.mavs.divas.core.spec.env;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellController;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.utils.DOMParser;

/**
 * This is a helper class for saving and loading environment.
 */
public class EnvLoader implements Serializable
{
    private static final long   serialVersionUID = 1L;

    private final static Logger logger           = LoggerFactory.getLogger(EnvLoader.class);

    /**
     * Default folder for environment specifications.
     */
    protected static String     envFolder        = System.getProperty("user.home") + System.getProperty("file.separator") + "Divas" + System.getProperty("file.separator") + "environment";

    /**
     * Saves the environment to an XML file.
     * 
     * @param fileName
     *        The full path to where the environment will be saved to.
     * @param envSpec
     *        An environment specification to be saved to an XML file.
     */
    public void saveEnvironment(String fileName, EnvSpec envSpec)
    {
        CellStateSpec cellStateSpec = envSpec.getCellState();
        saveEnvironment(fileName, cellStateSpec.getBounds(), cellStateSpec.getEnvObjects());
        logger.info("Saved environment {} to local user folder.", fileName);
    }

    /**
     * Saves the environment to an XML file.
     * 
     * @param fileName
     *        The full path to where the environment will be saved to.
     */
    public void saveEnvironment(String fileName)
    {
        if(Host.getHost().getEnvironment() != null)
        {
            CellBounds bounds = Host.getHost().getEnvironment().getCellMap().getRoot().getBounds();
            List<EnvObjectState> objects = new ArrayList<EnvObjectState>();

            for(CellController cc : Host.getHost().getSimulation().getEnvironment().getCellControllers())
            {
                for(EnvObjectState eo : cc.getCellState().getEnvObjects())
                {
                    objects.add(eo);
                }
            }

            saveEnvironment(fileName, bounds, objects);
        }
    }

    /**
     * Saves the environment to an XML file.
     * 
     * @param fileName
     *        The full path to where the environment will be saved to.
     * @param bounds
     *        The bounds of the environment.
     * @param objects
     *        A list of current environment objects in the environment.
     */
    public void saveEnvironment(String fileName, CellBounds bounds, List<EnvObjectState> objects)
    {
        Document doc = getDOMDocument();

        // creates an environment element as a root element in the DOM document
        Element rootElement = doc.createElement("environment");
        doc.appendChild(rootElement);

        // creates a cellBound element attached to the rootElement using
        // environment boundaries
        Element cellBounds = doc.createElement("cellBounds");
        rootElement.appendChild(cellBounds);
        setCellBoundsAttribute(cellBounds, bounds);

        for(EnvObjectState eo : objects)
        {
            // envObject elements
            Element envObject = doc.createElement("envObject");
            rootElement.appendChild(envObject);
            setEnvObjectAttribute(eo, envObject);
        }

        writeToXML(fileName, doc);
    }

    /**
     * Generate EnvSpec object that represent the environment specifications
     * from the given XML file
     * 
     * @param file
     * @return EnvSpec object for the environment
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public EnvSpec loadToEnvSpec(final String file) throws ClassNotFoundException, IOException
    {
        return loadToEnvSpec(file, false);
    }

    /**
     * Generate EnvSpec object that represent the environment specifications
     * from the given XML file
     * 
     * @param file
     * @param inClassPath
     * @return EnvSpec object for the environment
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public EnvSpec loadToEnvSpec(String file, boolean inClassPath) throws IOException, ClassNotFoundException
    {
        Node rootNode = null;

        EnvSpec envSpec = new EnvSpec(this);

        Document doc = DOMParser.parseDocument(file, inClassPath);
        rootNode = doc.getDocumentElement();

        // Get the root XML element from the given file
        List<Node> childList = DOMParser.getElementsByTag(rootNode, "envObject");

        List<Node> cellBoundsList = DOMParser.getElementsByTag(rootNode, "cellBounds");
        Node cellBoundNode = cellBoundsList.get(0);

        CellBounds cellBounds = retrieveCellBounds(cellBoundNode);
        CellStateSpec cell = new CellStateSpec(CellID.rootID(), cellBounds);

        for(int i = 0; i < childList.size(); i++)
        {
            Node node = childList.get(i);
            cell.addEnvObject(retrieveEnvObjAttributes(node));
        }

        envSpec.setCellState(cell);
        return envSpec;
    }

    protected void writeToXML(String name, Document doc)
    {
        try
        {
            // write the content into XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            File file = new File(envFolder);

            if(!file.exists())
            {
                file.mkdir();
                logger.info("{} was created.", envFolder);
            }

            file = new File(name);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        }
        catch(TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
        }
    }

    protected Document getDOMDocument()
    {
        Document doc = null;

        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            doc = docBuilder.newDocument();
        }
        catch(ParserConfigurationException pce)
        {
            pce.printStackTrace();
        }

        return doc;
    }

    protected void setEnvObjectAttribute(EnvObjectState eo, Element envObject)
    {
        envObject.setAttribute("id", ((Integer) eo.getID()).toString());
        envObject.setAttribute("type", eo.getType());
        envObject.setAttribute("material", eo.getMaterial());
        envObject.setAttribute("name", eo.getName());
        envObject.setAttribute("image", eo.getImage());
        envObject.setAttribute("onFire", Boolean.toString(eo.isOnFire()));
        envObject.setAttribute("visualized", Boolean.toString(eo.isVisualized()));
        envObject.setAttribute("collidable", Boolean.toString(eo.isCollidable()));
        envObject.setAttribute("modelName", eo.getModelName());
        envObject.setAttribute("description", eo.getDescription());

        envObject.setAttribute("posX", (((Float) eo.getPosition().getX()).toString()));
        envObject.setAttribute("posY", (((Float) eo.getPosition().getY()).toString()));
        envObject.setAttribute("posZ", (((Float) eo.getPosition().getZ()).toString()));

        envObject.setAttribute("scaleX", (((Float) eo.getScale().getX()).toString()));
        envObject.setAttribute("scaleY", (((Float) eo.getScale().getY()).toString()));
        envObject.setAttribute("scaleZ", (((Float) eo.getScale().getZ()).toString()));

        envObject.setAttribute("rotX", (((Float) eo.getRotation().getX()).toString()));
        envObject.setAttribute("rotY", (((Float) eo.getRotation().getY()).toString()));
        envObject.setAttribute("rotZ", (((Float) eo.getRotation().getZ()).toString()));
        envObject.setAttribute("rotW", (((Float) eo.getRotation().getW()).toString()));
    }

    protected void setCellBoundsAttribute(Element cellBounds, CellBounds cb)
    {
        cellBounds.setAttribute("minX", ((Double) cb.getMinX()).toString());
        cellBounds.setAttribute("maxX", ((Double) cb.getMaxX()).toString());
        cellBounds.setAttribute("minY", ((Double) cb.getMinY()).toString());
        cellBounds.setAttribute("maxY", ((Double) cb.getMaxY()).toString());
        cellBounds.setAttribute("minZ", ((Double) cb.getY()).toString());
        cellBounds.setAttribute("maxZ", ((Double) (cb.getY() + cb.getHeight())).toString());
    }

    protected CellBounds retrieveCellBounds(Node cellBoundNode)
    {
        return new CellBounds(Float.parseFloat(DOMParser.getAttribute(cellBoundNode, "minX")), Float.parseFloat(DOMParser.getAttribute(cellBoundNode, "maxX")), Float.parseFloat(DOMParser.getAttribute(
                cellBoundNode, "minY")), Float.parseFloat(DOMParser.getAttribute(cellBoundNode, "maxY")), Float.parseFloat(DOMParser.getAttribute(cellBoundNode, "minZ")), Float.parseFloat(DOMParser.getAttribute(
                cellBoundNode, "maxZ")));
    }

    protected EnvObjectState retrieveEnvObjAttributes(Node node)
    {
        EnvObjectState eo = new EnvObjectState();

        eo.setDescription((DOMParser.getAttribute(node, "description")));
        eo.setType((DOMParser.getAttribute(node, "type")));
        eo.setMaterial((DOMParser.getAttribute(node, "material")));
        eo.setName((DOMParser.getAttribute(node, "name")));
        eo.setImage((DOMParser.getAttribute(node, "image")));
        eo.setOnFire(Boolean.valueOf((DOMParser.getAttribute(node, "onFire"))));
        eo.setVisualized(Boolean.valueOf((DOMParser.getAttribute(node, "visualized"))));
        eo.setCollidable(Boolean.valueOf((DOMParser.getAttribute(node, "collidable"))));
        eo.setModelName((DOMParser.getAttribute(node, "modelName")));

        eo.setPosition(new Vector3f(Float.parseFloat(DOMParser.getAttribute(node, "posX")), Float.parseFloat(DOMParser.getAttribute(node, "posY")), Float.parseFloat(DOMParser.getAttribute(node,
                "posZ"))));
        eo.setScale(new Vector3f(Float.parseFloat(DOMParser.getAttribute(node, "scaleX")), Float.parseFloat(DOMParser.getAttribute(node, "scaleY")), Float.parseFloat(DOMParser.getAttribute(node,
                "scaleZ"))));
        eo.setRotation(new Quaternion(Float.parseFloat(DOMParser.getAttribute(node, "rotX")), Float.parseFloat(DOMParser.getAttribute(node, "rotY")), Float.parseFloat(DOMParser.getAttribute(node,
                "rotZ")), Float.parseFloat(DOMParser.getAttribute(node, "rotW"))));

        return eo;
    }

    /**
     * Loads default environments.
     */
    public void loadDefaultEnvironments()
    {
        EnvSpec envSpec = null;

        try
        {
            String defaultEnvironments[] = { "Default.xml", "ComplexRoom.xml", "Mavsville.xml", "RandomRoom1.xml", "RandomRoom2.xml" };

            for(String name : defaultEnvironments)
            {
                envSpec = loadToEnvSpec("environment/" + name, true);
                saveEnvironment(envFolder + System.getProperty("file.separator") + name, envSpec);
            }
        }
        catch(ClassNotFoundException | IOException e)
        {
            logger.error("An error occurred when loading default environments");
        }
    }
}
