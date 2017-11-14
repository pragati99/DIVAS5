package edu.utdallas.mavs.divas.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is a helper class for parsing XML into a tree of Node.
 */
public class DOMParser
{
    private final static Logger logger = LoggerFactory.getLogger(DOMParser.class);

    /**
     * The {@link DOMParser} constructor.
     */
    public DOMParser()
    {}

    /**
     * Gets the root XML element from the given file.
     * 
     * @param file
     *        The XML file to be parsed.
     * @param inClassPath
     *        The file to be parsed in class path
     * @return
     *         The newly-created {@link Document} object.
     * @throws IOException 
     */
    public static Document parseDocument(String file, boolean inClassPath) throws IOException
    {

        Document document = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try
        {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            
            if(inClassPath)
            {
                InputStream resourceStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file);

                if(resourceStream != null)
                {
                    document = builder.parse(resourceStream);
                }
                else
                {
                    logger.error("Couldn't locate file: " + file + "\nMake sure the file is in the java classpath.");
                }
            }
            else
            {
                document = builder.parse(file);
            }
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch(SAXException e)
        {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * Retrieves an attribute value for a given node by Name.
     * 
     * @param node
     *        The node of the tree the attribute will be retrieved from.
     * @param name
     *        The name of the attribute to retrieve.
     * @return the attribute value.
     */
    public static String getAttribute(Node node, String name)
    {
        if(node instanceof Element)
        {
            Element element = (Element) node;
            // logger.debug(name + ": " +element.getAttribute(name));
            return element.getAttribute(name);
        }
        return null;
    }

    /**
     * Returns a child {@link Element} of the {@link Node} by Id.
     * 
     * @param node
     *        The node of the tree from where the Element will be retrieved from.
     * @param id
     *        The id of the element to retrieve.
     * @return
     *         the Element.
     */
    public static Element getElementById(Node node, String id)
    {
        return getElementById((Element) node, id);
    }

    /**
     * Returns a child {@link Element} of the given {@link Element} by Id
     * 
     * @param element
     *        An element from an XML document
     * @param id
     *        The id of the element to retrieve
     * @return Element
     */
    public static Element getElementById(Element element, String id)
    {
        NodeList nodeList = element.getChildNodes();
        // logger.debug("nodeList length: " +nodeList.getLength());
        for(int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            if(node.hasAttributes() && getAttribute(node, "id").equalsIgnoreCase(id))
            {
                // logger.debug("Node name: " +node.getNodeName());
                return (Element) node;
            }
        }
        return null;
    }

    /**
     * Returns a list of child nodes of the given {@link Node} with the
     * given name tag.
     * 
     * @param node
     *        The node of the tree from where the child node will be retrieved from.
     * @param name
     *        The name of the child node to retrieve.
     * @return
     *         List of child nodes.
     */
    public static List<Node> getElementsByTag(Node node, String name)
    {
        return getElementsByTag((Element) node, name);
    }

    /**
     * Returns a list of child nodes of the given {@link Element} with the
     * given name tag.
     * 
     * @param element
     *        The element of the tree from where the child node will be retrieved from.
     * @param name
     *        The name of the child node to retrieve.
     * @return
     *         List of child nodes.
     */
    public static List<Node> getElementsByTag(Element element, String name)
    {
        List<Node> elementList = new ArrayList<Node>();
        NodeList childNodes = element.getChildNodes();
        // logger.debug("nodeList length: " +childNodes.getLength());
        for(int i = 0; i < childNodes.getLength(); i++)
        {
            Node node = childNodes.item(i);
            if(node.hasAttributes() && (node.getNodeName()).equalsIgnoreCase(name))
            {
                // logger.debug("Node name: " +node.getNodeName());
                elementList.add(node);
            }
        }
        return elementList;
    }

    /**
     * Returns a list of child elements of the given {@link Node} with the
     * given name and attribute.
     * 
     * @param node
     *        The node of the tree from where the child node will be retrieved from.
     * @param name
     *        The name of the child node to retrieve.
     * @param attribute
     *        The attribute of the child node to retrieve.
     * @return List of attributes of the child elements.
     */
    public static List<String> getChildElementsAttributesByTag(Node node, String name, String attribute)
    {
        return getChildElementsAttributesByTag((Element) node, name, attribute);
    }

    /**
     * Returns a list of child elements of the given {@link Element} with the
     * given name and attribute.
     * 
     * @param element
     *        The element of the tree from where the child node will be retrieved from.
     * @param name
     *        The name of the child node to retrieve.
     * @param attribute
     *        The attribute of the child node to retrieve.
     * @return List of attributes of the child elements.
     */
    public static List<String> getChildElementsAttributesByTag(Element element, String name, String attribute)
    {
        List<String> childElementStrings = new ArrayList<String>();
        List<Node> nodeList = getElementsByTag(element, name);
        for(Node node : nodeList)
        {
            childElementStrings.add(getAttribute(node, attribute));
        }
        return childElementStrings;
    }
}
