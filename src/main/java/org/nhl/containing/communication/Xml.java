package org.nhl.containing.communication;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses provided XML files and returns Containers.
 */
public class Xml {

    /**
     * Parse an XML instruction from the backend, and represent it as program-
     * readable Message object.
     *
     * @param xmlMessage An XML instruction as defined in the project's XML
     * protocol.
     * @return (Mostly) one-on-one conversion towards an instance of Message.
     */
    public static Message parseXmlMessage(String xmlMessage) throws
            ParserConfigurationException, SAXException, IOException {
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
            throw new IllegalArgumentException(xmlMessage + " is not a valid message");
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

        throw new IllegalArgumentException("Could not find valid tag in " + xmlMessage);
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

                switch (node.getNodeName()) {
                    case "iso":
                        containerBean.setIso(content);
                        break;
                    case "owner":
                        containerBean.setOwner(content);
                        break;
                    case "xLoc":
                        containerBean.setxLoc(Integer.parseInt(content));
                        break;
                    case "yLoc":
                        containerBean.setyLoc(Integer.parseInt(content));
                        break;
                    case "zLoc":
                        containerBean.setzLoc(Integer.parseInt(content));
                        break;
                }
            }
        }
        return containerBean;
    }

    private static ArriveMessage parseArriveMessage(Node arriveNode, int id) {
        int transporterId = -1;
        int depotIndex = -1;

        NodeList arriveNodes = arriveNode.getChildNodes();

        for (int i = 0; i < arriveNodes.getLength(); i++) {
            Node node = arriveNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String content = node.getTextContent();

                switch (node.getNodeName()) {
                    case "transporterId":
                        transporterId = Integer.parseInt(content);
                        break;
                    case "depotIndex":
                        depotIndex = Integer.parseInt(content);
                        break;
                }
            }
        }
        return new ArriveMessage(id, transporterId, depotIndex);
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