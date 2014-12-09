package org.nhl.containing.communication;

import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses provided XML files and returns Containers.
 */
public class Xml {

    public static Message parseXmlMessage(String xmlMessage) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource source = new InputSource();
        source.setCharacterStream(new StringReader(xmlMessage));

        Document doc = db.parse(source);

        // Optional, but recommended.
        // Read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("id");
        if (nodes.getLength() != 1) {
            throw new Exception(xmlMessage + " is not a valid message");
        }

        Element line = (Element) nodes.item(0);
        int id = Integer.parseInt(Xml.getCharacterDataFromElement(line));

        // The following is trying to find, match and parse the correct message
        // type. There is probably a better way of achieving this, but this
        // appears to be the simplest way.
        // There don't seem to be any drawbacks by doing this, other than
        // ugly code.

        NodeList messageTypeNodes = doc.getElementsByTagName("Create");

        if (messageTypeNodes.getLength() > 0) {
            return parseCreateMessage(messageTypeNodes.item(0), id);
        }

        messageTypeNodes = doc.getElementsByTagName("Arrive");
        if (messageTypeNodes.getLength() > 0) {
            return parseArriveMessage(messageTypeNodes.item(0), id);
        }

        // etc etc etc. TODO

        throw new Exception("Could not find valid tag in " + xmlMessage);
    }

    private static CreateMessage parseCreateMessage(Node createNode, int id) {
        List<ContainerBean> containerBeans = new ArrayList<ContainerBean>();

        // There is only one child node, which is the transporter.
        Node transporterNode = createNode.getFirstChild();
        int identifier = Integer.parseInt(transporterNode.getAttributes().
                getNamedItem("identifier").getNodeValue());
        String type = transporterNode.getAttributes().getNamedItem("type").
                getNodeValue();

        NodeList transporterNodes = transporterNode.getChildNodes();

        for (int i = 0; i < transporterNodes.getLength(); i++) {
            Node node = transporterNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && node.getNodeName().equals("Container")) {
                containerBeans.add(parseContainerXml(node));
            }
        }
        return new CreateMessage(id, type, identifier, containerBeans);
    }

    private static ContainerBean parseContainerXml(Node containerNode) {
        ContainerBean containerBean = new ContainerBean();

        NodeList containerNodes = containerNode.getChildNodes();

        for (int i = 0; i < containerNodes.getLength(); i++) {
            Node node = containerNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String content = node.getTextContent();
                String nodeName = node.getNodeName();

                if (nodeName.equals("iso")) {
                    containerBean.setIso(content);
                } else if (nodeName.equals("owner")) {
                    containerBean.setOwner(content);
                } else if (nodeName.equals("xLoc")) {
                    containerBean.setxLoc(Integer.parseInt(content));
                } else if (nodeName.equals("yLoc")) {
                    containerBean.setyLoc(Integer.parseInt(content));
                } else if (nodeName.equals("zLoc")) {
                    containerBean.setzLoc(Integer.parseInt(content));
                }
            }
        }
        return containerBean;
    }

    private static ArriveMessage parseArriveMessage(Node arriveNode, int id) {
        return null;
    }

    /**
     * Gets the characterdata from the specified element
     */
    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
}